import React, { useRef, useEffect, useState, useMemo } from 'react';
import { Pressable, StyleSheet, Text, View, Dimensions, Alert, TouchableOpacity, Image, ScrollView, ActivityIndicator } from 'react-native';
import Iconicons from 'react-native-vector-icons/Ionicons';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';
import MapView, { PROVIDER_GOOGLE, Callout, LatLng, UserLocationChangeEvent } from 'react-native-maps';
import { colors } from '@/constants';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { CompositeNavigationProp, useNavigation } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { DrawerNavigationProp } from '@react-navigation/drawer';
import { MapStackParamList } from '@/navigations/stack/MapStackNavigator';
import { MainDrawerParamList } from '@/navigations/drawer/MainDrawerNavigator';
import useUserLocation from '@/hooks/useUserLocation';
import usePermission from '@/hooks/usePermission';
import mapStyle from '@/style/mapStyle';
import useNearbyAnimalHospitalsData from '@/hooks/useNearbyAnimalHospitals';
import { AnimalHospital } from '@/api/places';
import { calculateDistance } from '@/utils/calculateDistance';
import BottomSheet, { BottomSheetFlatList, BottomSheetScrollView } from '@gorhom/bottom-sheet';
import Animated, {
  useSharedValue,
  useAnimatedStyle,
  interpolate,
} from 'react-native-reanimated';
import { Marker as MapMarker } from 'react-native-maps';
import { getEncryptStorage } from '@/utils/encryptStorage';
import useThemeStore from '@/store/useThemeStore';
import {ThemeMode} from '@/types/common';

type Navigation = CompositeNavigationProp<
  StackNavigationProp<MapStackParamList>,
  DrawerNavigationProp<MainDrawerParamList>
>;

type MapMarkerRef = InstanceType<typeof MapMarker>;

function MapHomeScreen() {
  const inset = useSafeAreaInsets();
  const navigation = useNavigation<Navigation>();
  const mapRef = useRef<MapView | null>(null);
  const { userLocation, isUserLocationError, updateUserLocation } = useUserLocation();

  // 알림 권한 요청
  usePermission('NOTIFICATION');
  // 위치 권한 요청
  usePermission('LOCATION');

  const [selectedType, setSelectedType] = useState<'all' | 'hospital' | 'pharmacy'>('all');
  const { data: hospitals, isLoading, isError } = useNearbyAnimalHospitalsData(selectedType);
  const [sortedHospitals, setSortedHospitals] = useState<AnimalHospital[]>([]);
  const [selectedHospitalId, setSelectedHospitalId] = useState<string | null>(null);
  const bottomSheetRef = useRef<BottomSheet>(null);
  const snapPoints = useMemo(() => ['20%','23%','26%','29%','32%','35%','38%','41%','44%','47%','50%','53%','56%','59%','62%','65%','68%', '71%', '74%', '77%', '80%'], []);
  const bottomSheetHeight = useSharedValue(0);
  const windowHeight = Dimensions.get('window').height;
  const markerRefs = useRef<{ [key: string]: MapMarkerRef | null }>({});

  const [mapRegion, setMapRegion] = useState({
    latitude: userLocation.latitude,
    longitude: userLocation.longitude,
    latitudeDelta: 0.05,
    longitudeDelta: 0.05,
  });

  const [isUserLocationPressed, setIsUserLocationPressed] = useState(false);
  const [isMapManuallyMoved, setIsMapManuallyMoved] = useState(false);

  const {theme} = useThemeStore();
  const styles = styling(theme);

  const [selectedHospital, setSelectedHospital] = useState<AnimalHospital | null>(null);

  useEffect(() => {
    const animateToSavedLocation = async () => {
      const savedLocation = await getEncryptStorage('userLocation');
      if (savedLocation) {
        const { latitude, longitude } = savedLocation;
        const { height } = Dimensions.get('window');
        const offset = calculateOffset(height / 4);
        animateToLocation(latitude, longitude, offset);
        setIsUserLocationPressed(true);
      } else {
        setMapRegion({
          latitude: userLocation.latitude,
          longitude: userLocation.longitude,
          latitudeDelta: 0.05,
          longitudeDelta: 0.05,
        });
      }
    };

    animateToSavedLocation();
  }, []);

  useEffect(() => {
    if (hospitals) {
      const hospitalsWithDistance = hospitals.map(hospital => ({
        ...hospital,
        distance: calculateDistance(
          userLocation.latitude,
          userLocation.longitude,
          hospital.latitude,
          hospital.longitude
        ),
      }));
      const sorted = hospitalsWithDistance.sort((a, b) => a.distance - b.distance);
      setSortedHospitals(sorted);
    }
  }, [hospitals, userLocation]);

  const calculateOffset = (height: number) => {
    return (height / 2) / 111111;
  };

  const animateToLocation = (latitude: number, longitude: number, offset: number) => {
    mapRef.current?.animateCamera({
      center: {
        latitude: latitude - offset, 
        longitude,
      },
      zoom: 15,
    }, { duration: 300 });
  };

  const handlePressUserLocation = () => {
    if (isUserLocationError) {
      Alert.alert('위치 오류', '현재 위를 가져올 수 없습니다. 위치 서비스가 활성화되어 있는지 확인해주세요.');
      return;
    }

    setSelectedHospital(null);
    setSelectedHospitalId(null);
    
    Object.values(markerRefs.current).forEach(marker => {
      marker?.hideCallout();
    });

    updateUserLocation((location) => {
      const { height: windowHeight } = Dimensions.get('window');
      const baseOffset = calculateOffset(windowHeight / 4);
      const currentHeight = bottomSheetHeight.value;
      const sheetOffset = (currentHeight / windowHeight) * 0.01;
      const totalOffset = baseOffset + sheetOffset;

      animateToLocation(location.latitude, location.longitude, totalOffset);

      bottomSheetRef.current?.collapse();
      setIsUserLocationPressed(true);
      setIsMapManuallyMoved(false);
    });
  };

  const handleHospitalPress = (hospital: AnimalHospital) => {
    setSelectedHospital(hospital);
    setSelectedHospitalId(hospital.id);
    
    if (selectedHospitalId && markerRefs.current[selectedHospitalId]) {
      markerRefs.current[selectedHospitalId]?.hideCallout();
    }
    
    if (markerRefs.current[hospital.id]) {
      markerRefs.current[hospital.id]?.showCallout();
    }
    
    // 바텀시트의 현재 높이를 가져와서 지도 중심 조정
    const currentHeight = bottomSheetHeight.value;
    const { height: windowHeight } = Dimensions.get('window');
    const baseOffset = calculateOffset(windowHeight / 4);
    const sheetOffset = (currentHeight / windowHeight) * 0.01;
    const totalOffset = baseOffset + sheetOffset;

    mapRef.current?.animateCamera({
      center: {
        latitude: hospital.latitude - totalOffset,
        longitude: hospital.longitude,
      },
      zoom: 15,
    }, { duration: 300 });
    
    setIsUserLocationPressed(false);
    setIsMapManuallyMoved(false);
    bottomSheetRef.current?.expand();
  };

  const renderHospitalItem = ({ item, index }: { item: AnimalHospital; index: number }) => {
    const isSelected = selectedHospitalId === item.id;
    
    return (
      <Pressable 
        key={`${item.id}-${item.type}`}
        onPress={() => handleHospitalPress(item)} 
        style={({ pressed }) => [
          styles.hospitalItem,
          isSelected && styles.selectedHospitalItem,
          pressed && styles.pressedHospitalItem
        ]}
      >
        <Text style={[
          styles.hospitalName,
          isSelected && styles.selectedHospitalText
        ]}>
          {index + 1}. {item.name}
        </Text>
        <Text style={[
          styles.hospitalAddress,
          isSelected && styles.selectedHospitalText
        ]}>{item.address}</Text>
        <Text style={[
          styles.hospitalDistance,
          isSelected && styles.selectedHospitalText
        ]}>{item.distance.toFixed(2)} km</Text>
      </Pressable>
    );
  };

  const animatedButtonStyle = useAnimatedStyle(() => {
    return {
      bottom: interpolate(
        bottomSheetHeight.value,
        [0, windowHeight * 0.5],
        [30, windowHeight * 0.5 + 30]
      ),
    };
  });

  const handleSheetChange = (index: number) => {
    const currentSnap = snapPoints[index];
    const height = windowHeight * parseFloat(currentSnap) / 100;
    bottomSheetHeight.value = height;
    
    adjustMapCenter(height);
  };

  const handleUserLocationChange = (event: UserLocationChangeEvent) => {
    const { coordinate } = event.nativeEvent;
    if (coordinate) {
      setMapRegion({
        ...mapRegion,
        latitude: coordinate.latitude,
        longitude: coordinate.longitude,
      });
    }
  };

  const handleMapMove = () => {
    if (isUserLocationPressed) {
      setIsMapManuallyMoved(true);
    }
  };

  const adjustMapCenter = async (sheetHeight: number) => {
    if (!isMapManuallyMoved) {
      const { height: windowHeight } = Dimensions.get('window');
      const baseOffset = calculateOffset(windowHeight / 4);
      const sheetOffset = (sheetHeight / windowHeight) * 0.01;
      const totalOffset = baseOffset + sheetOffset;

      if (selectedHospital) {
        mapRef.current?.animateCamera({
          center: {
            latitude: selectedHospital.latitude - totalOffset,
            longitude: selectedHospital.longitude,
          },
          zoom: 15,
        }, { duration: 300 });
      } else if (isUserLocationPressed) {
        const { latitude, longitude } = userLocation;
        animateToLocation(latitude, longitude, totalOffset);
      }
    }
  };

  return (
    <>
      <MapView
        ref={mapRef}
        style={StyleSheet.absoluteFillObject}
        provider={PROVIDER_GOOGLE}
        customMapStyle={mapStyle[theme]}
        userInterfaceStyle={theme === 'dark' ? 'dark' : 'light'}
        showsUserLocation
        showsMyLocationButton={false}
        initialRegion={mapRegion}
        onPanDrag={handleMapMove}
      >
        {sortedHospitals.map(hospital => (
          <MapMarker
            key={`${hospital.id}-${hospital.type}`}
            ref={(ref) => {
              markerRefs.current[hospital.id] = ref;
            }}
            coordinate={{
              latitude: hospital.latitude,
              longitude: hospital.longitude,
            }}
            onPress={() => handleHospitalPress(hospital)}
          >
            <Callout tooltip>
              <View style={styles.callout}>
                <Text style={styles.calloutTitle}>{hospital.name}</Text>
                <Text style={styles.calloutText}>{hospital.address}</Text>
              </View>
            </Callout>
          </MapMarker>
        ))}
      </MapView>
      <Pressable
        style={({pressed}) => [
          styles.drawerButton,
          { top: inset.top || 20 },
          pressed && {
            backgroundColor: colors[theme].PINK_500,
            transform: [{ scale: 0.98 }]
          }
        ]}
        onPress={() => navigation.openDrawer()}
      >
        <Iconicons name='menu' color={colors[theme].WHITE} size={29} />
      </Pressable>

      <Animated.View style={[styles.animatedButtonContainer, animatedButtonStyle]}>
        <Pressable 
          style={({pressed}) => [
            styles.mapButton,
            pressed && {
              backgroundColor: colors[theme].PINK_500,
              transform: [{ scale: 0.98 }]
            }
          ]} 
          onPress={handlePressUserLocation}
        >
          <MaterialIcons name='my-location' color={colors[theme].UNCHANGE_WHITE} size={25} />
        </Pressable>
      </Animated.View>

      {/* Bottom Sheet */}
      <BottomSheet
        ref={bottomSheetRef}
        index={0}
        snapPoints={snapPoints}
        onChange={handleSheetChange}
      >
        <View style={styles.bottomSheetHeader}>
          <View style={styles.typeButtons}>
            <TouchableOpacity 
              style={[
                styles.typeButton, 
                selectedType === 'all' && styles.typeButtonActive
              ]}
              onPress={() => {
                setSelectedType('all');
                setSelectedHospital(null);
                setSelectedHospitalId(null);
              }}
              disabled={isLoading}
            >
              <Text style={[
                styles.typeButtonText,
                selectedType === 'all' && styles.typeButtonTextActive
              ]}>
                전체
              </Text>
            </TouchableOpacity>
            
            <TouchableOpacity 
              style={[
                styles.typeButton, 
                selectedType === 'hospital' && styles.typeButtonActive
              ]}
              onPress={() => {
                setSelectedType('hospital');
                setSelectedHospital(null);
                setSelectedHospitalId(null);
              }}
              disabled={isLoading}
            >
              <Text style={[
                styles.typeButtonText,
                selectedType === 'hospital' && styles.typeButtonTextActive
              ]}>
                동물병원
              </Text>
            </TouchableOpacity>

            <TouchableOpacity 
              style={[
                styles.typeButton, 
                selectedType === 'pharmacy' && styles.typeButtonActive
              ]}
              onPress={() => {
                setSelectedType('pharmacy');
                setSelectedHospital(null);
                setSelectedHospitalId(null);
              }}
              disabled={isLoading}
            >
              <Text style={[
                styles.typeButtonText,
                selectedType === 'pharmacy' && styles.typeButtonTextActive
              ]}>
                동물약국
              </Text>
            </TouchableOpacity>
          </View>
        </View>

        {selectedHospital ? (
          <BottomSheetScrollView 
            style={styles.hospitalDetailContainer}
            contentContainerStyle={styles.hospitalDetailContent}
            showsVerticalScrollIndicator={true}
          >
            <View style={styles.detailHeader}>
              <Text style={styles.detailTitle}>상세 정보</Text>
              <TouchableOpacity 
                style={styles.closeButton}
                onPress={() => {
                  setSelectedHospital(null);
                  setSelectedHospitalId(null);
                }}
              >
                <MaterialIcons name="close" size={24} color={colors[theme].GRAY_500} />
              </TouchableOpacity>
            </View>
            {selectedHospital.photos && selectedHospital.photos.length > 0 && (
              <View style={styles.hospitalImageContainer}>
                <Image 
                  source={{ uri: selectedHospital.photos[0] }}
                  style={styles.hospitalImage}
                  resizeMode="cover"
                />
              </View>
            )}
            <View style={styles.headerSection}>
              <Text style={styles.hospitalDetailName}>{selectedHospital.name}</Text>
              <View style={[
                styles.openStatusBadge, 
                selectedHospital.isOpen ? styles.openBadge : styles.closedBadge
              ]}>
                <Text style={styles.openStatusText}>
                  {selectedHospital.isOpen ? '영업중' : '영업종료'}
                </Text>
              </View>
            </View>

            <View style={styles.infoSection}>
              <View style={styles.infoRow}>
                <MaterialIcons name="location-on" size={20} color={colors[theme].GRAY_500} />
                <Text style={styles.infoText}>{selectedHospital.address}</Text>
              </View>
              {selectedHospital.phoneNumber && (
                <View style={styles.infoRow}>
                  <MaterialIcons name="phone" size={20} color={colors[theme].GRAY_500} />
                  <Text style={styles.infoText}>{selectedHospital.phoneNumber}</Text>
                </View>
              )}
            </View>

            {selectedHospital.openingHours && (
              <View style={styles.openingHoursSection}>
                <Text style={styles.sectionTitle}>영업시간</Text>
                {selectedHospital.openingHours.map((hours, index) => (
                  <Text key={index} style={styles.openingHoursText}>{hours}</Text>
                ))}
              </View>
            )}

            {selectedHospital.rating && (
              <View style={styles.ratingSection}>
                <Text style={styles.sectionTitle}>평점</Text>
                <View style={styles.ratingContainer}>
                  {[1, 2, 3, 4, 5].map((star) => (
                    <MaterialIcons
                      key={star}
                      name={star <= selectedHospital.rating! ? "star" : "star-border"}
                      size={24}
                      color={colors[theme].YELLOW_500}
                    />
                  ))}
                  <Text style={styles.ratingText}>{selectedHospital.rating.toFixed(1)}</Text>
                </View>
              </View>
            )}

            {selectedHospital.reviews && selectedHospital.reviews.length > 0 && (
              <View style={styles.reviewsSection}>
                <Text style={styles.sectionTitle}>리뷰</Text>
                {selectedHospital.reviews.map((review, index) => (
                  <View key={index} style={styles.reviewItem}>
                    <View style={styles.reviewHeader}>
                      <Text style={styles.reviewAuthor}>{review.author_name}</Text>
                      <View style={styles.reviewRating}>
                        {[1, 2, 3, 4, 5].map((star) => (
                          <MaterialIcons
                            key={star}
                            name={star <= review.rating ? "star" : "star-border"}
                            size={16}
                            color={colors[theme].YELLOW_500}
                          />
                        ))}
                      </View>
                    </View>
                    <Text style={styles.reviewText}>{review.text}</Text>
                  </View>
                ))}
              </View>
            )}
          </BottomSheetScrollView>
        ) : (
          <View style={styles.listContainer}>
            {isLoading ? (
              <View style={[
                styles.loaderContainer,
                {
                  paddingTop: bottomSheetHeight.value * 0.2,
                  paddingBottom: bottomSheetHeight.value * 0.2,
                }
              ]}>
                <ActivityIndicator size="large" color={colors[theme].PINK_700} />
                <Text style={styles.loadingText}>주변 검색중...</Text>
              </View>
            ) : (
              <BottomSheetFlatList
                data={sortedHospitals}
                keyExtractor={(item) => item.id}
                renderItem={renderHospitalItem}
                contentContainerStyle={styles.flatListContent}
              />
            )}
          </View>
        )}
      </BottomSheet>
    </>
  );
}

const styling = (theme: ThemeMode) =>
  StyleSheet.create({
    container: {
      flex: 1,
    },
    drawerButton: {
      position: 'absolute',
      left: 10,
      paddingVertical: 10,
      paddingHorizontal: 10,
      backgroundColor: colors[theme].PINK_700,
      borderRadius: 15,
      flexDirection: 'row',
      alignItems: 'center',
      gap: 8,
      shadowColor: colors[theme].BLACK,
      shadowOffset: {
        width: 0,
        height: 2 ,
      },
      shadowOpacity: 0.1,
      shadowRadius: 8,
      elevation: 5,
    },
    mapButton: {
      backgroundColor: colors[theme].PINK_700,
      height: 48,
      width: 48,
      alignItems: 'center',
      justifyContent: 'center',
      borderRadius: 24,
      shadowColor: 'black',
      shadowOffset: {
        width: 1,
        height: 2,
      },
      shadowOpacity: 0.5,
      elevation: 2,
    },
    bottomSheet: {
      backgroundColor: colors[theme].WHITE,
      borderTopLeftRadius: 20,
      borderTopRightRadius: 20,
      shadowColor: '#000',
      shadowOffset: {
        width: 0,
        height: -3,
      },
      shadowOpacity: 0.25,
      shadowRadius: 3,
      elevation: 24,
    },
    flatListContent: {
      paddingTop: 5,
      backgroundColor: colors[theme].WHITE,
    },
    hospitalItem: {
      padding: 12,
      paddingHorizontal: 25,
      borderBottomWidth: 1,
      borderBottomColor: colors[theme].GRAY_200,
      backgroundColor: colors[theme].WHITE,
    },
    hospitalName: {
      fontSize: 16,
      fontWeight: 'bold',
      color: colors[theme].BLACK,
      marginBottom: 4, 
    },
    hospitalAddress: {
      fontSize: 14,
      color: colors[theme].GRAY_700,
    },
    hospitalDistance: {
      fontSize: 12, 
      color: colors[theme].GRAY_500,
    },
    animatedButtonContainer: {
      position: 'absolute',
      right: 15,
      // bottom은 애니메이션로 조정
    },
    callout: {
      width: 250,
      padding: 10,
      backgroundColor: colors[theme].WHITE,
      borderRadius: 5,
      borderWidth: 1,
      borderColor: colors[theme].GRAY_500,
    },
    calloutTitle: {
      fontWeight: 'bold',
      fontSize: 16,
      marginBottom: 5,
      color: colors[theme].BLACK,
    },
    calloutSubtitle: {
      fontWeight: 'bold',
      fontSize: 14,
      marginBottom: 5,
      color: colors[theme].BLACK,
    },
    calloutText: {
      fontSize: 12,
      color: colors[theme].GRAY_700,
      marginBottom: 3,
    },
    openingHoursContainer: {
      marginTop: 10,
    },
    bottomSheetHeader: {
      alignItems: 'center',
      paddingVertical: 5,
      paddingHorizontal: 20,
      backgroundColor: colors[theme].WHITE,
    },
    bottomSheetHeaderText: {
      fontSize: 14,
      color: colors[theme].UNCHANGE_WHITE,
      fontWeight: 'bold',
    },
    bottomSheetHeaderCircle: {
      backgroundColor: colors[theme].PINK_700,
      borderRadius: 10,
      paddingVertical: 7,
      paddingHorizontal: 10,
      alignSelf: 'flex-start',
      justifyContent: 'center',
      alignItems: 'center',
    },
    bottomSheetIndicator: {
      alignSelf: 'center',
      width: 40,
      height: 4,
      borderRadius: 2,
      marginTop: -5,
      marginBottom: 4,
      backgroundColor: colors[theme].GRAY_500,
    },
    selectedHospitalItem: {
      backgroundColor: colors[theme].PINK_50,
      borderLeftWidth: 4,
      borderLeftColor: colors[theme].PINK_700,
    },
    pressedHospitalItem: {
      opacity: 0.7,
      backgroundColor: colors[theme].GRAY_100,
    },
    selectedHospitalText: {
      color: colors[theme].PINK_700,
    },
    typeButtons: {
      flexDirection: 'row',
      backgroundColor: colors[theme].GRAY_50,
      borderRadius: 20,
      padding: 4,
      marginBottom: 10,
      alignSelf: 'flex-start',
      borderWidth: 1,
      borderColor: colors[theme].GRAY_200,
      marginLeft: 1,
    },
    typeButton: {
      paddingVertical: 6,
      paddingHorizontal: 12,
      borderRadius: 16,
    },
    typeButtonActive: {
      backgroundColor: colors[theme].PINK_700,
    },
    typeButtonText: {
      fontSize: 13,
      color: colors[theme].GRAY_500,
      fontWeight: '500',
    },
    typeButtonTextActive: {
      color: colors[theme].WHITE,
      fontWeight: '500',
    },
    hospitalDetailContainer: {
      flex: 1,
    },
    hospitalDetailContent: {
      paddingBottom: 20,
    },
    imageContainer: {
      width: '100%',
      height: 200,
    },
    hospitalImage: {
      width: '100%',
      height: '100%',
      resizeMode: 'cover',
    },
    headerSection: {
      padding: 16,
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      borderBottomWidth: 1,
      borderBottomColor: colors[theme].GRAY_200,
    },
    hospitalDetailName: {
      fontSize: 20,
      fontWeight: 'bold',
      color: colors[theme].BLACK,
      flex: 1,
    },
    openStatusBadge: {
      paddingHorizontal: 8,
      paddingVertical: 4,
      borderRadius: 12,
      marginLeft: 8,
    },
    openBadge: {
      backgroundColor: colors[theme].GREEN_50,
    },
    closedBadge: {
      backgroundColor: colors[theme].RED_50,
    },
    openStatusText: {
      fontSize: 12,
      fontWeight: '500',
      color: colors[theme].GREEN_700,
    },
    closedStatusText: {
      color: colors[theme].RED_700,
    },
    infoSection: {
      padding: 16,
      gap: 12,
      borderBottomWidth: 1,
      borderBottomColor: colors[theme].GRAY_200,
    },
    infoRow: {
      flexDirection: 'row',
      alignItems: 'center',
      gap: 8,
    },
    infoText: {
      fontSize: 15,
      color: colors[theme].GRAY_700,
      flex: 1,
    },
    openingHoursSection: {
      padding: 16,
      borderBottomWidth: 1,
      borderBottomColor: colors[theme].GRAY_200,
    },
    sectionTitle: {
      fontSize: 16,
      fontWeight: 'bold',
      color: colors[theme].BLACK,
      marginBottom: 8,
    },
    openingHoursText: {
      fontSize: 14,
      color: colors[theme].GRAY_600,
      marginBottom: 4,
    },
    ratingSection: {
      padding: 16,
      borderBottomWidth: 1,
      borderBottomColor: colors[theme].GRAY_200,
    },
    ratingContainer: {
      flexDirection: 'row',
      alignItems: 'center',
      gap: 4,
    },
    ratingText: {
      fontSize: 16,
      color: colors[theme].BLACK,
      marginLeft: 8,
    },
    reviewsSection: {
      padding: 16,
    },
    reviewItem: {
      marginBottom: 16,
      padding: 12,
      backgroundColor: colors[theme].GRAY_50,
      borderRadius: 8,
    },
    reviewHeader: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      marginBottom: 8,
    },
    reviewAuthor: {
      fontSize: 14,
      fontWeight: '500',
      color: colors[theme].BLACK,
    },
    reviewRating: {
      flexDirection: 'row',
      gap: 2,
    },
    reviewText: {
      fontSize: 14,
      color: colors[theme].GRAY_700,
      lineHeight: 20,
    },
    detailHeader: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      padding: 16,
      borderBottomWidth: 1,
      borderBottomColor: colors[theme].GRAY_200,
    },
    detailTitle: {
      fontSize: 18,
      fontWeight: '600',
      color: colors[theme].BLACK,
    },
    closeButton: {
      padding: 8,
      borderRadius: 20,
    },
    hospitalImageContainer: {
      width: '100%',
      height: 200,
      backgroundColor: colors[theme].GRAY_50,
    },
    loaderContainer: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
    },
    loadingText: {
      marginTop: 12,
      fontSize: 15,
      color: colors[theme].GRAY_600,
      fontWeight: '500',
    },
    listContainer: {
      flex: 1,
      backgroundColor: colors[theme].WHITE,
    },
  });

export default MapHomeScreen;

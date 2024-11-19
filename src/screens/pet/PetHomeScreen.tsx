import React from 'react';
import {View, StyleSheet, FlatList, TouchableOpacity, Text, Alert, Image, ScrollView, Platform} from 'react-native';
import {useQuery, useMutation, useQueryClient} from '@tanstack/react-query';
import {getPets, deletePet} from '@/api/pet';
import {colors, petNavigations} from '@/constants';
import useThemeStore, { ThemeMode } from '@/store/useThemeStore';
import Icon from 'react-native-vector-icons/Ionicons';
import {CompoundOption} from '@/components/common/CompoundOption';
import useModal from '@/hooks/useModal';
import {StackScreenProps} from '@react-navigation/stack';
import {PetStackParamList} from '@/navigations/stack/PetStackNavigator';
import EncryptedStorage from 'react-native-encrypted-storage';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { getAdoptionDate, Pet } from '@/types/pet';
import LinearGradient from 'react-native-linear-gradient';
import { calculateDaysFromAdoption } from '@/utils/pet';
import { getEncryptStorage, setEncryptStorage } from '@/utils/encryptStorage';

type PetHomeScreenProps = StackScreenProps<
  PetStackParamList,
  typeof petNavigations.PET_HOME
>;

function PetHomeScreen({navigation}: PetHomeScreenProps) {
  const {theme} = useThemeStore();
  const styles = styling(theme);
  const queryClient = useQueryClient();
  const deleteModal = useModal();
  const [selectedPet, setSelectedPet] = React.useState<string | null>(null);
  const [adoptionDates, setAdoptionDates] = React.useState<{[key: string]: Date}>({});

  const {data: pets} = useQuery<Pet[]>({
    queryKey: ['pets'],
    queryFn: getPets,
  });

  const deleteMutation = useMutation({
    mutationFn: deletePet,
    onSuccess: () => {
      queryClient.invalidateQueries({queryKey: ['pets']});
    },
  });

  const handleAddPet = () => {
    navigation.navigate(petNavigations.PET_ADD);
  };

  const handleEditPet = (petName: string) => {
    navigation.navigate(petNavigations.PET_EDIT, {petName});
  };

  const handleDeletePet = async (petName: string) => {
    setSelectedPet(petName);
    
    try {
      await deleteMutation.mutateAsync(petName);
      // 입양일 정보 삭제
      await EncryptedStorage.removeItem(`adoption_date_${petName}`);
      // 건강검진 기록 삭제
      await EncryptedStorage.removeItem(`health_checkups_${petName}`);
      // 예방접종 기록 삭제
      await EncryptedStorage.removeItem(`vaccinations_${petName}`);
      // 질병 기록 삭제
      await EncryptedStorage.removeItem(`medical_history_${petName}`);
      // 캘린더 일정에서 해당 반려동물 관련 일정 삭제
      const schedules = await getEncryptStorage('schedules') || {};
      const newSchedules: {[key: string]: any[]} = {};
      
      Object.keys(schedules).forEach(dateKey => {
        const filteredSchedules = schedules[dateKey].filter(
          (schedule: any) => !schedule.content.includes(petName)
        );
        
        if (filteredSchedules.length > 0) {
          newSchedules[dateKey] = filteredSchedules;
        }
      });
      
      await setEncryptStorage('schedules', newSchedules);
      deleteModal.hide();
      
    } catch (error) {
      console.error('Failed to delete pet:', error);
      Alert.alert('오류', '반려동물 삭제에 실패했습니다.');
    }
  };

  const confirmDelete = () => {
    if (selectedPet) {
      deleteMutation.mutate(selectedPet);
      deleteModal.hide();
    }
  };

  const handleHealthManagement = (petName: string) => {
    navigation.navigate(petNavigations.PET_HEALTH, {petName});
  };

  React.useEffect(() => {
    const loadAdoptionDates = async () => {
      const dates: {[key: string]: Date} = {};
      for (const pet of pets || []) {
        const savedDate = await getAdoptionDate(pet.petName);
        if (savedDate) {
          dates[pet.petName] = savedDate;
        }
      }
      setAdoptionDates(dates);
    };
    
    loadAdoptionDates();
  }, [pets]);

  return (
    <View style={styles.container}>
      <ScrollView>
        <View style={styles.header}>
          <Image 
            source={require('@/assets/images/pet-home-banner.png')}
            style={styles.banner}
            resizeMode="cover"
          />
          <LinearGradient
            colors={['transparent', 'rgba(0,0,0,0.7)']}
            style={styles.gradient}>
            <View style={styles.headerContent}>
              <Text style={styles.headerTitle}>반려동물 관리</Text>
              <Text style={styles.headerSubtitle}>
                소중한 가족의{'\n'}건강한 일상을 관리하세요
              </Text>
            </View>
          </LinearGradient>
        </View>

        <View style={styles.petsContainer}>
          {pets?.map((pet) => (
            <TouchableOpacity 
              key={pet.petName}
              style={styles.petCard}
              onPress={() => handleHealthManagement(pet.petName)}
            >
              <Image
                source={require('@/assets/images/pet-profile.png')}
                style={styles.petImage}
                resizeMode="cover"
              />
              <LinearGradient
                colors={['transparent', 'rgba(0,0,0,0.7)']}
                style={styles.gradient}>
                <View style={styles.petContent}>
                  <View style={styles.contentContainer}>
                    {adoptionDates[pet.petName] && (
                      <Text style={styles.adoptionText}>
                        {pet.petName}와 함께한지 +{calculateDaysFromAdoption(adoptionDates[pet.petName])}일
                      </Text>
                    )}
                    <View style={styles.petInfo}>
                      <Text style={styles.petName}>{pet.petName}</Text>
                      <Text style={styles.petDetails}>
                        {pet.petBreed} • {pet.petAge}세 • {pet.petGender}
                      </Text>
                    </View>
                  </View>
                  <View style={styles.petActions}>
                    <TouchableOpacity 
                      style={styles.actionButton}
                      onPress={() => handleEditPet(pet.petName)}
                    >
                      <Ionicons name="create-outline" size={20} color={colors[theme].WHITE} />
                    </TouchableOpacity>
                    <TouchableOpacity 
                      style={[styles.actionButton, styles.deleteButton]}
                      onPress={() => {
                        setSelectedPet(pet.petName);
                        deleteModal.show();
                      }}
                    >
                      <Ionicons name="trash-outline" size={20} color={colors[theme].WHITE} />
                    </TouchableOpacity>
                  </View>
                </View>
              </LinearGradient>
            </TouchableOpacity>
          ))}
          <TouchableOpacity
            style={styles.addPetCard}
            onPress={handleAddPet}>
            <View style={styles.addPetContent}>
              <View style={styles.addIconContainer}>
                <Ionicons name="add-circle" size={48} color={colors[theme].PINK_500} />
              </View>
              <Text style={styles.addPetText}>새로운 반려동물 추가</Text>
            </View>
          </TouchableOpacity>
        </View>
      </ScrollView>

      <CompoundOption isVisible={deleteModal.isVisible} hideOption={deleteModal.hide}>
        <CompoundOption.Background>
          <CompoundOption.Container>
            <View style={styles.deleteConfirmContainer}>
              <Text style={styles.deleteConfirmTitle}>반려동물 삭제</Text>
              <Text style={styles.deleteConfirmMessage}>
                정말 반려동물을 삭제하시겠습니까?
              </Text>
            </View>
            <CompoundOption.Button onPress={confirmDelete} isDanger>
              삭제
            </CompoundOption.Button>
            <CompoundOption.Divider />
            <CompoundOption.Button onPress={deleteModal.hide}>
              취소
            </CompoundOption.Button>
          </CompoundOption.Container>
        </CompoundOption.Background>
      </CompoundOption>
    </View>
  );
}

const styling = (theme: ThemeMode) =>
  StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: colors[theme].WHITE,
    },
    header: {
      height: 240,
    },
    banner: {
      width: '100%',
      height: '100%',
    },
    headerContent: {
      position: 'absolute',
      bottom: 24,
      left: 24,
    },
    headerTitle: {
      fontSize: 28,
      fontWeight: 'bold',
      color: colors[theme].WHITE,
      marginBottom: 8,
      textShadowColor: 'rgba(0, 0, 0, 0.3)',
      textShadowOffset: {width: 0, height: 1},
      textShadowRadius: 4,
    },
    headerSubtitle: {
      fontSize: 16,
      color: colors[theme].WHITE,
      lineHeight: 22,
      textShadowColor: 'rgba(0, 0, 0, 0.3)',
      textShadowOffset: {width: 0, height: 1},
      textShadowRadius: 4,
    },
    gradient: {
      position: 'absolute',
      left: 0,
      right: 0,
      bottom: 0,
      height: '50%',
      justifyContent: 'flex-end',
    },
    petsContainer: {
      padding: 16,
      gap: 16,
      paddingBottom: 32,
    },
    petCard: {
      height: 200,
      borderRadius: 16,
      overflow: 'hidden',
      backgroundColor: colors[theme].GRAY_100,
      ...Platform.select({
        ios: {
          shadowColor: colors[theme].BLACK,
          shadowOffset: {width: 0, height: 2},
          shadowOpacity: 0.1,
          shadowRadius: 8,
        },
        android: {
          elevation: 4,
        },
      }),
    },
    petImage: {
      width: '100%',
      height: '100%',
    },
    petContent: {
      padding: 16,
      flexDirection: 'column',
      justifyContent: 'flex-end',
      height: '100%',
    },
    contentContainer: {
      width: '100%',
    },
    adoptionText: {
      fontSize: 12,
      color: colors[theme].WHITE,
      marginBottom: 12,
      opacity: 0.9,
    },
    petInfo: {
      marginBottom: 8,
    },
    petName: {
      fontSize: 24,
      fontWeight: 'bold',
      color: colors[theme].WHITE,
      marginBottom: 4,
      flexWrap: 'wrap',
    },
    petDetails: {
      fontSize: 14,
      color: colors[theme].WHITE,
      opacity: 0.9,
      flexWrap: 'wrap',
      lineHeight: 20,
    },
    petActions: {
      flexDirection: 'row',
      gap: 8,
      position: 'absolute',
      right: 16,
      bottom: 16,
    },
    actionButton: {
      width: 36,
      height: 36,
      borderRadius: 18,
      backgroundColor: 'rgba(255,255,255,0.2)',
      justifyContent: 'center',
      alignItems: 'center',
    },
    deleteButton: {
      backgroundColor: 'rgba(239,68,68,0.4)',
    },
    addPetCard: {
      height: 200,
      borderRadius: 16,
      backgroundColor: colors[theme].GRAY_50,
      borderWidth: 2,
      borderStyle: 'dashed',
      borderColor: colors[theme].GRAY_300,
      overflow: 'hidden',
    },
    addPetContent: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
    },
    addIconContainer: {
      marginBottom: 12,
    },
    addPetText: {
      fontSize: 16,
      color: colors[theme].GRAY_600,
      fontWeight: '500',
    },
    deleteConfirmContainer: {
      padding: 20,
      alignItems: 'center',
    },
    deleteConfirmTitle: {
      fontSize: 18,
      fontWeight: 'bold',
      marginBottom: 8,
    },
    deleteConfirmMessage: {
      fontSize: 15,
      textAlign: 'center',
    },
  });

export default PetHomeScreen;

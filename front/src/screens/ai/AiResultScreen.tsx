import React from 'react';
import { View, Text, Image, StyleSheet, ScrollView, Dimensions } from 'react-native';
import { useQuery } from '@tanstack/react-query';
import { getAiScanImage } from '@/api/ai';
import useThemeStore, { ThemeMode } from '@/store/useThemeStore';
import { colors } from '@/constants/colors';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';
import LinearGradient from 'react-native-linear-gradient';
import { useNavigation } from '@react-navigation/native';
import { CompositeNavigationProp } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { DrawerNavigationProp } from '@react-navigation/drawer';
import { MainDrawerParamList } from '@/navigations/drawer/MainDrawerNavigator';
import { MapStackParamList } from '@/navigations/stack/MapStackNavigator';
import CustomButton from '@/components/CustomButton';
import { mainNavigations } from '@/constants/navigations';

interface Props {
  route: {
    params: {
      result: {
        imgResult: string;
        fileName: string;
      };
      petType: 'dog' | 'cat';
      hasSymptom: boolean;
    };
  };
}

type NavigationProp = CompositeNavigationProp<
  StackNavigationProp<MapStackParamList>,
  DrawerNavigationProp<MainDrawerParamList>
>;

function AiResultScreen({ route }: Props) {
  const { theme } = useThemeStore();
  const { result, petType, hasSymptom } = route.params;
  const styles = styling(theme, hasSymptom);
  const navigation = useNavigation<NavigationProp>();

  const { data: imageUri } = useQuery({
    queryKey: ['aiScanImage', result.fileName],
    queryFn: () => getAiScanImage(result.fileName),
  });

  const handleFindHospital = () => {
    navigation.navigate(mainNavigations.HOME, {
      screen: 'MapHome'
    });
  };

  return (
    <ScrollView style={styles.container} bounces={false}>
      <View style={styles.header}>
        {imageUri && (
          <Image 
            source={{ uri: imageUri }} 
            style={styles.headerImage}
            resizeMode="cover"
          />
        )}
        <LinearGradient
          colors={['transparent', 'rgba(0,0,0,0.7)']}
          style={styles.gradient}
        />
        <View style={styles.headerContent}>
          <Text style={styles.title}>AI 진단 결과</Text>
          <View style={styles.badge}>
            <Text style={styles.badgeText}>
              {hasSymptom ? '유증상' : '무증상'}
            </Text>
          </View>
        </View>
      </View>

      <View style={styles.content}>
        <View style={styles.infoCard}>
          <View style={styles.infoItem}>
            <MaterialIcons 
              name="pets" 
              size={24} 
              color={colors[theme].PINK_700} 
            />
            <View>
              <Text style={styles.infoLabel}>반려동물 종류</Text>
              <Text style={styles.infoValue}>
                {petType === 'dog' ? '강아지' : '고양이'}
              </Text>
            </View>
          </View>
        </View>

        <View style={styles.resultCard}>
          <View style={styles.resultHeader}>
            <MaterialIcons 
              name="medical-services" 
              size={24} 
              color={colors[theme].PINK_700} 
            />
            <Text style={styles.resultTitle}>진단 결과</Text>
          </View>
          <Text style={styles.resultContent}>{result.imgResult}</Text>
        </View>

        <View style={styles.notice}>
          <MaterialIcons 
            name="info" 
            size={24} 
            color={colors[theme].GRAY_500} 
          />
          <Text style={styles.noticeText}>
            AI 진단 결과는 참고용으로만 사용해주세요.{'\n'}
            정확한 진단을 위해서는 반드시 전문 의료기관을 방문하세요.
          </Text>
        </View>

        <CustomButton
          style={styles.findHospitalButton}
          label="가까운 동물병원 찾기"
          onPress={handleFindHospital}
          variant="filled"
        />
      </View>
    </ScrollView>
  );
}

const SCREEN_WIDTH = Dimensions.get('window').width;

const styling = (theme: ThemeMode, hasSymptom: boolean) =>
  StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: colors[theme].WHITE,
    },
    header: {
      height: SCREEN_WIDTH * 0.8,
      position: 'relative',
    },
    headerImage: {
      width: '100%',
      height: '100%',
    },
    gradient: {
      position: 'absolute',
      left: 0,
      right: 0,
      bottom: 0,
      height: '50%',
    },
    headerContent: {
      position: 'absolute',
      bottom: 24,
      left: 24,
      right: 24,
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'flex-end',
    },
    title: {
      fontSize: 28,
      fontWeight: 'bold',
      color: colors[theme].WHITE,
    },
    badge: {
      backgroundColor: hasSymptom ? colors[theme].RED_500 : colors[theme].GREEN_500,
      paddingHorizontal: 12,
      paddingVertical: 6,
      borderRadius: 20,
    },
    badgeText: {
      color: colors[theme].WHITE,
      fontWeight: '600',
      fontSize: 14,
    },
    content: {
      padding: 24,
      gap: 24,
    },
    infoCard: {
      backgroundColor: colors[theme].WHITE,
      borderRadius: 16,
      padding: 20,
      shadowColor: colors[theme].BLACK,
      shadowOffset: {
        width: 0,
        height: 2,
      },
      shadowOpacity: 0.1,
      shadowRadius: 8,
      elevation: 4,
    },
    infoItem: {
      flexDirection: 'row',
      alignItems: 'center',
      gap: 16,
    },
    infoLabel: {
      fontSize: 14,
      color: colors[theme].GRAY_500,
      marginBottom: 4,
    },
    infoValue: {
      fontSize: 18,
      fontWeight: '600',
      color: colors[theme].BLACK,
    },
    resultCard: {
      backgroundColor: colors[theme].WHITE,
      borderRadius: 16,
      padding: 20,
      shadowColor: colors[theme].BLACK,
      shadowOffset: {
        width: 0,
        height: 2,
      },
      shadowOpacity: 0.1,
      shadowRadius: 8,
      elevation: 4,
    },
    resultHeader: {
      flexDirection: 'row',
      alignItems: 'center',
      gap: 12,
      marginBottom: 16,
    },
    resultTitle: {
      fontSize: 18,
      fontWeight: '600',
      color: colors[theme].BLACK,
    },
    resultContent: {
      fontSize: 16,
      color: colors[theme].GRAY_700,
      lineHeight: 24,
    },
    notice: {
      flexDirection: 'row',
      backgroundColor: colors[theme].GRAY_50,
      padding: 16,
      borderRadius: 12,
      gap: 12,
    },
    noticeText: {
      flex: 1,
      fontSize: 14,
      color: colors[theme].GRAY_600,
      lineHeight: 20,
    },
    findHospitalButton: {
      marginTop: 16,
    },
  });

export default AiResultScreen;

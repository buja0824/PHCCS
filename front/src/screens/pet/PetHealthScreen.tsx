import { colors } from '@/constants';
import useThemeStore, { ThemeMode } from '@/store/useThemeStore';
import React from 'react';
import { View, Image, Text, TouchableOpacity, ScrollView, StyleSheet } from 'react-native';
import Ionicons from 'react-native-vector-icons/Ionicons';

interface Props {
  navigation: any;
  route: any;
}

function PetHealthScreen({ navigation, route }: Props) {
  const {theme} = useThemeStore();
  const styles = styling(theme);
  const {petName} = route.params;

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Image 
          source={require('@/assets/images/pet-health-banner.png')}
          style={styles.banner}
          resizeMode="cover"
        />
        <View style={styles.headerContent}>
          <Text style={styles.title}>{petName}의 건강관리</Text>
          <Text style={styles.subtitle}>
            소중한 반려동물의 건강 기록을{'\n'}
            안전하게 보관하세요
          </Text>
        </View>
      </View>

      <View style={styles.notice}>
        <Ionicons name="shield-checkmark" size={20} color={colors[theme].GREEN_600} />
        <Text style={styles.noticeText}>
          모든 건강 기록은 기기에 암호화되어 저장되며{'\n'}
          서버로 전송되지 않습니다.
        </Text>
      </View>

      <View style={styles.menuGrid}>
        <TouchableOpacity 
          style={styles.menuCard}
          onPress={() => navigation.navigate('HealthCheckupList', {petName})}
        >
          <View style={[styles.iconContainer, { backgroundColor: '#E3F2FD' }]}>
            <Ionicons name="fitness" size={24} color="#1565C0" />
          </View>
          <Text style={styles.menuTitle}>건강 검진</Text>
          <Text style={styles.menuDescription}>
            정기 검진 기록과{'\n'}
            다음 검진 일정 관리
          </Text>
        </TouchableOpacity>

        <TouchableOpacity 
          style={styles.menuCard}
          onPress={() => navigation.navigate('VaccinationList', {petName})}
        >
          <View style={[styles.iconContainer, { backgroundColor: colors[theme].GREEN_50 }]}>
            <Ionicons name="shield-checkmark" size={24} color={colors[theme].GREEN_600} />
          </View>
          <Text style={styles.menuTitle}>예방접종</Text>
          <Text style={styles.menuDescription}>
            예방접종 기록과{'\n'}
            다음 접종 일정 관리
          </Text>
        </TouchableOpacity>

        <TouchableOpacity 
          style={styles.menuCard}
          onPress={() => navigation.navigate('MedicalHistoryList', {petName})}
        >
          <View style={[styles.iconContainer, { backgroundColor: colors[theme].RED_50 }]}>
            <Ionicons name="medical" size={24} color={colors[theme].RED_600} />
          </View>
          <Text style={styles.menuTitle}>질병 관리</Text>
          <Text style={styles.menuDescription}>
            질병 이력과{'\n'}
            치료 기록 관리
          </Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
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
    title: {
      fontSize: 28,
      fontWeight: 'bold',
      color: colors[theme].WHITE,
      marginBottom: 8,
      textShadowColor: 'rgba(0, 0, 0, 0.3)',
      textShadowOffset: {width: 0, height: 1},
      textShadowRadius: 4,
    },
    subtitle: {
      fontSize: 16,
      color: colors[theme].WHITE,
      lineHeight: 22,
      textShadowColor: 'rgba(0, 0, 0, 0.3)',
      textShadowOffset: {width: 0, height: 1},
      textShadowRadius: 4,
    },
    notice: {
      flexDirection: 'row',
      alignItems: 'center',
      backgroundColor: colors[theme].GREEN_50,
      padding: 16,
      marginHorizontal: 24,
      marginTop: -20,
      borderRadius: 12,
      gap: 12,
      shadowColor: colors[theme].BLACK,
      shadowOffset: {
        width: 0,
        height: 2,
      },
      shadowOpacity: 0.1,
      shadowRadius: 3,
      elevation: 2,
    },
    noticeText: {
      flex: 1,
      fontSize: 13,
      color: colors[theme].GREEN_700,
      lineHeight: 18,
    },
    menuGrid: {
      padding: 24,
      flexDirection: 'row',
      flexWrap: 'wrap',
      gap: 16,
    },
    menuCard: {
      width: '47%',
      backgroundColor: colors[theme].WHITE,
      borderRadius: 16,
      padding: 20,
      shadowColor: colors[theme].BLACK,
      shadowOffset: {
        width: 0,
        height: 2,
      },
      shadowOpacity: 0.1,
      shadowRadius: 3,
      elevation: 2,
    },
    iconContainer: {
      width: 48,
      height: 48,
      borderRadius: 12,
      justifyContent: 'center',
      alignItems: 'center',
      marginBottom: 12,
    },
    menuTitle: {
      fontSize: 16,
      fontWeight: '600',
      color: colors[theme].GRAY_900,
      marginBottom: 4,
    },
    menuDescription: {
      fontSize: 13,
      color: colors[theme].GRAY_600,
      lineHeight: 18,
    },
  });

export default PetHealthScreen;

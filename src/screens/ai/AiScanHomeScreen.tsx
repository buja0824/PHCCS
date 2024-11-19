import React from 'react';
import {View, Text, StyleSheet, TouchableOpacity, Image, Platform} from 'react-native';
import {colors} from '@/constants';
import useThemeStore from '@/store/useThemeStore';
import {ThemeMode} from '@/types';
import {StackScreenProps} from '@react-navigation/stack';
import {AiStackParamList} from '@/navigations/stack/AiStackNavigator';
import {aiNavigations} from '@/constants/navigations';
import Ionicons from 'react-native-vector-icons/Ionicons';

type Props = StackScreenProps<AiStackParamList, typeof aiNavigations.AI_HOME>;

function AiHomeScreen({navigation}: Props) {
  const {theme} = useThemeStore();
  const styles = styling(theme);

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Image 
          source={require('@/assets/images/ai-banner.png')}
          style={styles.banner}
          resizeMode="cover"
        />
        <View style={styles.headerContent}>
          <Text style={styles.title}>AI 피부병 진단</Text>
          <Text style={styles.subtitle}>
            반려동물의 피부 상태를{'\n'}
            AI가 빠르게 진단해드립니다
          </Text>
        </View>
      </View>

      <View style={styles.content}>
        <View style={styles.infoCard}>
          <View style={styles.infoItem}>
            <Ionicons name="camera" size={24} color={colors[theme].PINK_700} />
            <Text style={styles.infoText}>사진 촬영</Text>
          </View>
          <View style={styles.arrow}>
            <Ionicons name="arrow-forward" size={20} color={colors[theme].GRAY_400} />
          </View>
          <View style={styles.infoItem}>
            <Ionicons name="scan" size={24} color={colors[theme].PINK_700} />
            <Text style={styles.infoText}>AI 분석</Text>
          </View>
          <View style={styles.arrow}>
            <Ionicons name="arrow-forward" size={20} color={colors[theme].GRAY_400} />
          </View>
          <View style={styles.infoItem}>
            <Ionicons name="medical" size={24} color={colors[theme].PINK_700} />
            <Text style={styles.infoText}>진단 결과</Text>
          </View>
        </View>

        <View style={styles.notice}>
          <Ionicons name="information-circle" size={20} color={colors[theme].GRAY_500} />
          <Text style={styles.noticeText}>
            AI 진단은 참고용으로만 사용해주세요.{'\n'}
            정확한 진단은 반드시 전문 의료기관을 방문하세요.
          </Text>
        </View>
      </View>

      <TouchableOpacity
        style={styles.startButton}
        onPress={() => navigation.navigate(aiNavigations.AI_PET_TYPE)}>
        <Text style={styles.startButtonText}>진단 시작하기</Text>
      </TouchableOpacity>
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
    content: {
      flex: 1,
      padding: 24,
    },
    infoCard: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      marginBottom: 24,
    },
    infoItem: {
      flexDirection: 'row',
      alignItems: 'center',
      flex: 1,
      justifyContent: 'center',
      gap: 8,
    },
    infoText: {
      fontSize: 14,
      color: colors[theme].GRAY_700,
      fontWeight: '500',
    },
    arrow: {
      marginHorizontal: 4,
    },
    notice: {
      flexDirection: 'row',
      alignItems: 'center',
      marginBottom: 24,
    },
    noticeText: {
      fontSize: 15,
      color: colors[theme].GRAY_700,
      lineHeight: 22,
      marginLeft: 8,
    },
    startButton: {
      backgroundColor: colors[theme].PINK_700,
      borderRadius: 12,
      padding: 20,
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'center',
      position: 'absolute',
      bottom: 40,
      left: 20,
      right: 20,
    },
    startButtonText: {
      color: colors[theme].WHITE,
      fontSize: 17,
      fontWeight: 'bold',
      marginRight: 8,
    },
  });

export default AiHomeScreen;

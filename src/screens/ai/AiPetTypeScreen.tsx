import React from 'react';
import {View, Text, StyleSheet, TouchableOpacity, Platform, Image} from 'react-native';
import {colors} from '@/constants';
import useThemeStore from '@/store/useThemeStore';
import {ThemeMode} from '@/types';
import {StackScreenProps} from '@react-navigation/stack';
import {AiStackParamList} from '@/navigations/stack/AiStackNavigator';
import {aiNavigations} from '@/constants/navigations';
import LinearGradient from 'react-native-linear-gradient';

type Props = StackScreenProps<AiStackParamList, typeof aiNavigations.AI_PET_TYPE>;

function AiPetTypeScreen({navigation}: Props) {
  const {theme} = useThemeStore();
  const styles = styling(theme);

  return (
    <View style={styles.container}>
      <Text style={styles.stepText}>STEP 1/3</Text>
      <Text style={styles.title}>반려동물을 선택해주세요</Text>
      
      <View style={styles.cardsContainer}>
        <TouchableOpacity
          style={styles.card}
          onPress={() => 
            navigation.navigate(aiNavigations.AI_SYMPTOM_TYPE, {
              petType: 'dog'
            })
          }>
          <Image
            source={require('@/assets/images/dog-card.png')}
            style={styles.cardImage}
            resizeMode="cover"
          />
          <LinearGradient
            colors={['transparent', 'rgba(0,0,0,0.7)']}
            style={styles.gradient}>
            <View style={styles.cardContent}>
              <Text style={styles.cardTitle}>강아지</Text>
              <Text style={styles.cardDescription}>
                강아지의 피부 상태를 진단합니다
              </Text>
            </View>
          </LinearGradient>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.card}
          onPress={() => 
            navigation.navigate(aiNavigations.AI_SYMPTOM_TYPE, {
              petType: 'cat'
            })
          }>
          <Image
            source={require('@/assets/images/cat-card.png')}
            style={styles.cardImage}
            resizeMode="cover"
          />
          <LinearGradient 
            colors={['transparent', 'rgba(0,0,0,0.7)']}
            style={styles.gradient}>
            <View style={styles.cardContent}>
              <Text style={styles.cardTitle}>고양이</Text>
              <Text style={styles.cardDescription}>
                고양이의 피부 상태를 진단합니다
              </Text>
            </View>
          </LinearGradient>
        </TouchableOpacity>
      </View>

      <View style={styles.tipContainer}>
        <Text style={styles.tipTitle}>촬영 팁</Text>
        <View style={styles.tipContent}>
          <Text style={styles.tipText}>• 피부 병변 부위를 가까이서 촬영해주세요</Text>
          <Text style={styles.tipText}>• 밝은 곳에서 촬영하면 더 정확합니다</Text>
          <Text style={styles.tipText}>• 흔들리지 않게 촬영해주세요</Text>
        </View>
      </View>
    </View>
  );
}

const styling = (theme: ThemeMode) =>
  StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: colors[theme].WHITE,
      padding: 24,
    },
    stepText: {
      fontSize: 14,
      fontWeight: '600',
      color: colors[theme].PINK_700,
      marginBottom: 8,
    },
    title: {
      fontSize: 24,
      fontWeight: 'bold',
      color: colors[theme].BLACK,
      marginBottom: 32,
    },
    cardsContainer: {
      flex: 1,
      gap: 16,
    },
    card: {
      height: 180,
      borderRadius: 16,
      overflow: 'hidden',
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
    cardImage: {
      width: '100%',
      height: '100%',
    },
    gradient: {
      position: 'absolute',
      left: 0,
      right: 0,
      bottom: 0,
      height: '50%',
      justifyContent: 'flex-end',
    },
    cardContent: {
      padding: 16,
    },
    cardTitle: {
      fontSize: 20,
      fontWeight: 'bold',
      color: colors[theme].WHITE,
      marginBottom: 4,
    },
    cardDescription: {
      fontSize: 14,
      color: colors[theme].WHITE,
      opacity: 0.9,
    },
    tipContainer: {
      marginTop: 32,
      backgroundColor: colors[theme].GRAY_50,
      borderRadius: 16,
      padding: 16,
    },
    tipTitle: {
      fontSize: 18,
      fontWeight: 'bold',
      color: colors[theme].BLACK,
      marginBottom: 8,
    },
    tipContent: {
      gap: 8,
    },
    tipText: {
      fontSize: 14,
      color: colors[theme].GRAY_700,
    },
  });

export default AiPetTypeScreen;

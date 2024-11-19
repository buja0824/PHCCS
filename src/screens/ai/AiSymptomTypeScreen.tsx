import React from 'react';
import {View, Text, StyleSheet, TouchableOpacity, Platform, Image, ScrollView} from 'react-native';
import {colors} from '@/constants';
import useThemeStore from '@/store/useThemeStore';
import {ThemeMode} from '@/types';
import {StackScreenProps} from '@react-navigation/stack';
import {AiStackParamList} from '@/navigations/stack/AiStackNavigator';
import {aiNavigations} from '@/constants/navigations';
import Ionicons from 'react-native-vector-icons/Ionicons';

type Props = StackScreenProps<AiStackParamList, typeof aiNavigations.AI_SYMPTOM_TYPE>;

function AiSymptomTypeScreen({navigation, route}: Props) {
  const {theme} = useThemeStore();
  const styles = styling(theme);
  const {petType} = route.params;

  return (
    <ScrollView style={styles.container}>
      <View style={styles.content}>
        <Text style={styles.stepText}>STEP 2/3</Text>
        <Text style={styles.title}>증상이 있나요?</Text>
        <Text style={styles.subtitle}>
          {petType === 'dog' ? '강아지' : '고양이'}의 피부 상태를 선택해주세요
        </Text>

        <View style={styles.cardsContainer}>
          <TouchableOpacity
            style={styles.card}
            onPress={() => 
              navigation.navigate(aiNavigations.AI_CAMERA, {
                petType,
                hasSymptom: true,
              })
            }>
            <View style={styles.cardHeader}>
              <View style={[styles.iconContainer, {backgroundColor: colors[theme].RED_50}]}>
                <Ionicons name="bandage" size={28} color={colors[theme].RED_600} />
              </View>
              <View style={styles.statusBadge}>
                <Text style={styles.statusText}>유증상</Text>
              </View>
            </View>
            
            <Text style={styles.cardTitle}>피부 증상이 있어요</Text>
            <View style={styles.symptomList}>
              <Text style={styles.symptomItem}>• 붉은 반점이 있어요</Text>
              <Text style={styles.symptomItem}>• 피부가 건조하고 각질이 있어요</Text>
              <Text style={styles.symptomItem}>• 털이 빠지고 피부가 벗겨져요</Text>
              <Text style={styles.symptomItem}>• 자주 긁어요</Text>
            </View>
            
            <Image 
              source={require('@/assets/images/symptom-example.png')}
              style={styles.exampleImage}
              resizeMode="cover"
            />
          </TouchableOpacity>

          <TouchableOpacity
            style={styles.card}
            onPress={() => 
              navigation.navigate(aiNavigations.AI_CAMERA, {
                petType,
                hasSymptom: false,
              })
            }>
            <View style={styles.cardHeader}>
              <View style={[styles.iconContainer, {backgroundColor: colors[theme].GREEN_50}]}>
                <Ionicons name="checkmark-circle" size={28} color={colors[theme].GREEN_600} />
              </View>
              <View style={[styles.statusBadge, styles.normalBadge]}>
                <Text style={[styles.statusText, styles.normalText]}>무증상</Text>
              </View>
            </View>
            
            <Text style={styles.cardTitle}>증상이 없어요</Text>
            <Text style={styles.cardDescription}>
              정기적인 피부 검진을 위해{'\n'}
              건강한 피부 상태를 기록하고 싶어요
            </Text>
          </TouchableOpacity>
        </View>
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
    content: {
      padding: 24,
      paddingBottom: 40,
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
      marginBottom: 8,
    },
    subtitle: {
      fontSize: 16,
      color: colors[theme].GRAY_600,
      marginBottom: 32,
    },
    cardsContainer: {
      gap: 16,
    },
    card: {
      backgroundColor: colors[theme].WHITE,
      borderRadius: 16,
      padding: 20,
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
    cardHeader: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      marginBottom: 16,
    },
    iconContainer: {
      width: 56,
      height: 56,
      borderRadius: 28,
      justifyContent: 'center',
      alignItems: 'center',
      marginRight: 16,
    },
    statusBadge: {
      backgroundColor: colors[theme].GRAY_100,
      borderRadius: 12,
      padding: 4,
      paddingHorizontal: 8,
    },
    statusText: {
      fontSize: 14,
      fontWeight: '600',
      color: colors[theme].BLACK,
    },
    normalBadge: {
      backgroundColor: colors[theme].GREEN_100,
    },
    normalText: {
      color: colors[theme].GREEN_700,
    },
    cardTitle: {
      fontSize: 20,
      fontWeight: 'bold',
      color: colors[theme].BLACK,
      marginBottom: 8,
    },
    symptomList: {
      marginBottom: 16,
    },
    symptomItem: {
      fontSize: 15,
      color: colors[theme].GRAY_700,
      lineHeight: 22,
      marginBottom: 4,
    },
    exampleImage: {
      width: '100%',
      height: 120,
      borderRadius: 8,
    },
    cardDescription: {
      fontSize: 15,
      color: colors[theme].GRAY_700,
      lineHeight: 22,
      marginBottom: 16,
    },
  });

export default AiSymptomTypeScreen;

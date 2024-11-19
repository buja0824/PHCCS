import DateSelector from '@/components/pet/DateSelector';
import { colors } from '@/constants';
import useModal from '@/hooks/useModal';
import useThemeStore, { ThemeMode } from '@/store/useThemeStore';
import { Vaccination } from '@/types/petHealth';
import { saveVaccination } from '@/utils/PetHealthStorage';
import React, { useState } from 'react';
import { ScrollView, View, Text, Pressable, TextInput, TouchableOpacity, Alert, StyleSheet } from 'react-native';
import Ionicons from 'react-native-vector-icons/Ionicons';

type Props = {
  navigation: any;
  route: any;
};

function VaccinationScreen({ navigation, route }: Props) {
  const {theme} = useThemeStore();
  const styles = styling(theme);
  const {petName} = route.params;
  const [date, setDate] = useState(new Date());
  const [type, setType] = useState('');
  const [description, setDescription] = useState('');
  const [nextVaccinationDate, setNextVaccinationDate] = useState<Date | null>(null);
  
  const dateSelector = useModal();
  const nextDateSelector = useModal();

  const handleSave = async () => {
    if (!type.trim() || !description.trim()) {
      Alert.alert('알림', '모든 항목을 입력해주세요.');
      return;
    }

    const vaccination: Vaccination = {
      id: Date.now(),
      petName,
      date: date.toISOString(),
      type,
      description,
      nextVaccinationDate: nextVaccinationDate?.toISOString(),
    };

    try {
      await saveVaccination(vaccination);
      Alert.alert('알림', '예방접종 기록이 저장되었습니다.', [
        { text: '확인', onPress: () => navigation.goBack() }
      ]);
    } catch (error) {
      Alert.alert('오류', '저장에 실패했습니다.');
    }
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>예방접종 기록</Text>
        <Text style={styles.subtitle}>{petName}의 예방접종 내용을 기록해주세요</Text>
      </View>

      <View style={styles.content}>
        <View style={styles.inputGroup}>
          <Text style={styles.label}>접종일</Text>
          <Pressable style={styles.dateInput} onPress={dateSelector.show}>
            <Text style={styles.dateText}>
              {date.getFullYear()}년 {date.getMonth() + 1}월 {date.getDate()}일
            </Text>
            <Ionicons name="calendar" size={20} color={colors[theme].GRAY_500} />
          </Pressable>
        </View>

        <View style={styles.inputGroup}>
          <Text style={styles.label}>접종 종류</Text>
          <TextInput
            style={styles.input}
            value={type}
            onChangeText={setType}
            placeholder="예: 종합백신, 광견병 등"
          />
        </View>

        <View style={styles.inputGroup}>
          <Text style={styles.label}>특이사항</Text>
          <TextInput
            style={[styles.input, styles.textArea]}
            value={description}
            onChangeText={setDescription}
            multiline
            placeholder="접종 후 주의사항이나 특이사항을 기록해주세요"
          />
        </View>

        <View style={styles.inputGroup}>
          <Text style={styles.label}>다음 접종 예정일</Text>
          <Pressable 
            style={styles.dateInput} 
            onPress={nextDateSelector.show}
          >
            <Text style={styles.dateText}>
              {nextVaccinationDate 
                ? `${nextVaccinationDate.getFullYear()}년 ${nextVaccinationDate.getMonth() + 1}월 ${nextVaccinationDate.getDate()}일`
                : '날짜 선택'}
            </Text>
            <Ionicons name="calendar" size={20} color={colors[theme].GRAY_500} />
          </Pressable>
        </View>

        <TouchableOpacity style={styles.saveButton} onPress={handleSave}>
          <Text style={styles.saveButtonText}>저장하기</Text>
        </TouchableOpacity>
      </View>

      <DateSelector
        isVisible={dateSelector.isVisible}
        currentDate={date}
        onChangeDate={setDate}
        hide={dateSelector.hide}
        maximumDate={new Date()}
      />

      <DateSelector
        isVisible={nextDateSelector.isVisible}
        currentDate={nextVaccinationDate || new Date()}
        onChangeDate={setNextVaccinationDate}
        hide={nextDateSelector.hide}
        minimumDate={new Date()}
      />
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
      padding: 24,
      paddingBottom: 16,
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
    },
    content: {
      padding: 24,
    },
    inputGroup: {
      marginBottom: 24,
    },
    label: {
      fontSize: 14,
      fontWeight: '500',
      color: colors[theme].GRAY_700,
      marginBottom: 8,
    },
    input: {
      borderWidth: 1,
      borderColor: colors[theme].GRAY_300,
      borderRadius: 8,
      padding: 12,
      fontSize: 16,
      color: colors[theme].BLACK,
    },
    textArea: {
      height: 100,
      textAlignVertical: 'top',
    },
    dateInput: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      borderWidth: 1,
      borderColor: colors[theme].GRAY_300,
      borderRadius: 8,
      padding: 12,
    },
    dateText: {
      fontSize: 16,
      color: colors[theme].BLACK,
    },
    saveButton: {
      backgroundColor: colors[theme].PINK_500,
      borderRadius: 8,
      padding: 16,
      alignItems: 'center',
      marginTop: 16,
    },
    saveButtonText: {
      color: colors[theme].WHITE,
      fontSize: 16,
      fontWeight: '600',
    },
  });

export default VaccinationScreen;

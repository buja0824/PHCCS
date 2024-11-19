import DateSelector from '@/components/pet/DateSelector';
import { colors } from '@/constants';
import useModal from '@/hooks/useModal';
import useThemeStore, { ThemeMode } from '@/store/useThemeStore';
import { MedicalHistory } from '@/types/petHealth';
import { saveMedicalHistory } from '@/utils/PetHealthStorage';
import React, { useState } from 'react';
import { ScrollView, View, Text, Pressable, TextInput, TouchableOpacity, Alert, StyleSheet } from 'react-native';
import Ionicons from 'react-native-vector-icons/Ionicons';

type Props = {
  navigation: any;
  route: any;
};

function MedicalHistoryScreen({ navigation, route }: Props) {
  const {theme} = useThemeStore();
  const styles = styling(theme);
  const {petName} = route.params;
  const [date, setDate] = useState(new Date());
  const [condition, setCondition] = useState('');
  const [treatment, setTreatment] = useState('');
  const [medication, setMedication] = useState('');
  const [nextVisitDate, setNextVisitDate] = useState<Date | null>(null);
  
  const dateSelector = useModal();
  const nextDateSelector = useModal();

  const handleSave = async () => {
    if (!condition.trim() || !treatment.trim()) {
      Alert.alert('알림', '필수 항목을 입력해주세요.');
      return;
    }

    const history: MedicalHistory = {
      id: Date.now(),
      petName,
      date: date.toISOString(),
      condition,
      treatment,
      medication,
      nextVisitDate: nextVisitDate?.toISOString(),
    };

    try {
      await saveMedicalHistory(history);
      Alert.alert('알림', '질병 기록이 저장되었습니다.', [
        { text: '확인', onPress: () => navigation.goBack() }
      ]);
    } catch (error) {
      Alert.alert('오류', '저장에 실패했습니다.');
    }
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>질병 기록</Text>
        <Text style={styles.subtitle}>{petName}의 질병과 치료 내용을 기록해주세요</Text>
      </View>

      <View style={styles.content}>
        <View style={styles.inputGroup}>
          <Text style={styles.label}>진료일</Text>
          <Pressable style={styles.dateInput} onPress={dateSelector.show}>
            <Text style={styles.dateText}>
              {date.getFullYear()}년 {date.getMonth() + 1}월 {date.getDate()}일
            </Text>
            <Ionicons name="calendar" size={20} color={colors[theme].GRAY_500} />
          </Pressable>
        </View>

        <View style={styles.inputGroup}>
          <Text style={styles.label}>증상</Text>
          <TextInput
            style={styles.input}
            value={condition}
            onChangeText={setCondition}
            placeholder="진단받은 질병이나 증상을 입력하세요"
          />
        </View>

        <View style={styles.inputGroup}>
          <Text style={styles.label}>치료 내용</Text>
          <TextInput
            style={[styles.input, styles.textArea]}
            value={treatment}
            onChangeText={setTreatment}
            multiline
            placeholder="받은 치료와 주의사항을 기록해주세요"
          />
        </View>

        <View style={styles.inputGroup}>
          <Text style={styles.label}>처방약</Text>
          <TextInput
            style={styles.input}
            value={medication}
            onChangeText={setMedication}
            placeholder="처방받은 약을 입력하세요 (선택)"
          />
        </View>

        <View style={styles.inputGroup}>
          <Text style={styles.label}>다음 내원일</Text>
          <Pressable 
            style={styles.dateInput} 
            onPress={nextDateSelector.show}
          >
            <Text style={styles.dateText}>
              {nextVisitDate 
                ? `${nextVisitDate.getFullYear()}년 ${nextVisitDate.getMonth() + 1}월 ${nextVisitDate.getDate()}일`
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
        currentDate={nextVisitDate || new Date()}
        onChangeDate={setNextVisitDate}
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

export default MedicalHistoryScreen;

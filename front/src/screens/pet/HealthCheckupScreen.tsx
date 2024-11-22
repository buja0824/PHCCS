import DateSelector from '@/components/pet/DateSelector';
import { colors } from '@/constants';
import useModal from '@/hooks/useModal';
import useThemeStore, { ThemeMode } from '@/store/useThemeStore';
import { HealthCheckup } from '@/types/petHealth';
import { setEncryptStorage, getEncryptStorage } from '@/utils';
import { saveHealthCheckup, getHealthCheckups } from '@/utils/PetHealthStorage';
import React, { useState } from 'react';
import { ScrollView, View, Text, Pressable, TextInput, TouchableOpacity, Alert, StyleSheet } from 'react-native';
import Ionicons from 'react-native-vector-icons/Ionicons';


type Props = {
  navigation: any;
  route: any;
};

function HealthCheckupScreen({ navigation, route }: Props) {
  const {theme} = useThemeStore();
  const styles = styling(theme);
  const {petName, checkup, mode} = route.params;
  const [date, setDate] = useState(mode === 'edit' ? new Date(checkup.date) : new Date());
  const [weight, setWeight] = useState(mode === 'edit' ? checkup.weight.toString() : '');
  const [description, setDescription] = useState(mode === 'edit' ? checkup.description : '');
  const [nextCheckupDate, setNextCheckupDate] = useState<Date | null>(
    mode === 'edit' && checkup.nextCheckupDate 
      ? new Date(checkup.nextCheckupDate) 
      : null
  );
  
  const dateSelector = useModal();
  const nextDateSelector = useModal();

  const handleSave = async () => {
    if (!weight.trim() || !description.trim()) {
      Alert.alert('알림', '모든 항목을 입력해주세요.');
      return;
    }

    const healthCheckup: HealthCheckup = {
      id: mode === 'edit' ? checkup.id : Date.now(),
      petName,
      date: date.toISOString(),
      weight: parseFloat(weight),
      description,
      nextCheckupDate: nextCheckupDate?.toISOString(),
    };

    try {
      const existingCheckups = await getHealthCheckups(petName);
      let newCheckups;
      
      // 기존 일정 삭제 (수정 시)
      if (mode === 'edit') {
        const schedules = await getEncryptStorage('schedules') || {};
        if (checkup.nextCheckupDate) {
          const oldDateKey = checkup.nextCheckupDate.split('T')[0];
          if (schedules[oldDateKey]) {
            schedules[oldDateKey] = schedules[oldDateKey].filter(
              (schedule: any) => schedule.type !== 'health_checkup' || 
                schedule.content !== `${petName} 건강검진`
            );
            if (schedules[oldDateKey].length === 0) {
              delete schedules[oldDateKey];
            }
          }
        }
        await setEncryptStorage('schedules', schedules);
        
        newCheckups = existingCheckups.map((item: HealthCheckup) =>
          item.id === healthCheckup.id ? healthCheckup : item
        );
      } else {
        newCheckups = [...existingCheckups, healthCheckup];
      }
      
      await setEncryptStorage(`health_checkups_${petName}`, newCheckups);
      
      // 새 일정 추가
      if (nextCheckupDate) {
        const schedules = await getEncryptStorage('schedules') || {};
        const dateKey = nextCheckupDate.toISOString().split('T')[0];
        
        if (!schedules[dateKey]) {
          schedules[dateKey] = [];
        }
        
        schedules[dateKey].push({
          id: Date.now(),
          content: `${petName} 건강검진`,
          date: nextCheckupDate,
          type: 'health_checkup',
          color: '#1565C0'
        });

        await setEncryptStorage('schedules', schedules);
      }
      
      Alert.alert('알림', 
        mode === 'edit' ? '수정되었습니다.' : '저장되었습니다.', 
        [{ text: '확인', onPress: () => navigation.goBack() }]
      );
    } catch (error) {
      Alert.alert('오류', '저장에 실패했습니다.');
    }
  };

  const handleWeightChange = (text: string) => {
    // 숫자와 소수점만 허용
    const filtered = text.replace(/[^0-9.]/g, '');
    
    // 소수점이 하나만 있도록 처리
    const dots = filtered.split('.').length - 1;
    if (dots <= 1) {
      // 소수점 둘째자리까지만 허용
      const parts = filtered.split('.');
      if (parts[1]?.length > 2) {
        parts[1] = parts[1].slice(0, 2);
        setWeight(parts.join('.'));
      } else {
        setWeight(filtered);
      }
    }
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>건강검진 기록</Text>
        <Text style={styles.subtitle}>{petName}의 건강검진 내용을 기록해주세요</Text>
      </View>

      <View style={styles.content}>
        <View style={styles.inputGroup}>
          <Text style={styles.label}>검진일</Text>
          <Pressable style={styles.dateInput} onPress={dateSelector.show}>
            <Text style={styles.dateText}>
              {date.getFullYear()}년 {date.getMonth() + 1}월 {date.getDate()}일
            </Text>
            <Ionicons name="calendar" size={20} color={colors[theme].GRAY_500} />
          </Pressable>
        </View>

        <View style={styles.inputGroup}>
          <Text style={styles.label}>체중 (kg)</Text>
          <TextInput
            style={styles.input}
            value={weight}
            onChangeText={handleWeightChange}
            keyboardType="decimal-pad"
            placeholder="0.0"
            maxLength={5}
          />
        </View>

        <View style={styles.inputGroup}>
          <Text style={styles.label}>검진 내용</Text>
          <TextInput
            style={[styles.input, styles.textArea]}
            value={description}
            onChangeText={setDescription}
            multiline
            placeholder="검진 결과와 특이사항을 기록해주세요"
          />
        </View>

        <View style={styles.inputGroup}>
          <Text style={styles.label}>다음 검진일</Text>
          <Pressable 
            style={styles.dateInput} 
            onPress={nextDateSelector.show}
          >
            <Text style={styles.dateText}>
              {nextCheckupDate 
                ? `${nextCheckupDate.getFullYear()}년 ${nextCheckupDate.getMonth() + 1}월 ${nextCheckupDate.getDate()}일`
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
        title="검진일 선택"
      />

      <DateSelector
        isVisible={nextDateSelector.isVisible}
        currentDate={nextCheckupDate || date}
        onChangeDate={setNextCheckupDate}
        hide={nextDateSelector.hide}
        minimumDate={date}
        title="다음 검진일 선택"
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
      paddingBottom: 40,
    },
    title: {
      fontSize: 24,
      fontWeight: '600',
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
      fontSize: 16,
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

export default HealthCheckupScreen;

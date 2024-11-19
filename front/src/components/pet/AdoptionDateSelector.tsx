import React from 'react';
import {View, StyleSheet, Pressable, Text} from 'react-native';
import {colors} from '@/constants';
import useThemeStore from '@/store/useThemeStore';
import {ThemeMode} from '@/types';
import DateSelector from './DateSelector';
import useModal from '@/hooks/useModal';

interface AdoptionDateSelectorProps {
  date: Date;
  birthDate: Date;
  onChangeDate: (date: Date) => void;
}

function AdoptionDateSelector({date, birthDate, onChangeDate}: AdoptionDateSelectorProps) {
  const {theme} = useThemeStore();
  const dateSelector = useModal();
  const styles = styling(theme);

  // 입양일이 출생일보다 이전이면 출생일로 설정
  React.useEffect(() => {
    if (date < birthDate) {
      onChangeDate(new Date(birthDate));
    }
  }, [birthDate, date, onChangeDate]);

  return (
    <View style={styles.container}>
      <Text style={styles.label}>입양일</Text>
      <Pressable style={styles.content} onPress={dateSelector.show}>
        <Text style={styles.dateText}>
          {date.getFullYear()}년 {date.getMonth() + 1}월 {date.getDate()}일
        </Text>
      </Pressable>
      <DateSelector
        isVisible={dateSelector.isVisible}
        currentDate={date}
        onChangeDate={onChangeDate}
        hide={dateSelector.hide}
        maximumDate={new Date()}
        minimumDate={birthDate}
        title="입양일 선택"
      />
    </View>
  );
}

const styling = (theme: ThemeMode) =>
  StyleSheet.create({
    container: {
      padding: 15,
      borderWidth: 1,
      borderColor: colors[theme].GRAY_200,
      borderRadius: 8,
      marginBottom: 16,
    },
    label: {
      fontSize: 12,
      color: colors[theme].GRAY_500,
      marginBottom: 4,
    },
    content: {
      flexDirection: 'row',
      alignItems: 'center',
    },
    dateText: {
      color: colors[theme].BLACK,
      flex: 1,
    },
  });

export default AdoptionDateSelector;

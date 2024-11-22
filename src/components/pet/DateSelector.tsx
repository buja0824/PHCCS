import React from 'react';
import {Modal, Pressable, StyleSheet, Text, View} from 'react-native';
import DatePicker from 'react-native-date-picker';
import {colors} from '@/constants';
import useThemeStore from '@/store/useThemeStore';
import {ThemeMode} from '@/types';

interface DateSelectorProps {
  isVisible: boolean;
  currentDate: Date;
  onChangeDate: (date: Date) => void;
  hide: () => void;
  maximumDate?: Date;
  minimumDate?: Date;
  title?: string;
}

function DateSelector({
  isVisible,
  currentDate,
  onChangeDate,
  hide,
  maximumDate = undefined,
  minimumDate,
  title = '날짜 선택',
}: DateSelectorProps) {
  const {theme} = useThemeStore();
  
  // 현재 날짜가 최소/최대 날짜 범위를 벗어나면 조정
  const adjustedDate = React.useMemo(() => {
    let date = new Date(currentDate);
    if (minimumDate && date < minimumDate) {
      date = new Date(minimumDate);
    }
    if (maximumDate && date > maximumDate) {
      date = new Date(maximumDate);
    }
    return date;
  }, [currentDate, minimumDate, maximumDate]);

  return (
    <DatePicker
      modal
      open={isVisible}
      date={adjustedDate}
      onConfirm={(date) => {
        onChangeDate(date);
        hide();
      }}
      onCancel={hide}
      mode="date"
      locale="ko"
      title={title}
      confirmText="확인"
      cancelText="취소"
      maximumDate={maximumDate}
      minimumDate={minimumDate}
    />
  );
}

const styling = (theme: ThemeMode) =>
  StyleSheet.create({});

export default DateSelector;

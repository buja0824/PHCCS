import React from 'react';
import {Dimensions, Pressable, StyleSheet, Text, View} from 'react-native';
import {colors} from '@/constants';
import useThemeStore from '@/store/useThemeStore';
import {ThemeMode} from '@/types';

interface DateBoxProps {
  date: number;
  isToday: boolean;
  hasSchedule: boolean;
  selectedDate: number;
  isInRange?: boolean;
  onPressDate: (date: number) => void;
}

const deviceWidth = Dimensions.get('window').width;

function DateBox({
  date,
  isToday,
  hasSchedule,
  selectedDate,
  isInRange,
  onPressDate,
}: DateBoxProps) {
  const {theme} = useThemeStore();
  const styles = styling(theme);

  return (
    <Pressable style={styles.container} onPress={() => onPressDate(date)}>
      {date > 0 && (
        <>
          <View
            style={[
              styles.dateContainer,
              isToday && styles.todayContainer,
              selectedDate === date && styles.selectedContainer,
              isInRange && styles.rangeContainer,
              selectedDate === date && isToday && styles.selectedTodayContainer,
            ]}>
            <Text
              style={[
                styles.dateText,
                isToday && styles.todayText,
                selectedDate === date && styles.selectedDateText,
                isInRange && styles.rangeText,
              ]}>
              {date}
            </Text>
          </View>
          {hasSchedule && <View style={styles.scheduleIndicator} />}
        </>
      )}
    </Pressable>
  );
}

const styling = (theme: ThemeMode) =>
  StyleSheet.create({
    container: {
      width: (Dimensions.get('window').width - 32) / 7,
      aspectRatio: 1,
      alignItems: 'center',
      justifyContent: 'center',
      padding: 2,
      borderRightWidth: 0.5,
      borderBottomWidth: 0.5,
      borderColor: colors[theme].GRAY_200,
    },
    dateContainer: {
      width: '80%',
      aspectRatio: 1,
      alignItems: 'center',
      justifyContent: 'center',
      borderRadius: 100,
    },
    dateText: {
      fontSize: 14,
      color: colors[theme].GRAY_700,
    },
    todayContainer: {
      backgroundColor: colors[theme].PINK_50,
      borderWidth: 1,
      borderColor: colors[theme].PINK_200,
    },
    todayText: {
      color: colors[theme].PINK_700,
      fontWeight: '600',
    },
    todayLabel: {
      position: 'absolute',
      top: 2,
      fontSize: 10,
      color: colors[theme].PINK_700,
      fontWeight: '700',
    },
    selectedContainer: {
      backgroundColor: colors[theme].PINK_700,
    },
    selectedTodayContainer: {
      backgroundColor: colors[theme].PINK_700,
      borderColor: colors[theme].PINK_700,
    },
    selectedDateText: {
      color: colors[theme].WHITE,
      fontWeight: '600',
    },
    scheduleIndicator: {
      width: 4,
      height: 4,
      borderRadius: 2,
      backgroundColor: colors[theme].PINK_700,
      marginTop: 2,
    },
    rangeContainer: {
      backgroundColor: colors[theme].PINK_50,
    },
    rangeText: {
      color: colors[theme].PINK_700,
    },
  });

export default DateBox; 
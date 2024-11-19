import React, { useState } from 'react';
import {View, Text, Pressable, StyleSheet, FlatList, Modal, Dimensions} from 'react-native';
import Ionicons from 'react-native-vector-icons/Ionicons';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';
import {colors} from '@/constants';
import {ThemeMode} from '@/types';
import DateBox from './DateBox';
import DayOfWeeks from './DayOfWeeks';
import CalendarFooter from './CalendarFooter';
import useThemeStore from '@/store/useThemeStore';
import useModal from '@/hooks/useModal';
import {isSameAsCurrentDate} from '@/utils/date';
import { GestureHandlerRootView, PanGestureHandler } from 'react-native-gesture-handler';
import DatePicker from 'react-native-date-picker';

interface CalendarProps<T> {
  monthYear: {
    lastDate: number;
    firstDOW: number;
    year: number;
    month: number;
  };
  selectedDate: Date;
  selectedEndDate?: Date;
  schedules: {
    [key: string]: T[];
  };
  onPressDate: (date: Date) => void;
  onChangeMonth: (amount: number) => void;
  moveToToday: () => void;
  renderFooter?: (props: any) => React.ReactNode;
  onSelectDateRange?: (start: Date, end: Date) => void;
  onPressAdd?: () => void;
  onCalendarLayout?: (height: number) => void;
}

const deviceWidth = Dimensions.get('window').width;

function Calendar<T>({
  monthYear,
  selectedDate,
  selectedEndDate,
  schedules = {},
  onPressDate,
  onChangeMonth,
  moveToToday,
  renderFooter,
  onSelectDateRange,
  onPressAdd,
  onCalendarLayout,
}: CalendarProps<T>) {
  const {theme} = useThemeStore();
  const styles = styling(theme);
  const {lastDate, firstDOW, year, month} = monthYear;
  const [isChangingMonth, setIsChangingMonth] = useState(false);
  const [showDatePicker, setShowDatePicker] = useState(false);
  const [tempDate, setTempDate] = useState(selectedDate);

  const handleDatePress = (date: number) => {
    if (onSelectDateRange && selectedDate && !selectedEndDate) {
      const endDate = new Date(year, month - 1, date);
      if (endDate >= selectedDate) {
        onSelectDateRange(selectedDate, endDate);
      } else {
        onSelectDateRange(endDate, selectedDate);
      }
    } else {
      const newDate = new Date(year, month - 1, date);
      onPressDate(newDate);
    }
  };

  const isInRange = (date: number) => {
    if (!selectedEndDate || !selectedDate) return false;
    const currentDate = new Date(year, month - 1, date);
    return currentDate >= selectedDate && currentDate <= selectedEndDate;
  };

  const handleConfirmDate = (date: Date) => {
    const yearDiff = date.getFullYear() - monthYear.year;
    const monthDiff = date.getMonth() + 1 - monthYear.month;
    const totalMonthDiff = yearDiff * 12 + monthDiff;
    
    onChangeMonth(totalMonthDiff);
    setShowDatePicker(false);
  };

  return (
    <GestureHandlerRootView style={styles.rootContainer}>
      <PanGestureHandler
        onGestureEvent={({nativeEvent}) => {
          if (isChangingMonth) return;
          
          if (nativeEvent.translationX > 100) {
            setIsChangingMonth(true);
            onChangeMonth(-1);
            setTimeout(() => setIsChangingMonth(false), 500);
          } else if (nativeEvent.translationX < -100) {
            setIsChangingMonth(true);
            onChangeMonth(1);
            setTimeout(() => setIsChangingMonth(false), 500);
          }
        }}>
        <View style={styles.mainContainer}>
          <View style={styles.headerContainer}>
            <Pressable onPress={() => onChangeMonth(-1)} style={styles.monthButtonContainer}>
              <Ionicons name="arrow-back" size={25} color={colors[theme].BLACK} />
            </Pressable>
            <Pressable 
              style={styles.dateSelector} 
              onPress={() => setShowDatePicker(true)}
            >
              <Text style={styles.titleText}>
                {monthYear.year}년 {monthYear.month}월
              </Text>
              <MaterialIcons 
                name="keyboard-arrow-down" 
                size={20} 
                color={colors[theme].GRAY_500} 
              />
            </Pressable>
            <Pressable onPress={() => onChangeMonth(1)} style={styles.monthButtonContainer}>
              <Ionicons name="arrow-forward" size={25} color={colors[theme].BLACK} />
            </Pressable>
          </View>

          <DayOfWeeks />
          
          <View style={styles.bodyContainer}>
            <FlatList
              data={Array.from({length: lastDate + firstDOW}, (_, i) => ({
                id: i,
                date: i - firstDOW + 1,
              }))}
              renderItem={({item}) => (
                <DateBox
                  date={item.date}
                  isToday={isSameAsCurrentDate(year, month, item.date)}
                  hasSchedule={Boolean(schedules[`${year}-${month}-${item.date}`])}
                  selectedDate={selectedDate.getDate()}
                  isInRange={isInRange(item.date)}
                  onPressDate={handleDatePress}
                />
              )}
              keyExtractor={item => String(item.id)}
              numColumns={7}
            />
          </View>

          {renderFooter && (
            <CalendarFooter
              month={new Date(year, month - 1)}
              renderFooter={renderFooter}
            />
          )}

          <DatePicker
            modal
            open={showDatePicker}
            date={tempDate}
            onConfirm={handleConfirmDate}
            onCancel={() => setShowDatePicker(false)}
            mode="date"
            locale="ko"
            title="날짜 선택"
            confirmText="확인"
            cancelText="취소"
          />
        </View>
      </PanGestureHandler>
    </GestureHandlerRootView>
  );
}

const styling = (theme: ThemeMode) =>
  StyleSheet.create({
    rootContainer: {
      backgroundColor: colors[theme].WHITE,
      width: '100%',
    },
    mainContainer: {
      backgroundColor: colors[theme].WHITE,
      borderRadius: 15,
      margin: 16,
      shadowColor: colors[theme].BLACK,
      shadowOffset: {
        width: 0,
        height: 2,
      },
      shadowOpacity: 0.1,
      shadowRadius: 4,
      elevation: 4,
      borderWidth: 1,
      borderColor: colors[theme].GRAY_100,
    },
    headerContainer: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      paddingHorizontal: 16,
      paddingVertical: 12,
      borderBottomWidth: 1,
      borderBottomColor: colors[theme].GRAY_100,
    },
    bodyContainer: {
      width: '100%',
    },
    monthButtonContainer: {
      padding: 8,
    },
    selectors: {
      flexDirection: 'row',
      alignItems: 'center',
      gap: 8,
    },
    dateSelector: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'center',
      backgroundColor: colors[theme].PINK_50,
      paddingVertical: 6,
      paddingHorizontal: 12,
      borderRadius: 8,
      height: 32,
    },
    titleText: {
      fontSize: 16,
      fontWeight: '600',
      color: colors[theme].PINK_700,
      marginRight: 4,
      lineHeight: 20,
      textAlignVertical: 'center',
    },
    modalContainer: {
      flex: 1,
      backgroundColor: 'rgba(0, 0, 0, 0.5)',
      justifyContent: 'center',
      alignItems: 'center',
    },
    pickerContainer: {
      width: '80%',
      backgroundColor: colors[theme].WHITE,
      borderRadius: 10,
      overflow: 'hidden',
    },
    pickerHeader: {
      padding: 15,
      borderBottomWidth: 1,
      borderBottomColor: colors[theme].GRAY_200,
      alignItems: 'center',
    },
    pickerTitle: {
      fontSize: 16,
      fontWeight: '600',
      color: colors[theme].BLACK,
    },
    closeButton: {
      padding: 15,
      borderTopWidth: 1,
      borderTopColor: colors[theme].GRAY_200,
      alignItems: 'center',
    },
    closeText: {
      fontSize: 16,
      fontWeight: '600',
      color: colors[theme].PINK_700,
    },
    addButton: {
      position: 'absolute',
      right: 20,
      bottom: 20,
      width: 56,
      height: 56,
      borderRadius: 28,
      backgroundColor: colors[theme].PINK_500,
      justifyContent: 'center',
      alignItems: 'center',
      elevation: 4,
      shadowColor: colors[theme].BLACK,
      shadowOffset: {
        width: 0,
        height: 2,
      },
      shadowOpacity: 0.25,
      shadowRadius: 3.84,
    },
  });

export default Calendar; 
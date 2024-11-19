import React, {useState, useCallback, useEffect} from 'react';
import {StyleSheet, View, Text, TouchableOpacity, Pressable, Alert, Dimensions} from 'react-native';
import {useNavigation} from '@react-navigation/native';
import Ionicons from 'react-native-vector-icons/Ionicons';
import Calendar from '@/components/calendar/Calendar';
import {getMonthYearDetails} from '@/utils/date';
import {CalendarPost} from '@/types/calendar';
import {colors} from '@/constants';
import useThemeStore, { ThemeMode } from '@/store/useThemeStore';
import useModal from '@/hooks/useModal';
import AddScheduleModal from '@/components/calendar/AddScheduleModal';
import {DrawerScreenProps} from '@react-navigation/drawer';
import {MainDrawerParamList} from '@/navigations/drawer/MainDrawerNavigator';
import { getEncryptStorage, setEncryptStorage } from '@/utils/encryptStorage';
import {StackScreenProps} from '@react-navigation/stack';
import {CalendarStackParamList} from '@/navigations/stack/CalendarStackNavigator';
import ScheduleBottomSheet from '@/components/calendar/ScheduleBottomSheet';
import {CompoundOption} from '@/components/common/CompoundOption';

type CalendarScreenProps = StackScreenProps<CalendarStackParamList>;

const deviceWidth = Dimensions.get('window').width;

interface ScheduleRecord {
  [key: string]: CalendarPost[];
}

function CalendarHomeScreen({ navigation, route }: CalendarScreenProps) {
  const {theme} = useThemeStore();
  const styles = styling(theme);
  const [selectedDate, setSelectedDate] = useState(new Date());
  const monthYear = getMonthYearDetails(selectedDate);
  const [schedules, setSchedules] = useState<ScheduleRecord>({});
  const addScheduleModal = useModal();
  const deleteConfirmModal = useModal();
  const [calendarHeight, setCalendarHeight] = useState(0);

  useEffect(() => {
    loadSchedules();
  }, []);

  useEffect(() => {
    if (route.params?.showDeleteConfirm) {
      deleteConfirmModal.show();
      navigation.setParams({ showDeleteConfirm: undefined });
    }
  }, [route.params?.showDeleteConfirm]);

  const handleDeleteAllSchedules = async () => {
    try {
      await setEncryptStorage('schedules', {});
      setSchedules({});
      deleteConfirmModal.hide();
    } catch (error) {
      console.error('전체 일정 삭제 실패:', error);
    }
  };

  const loadSchedules = async () => {
    try {
      const savedSchedules = await getEncryptStorage('schedules');
      if (savedSchedules) {
        setSchedules(savedSchedules);
      }
    } catch (error) {
      console.error('스케줄 로딩 실패:', error);
    }
  };

  const saveSchedule = async (scheduleData: Omit<CalendarPost, 'date'>) => {
    try {
      const year = selectedDate.getFullYear();
      const month = selectedDate.getMonth() + 1;
      const date = selectedDate.getDate();
      const dateKey = `${year}-${month}-${date}`;
      
      const newSchedules = {...schedules};
      if (!newSchedules[dateKey]) {
        newSchedules[dateKey] = [];
      }
      
      const schedule = {
        ...scheduleData,
        date: selectedDate,
      };
      
      newSchedules[dateKey].push(schedule);
      await setEncryptStorage('schedules', newSchedules);
      setSchedules(newSchedules);
      addScheduleModal.hide();
    } catch (error) {
      console.error('스케줄 저장 실패:', error);
    }
  };

  const handlePressDate = useCallback((date: Date) => {
    setSelectedDate(date);
  }, []);

  const handleChangeMonth = useCallback((amount: number) => {
    setSelectedDate(prev => {
      const newDate = new Date(prev);
      newDate.setMonth(prev.getMonth() + amount);
      return newDate;
    });
  }, []);

  const moveToToday = useCallback(() => {
    setSelectedDate(new Date());
  }, []);

  const deleteSchedule = async (scheduleId: number) => {
    try {
      const year = selectedDate.getFullYear();
      const month = selectedDate.getMonth() + 1;
      const date = selectedDate.getDate();
      const dateKey = `${year}-${month}-${date}`;
      
      const newSchedules = {...schedules};
      if (newSchedules[dateKey]) {
        newSchedules[dateKey] = newSchedules[dateKey].filter(
          schedule => schedule.id !== scheduleId
        );
        
        if (newSchedules[dateKey].length === 0) {
          delete newSchedules[dateKey];
        }
        
        await setEncryptStorage('schedules', newSchedules);
        setSchedules(newSchedules);
      }
    } catch (error) {
      console.error('일정 삭제 실패:', error);
    }
  };

  const getSchedulesForDate = (date: Date) => {
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const dateKey = `${year}-${month}-${day}`;
    return schedules[dateKey] || [];
  };

  const handleCalendarLayout = (event: any) => {
    setCalendarHeight(event.nativeEvent.layout.height);
  };

  return (
    <View style={styles.container}>
      <Calendar
        monthYear={monthYear}
        selectedDate={selectedDate}
        schedules={schedules}
        onPressDate={handlePressDate}
        onChangeMonth={handleChangeMonth}
        moveToToday={moveToToday}
        onPressAdd={addScheduleModal.show}
        onCalendarLayout={handleCalendarLayout}
      />
      <ScheduleBottomSheet
        schedules={getSchedulesForDate(selectedDate)}
        selectedDate={selectedDate}
        onDeleteSchedule={deleteSchedule}
        onPressAdd={addScheduleModal.show}
      />
      <AddScheduleModal
        isVisible={addScheduleModal.isVisible}
        onClose={addScheduleModal.hide}
        onSave={saveSchedule}
        date={selectedDate}
      />
      <CompoundOption isVisible={deleteConfirmModal.isVisible} hideOption={deleteConfirmModal.hide}>
        <CompoundOption.Background>
          <CompoundOption.Container>
            <View style={styles.deleteConfirmContainer}>
              <Text style={styles.deleteConfirmTitle}>전체 일정 삭제</Text>
              <Text style={styles.deleteConfirmMessage}>
                모든 일정이 삭제됩니다. 계속하시겠습니까?
              </Text>
            </View>
            <CompoundOption.Button onPress={handleDeleteAllSchedules} isDanger>
              삭제
            </CompoundOption.Button>
            <CompoundOption.Divider />
            <CompoundOption.Button onPress={deleteConfirmModal.hide}>
              취소
            </CompoundOption.Button>
          </CompoundOption.Container>
        </CompoundOption.Background>
      </CompoundOption>
    </View>
  );
}

const styling = (theme: ThemeMode) =>
  StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: colors[theme].GRAY_100,
    },
    calendarContainer: {
      width: deviceWidth,
      backgroundColor: colors[theme].WHITE,
      borderBottomLeftRadius: 20,
      borderBottomRightRadius: 20,
      shadowColor: colors[theme].BLACK,
      shadowOffset: {
        width: 0,
        height: 2,
      },
      shadowOpacity: 0.1,
      shadowRadius: 3,
      elevation: 3,
      marginBottom: 8,
    },
    deleteConfirmContainer: {
      padding: 20,
      alignItems: 'center',
    },
    deleteConfirmTitle: {
      fontSize: 18,
      fontWeight: 'bold',
      color: colors[theme].BLACK,
      marginBottom: 10,
    },
    deleteConfirmMessage: {
      fontSize: 14,
      color: colors[theme].GRAY_700,
      textAlign: 'center',
    },
  });

export default CalendarHomeScreen;

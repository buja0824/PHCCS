import React from 'react';
import {View, Text, StyleSheet, Pressable} from 'react-native';
import {colors} from '@/constants';
import {ThemeMode} from '@/types';
import {CalendarPost} from '@/types/calendar';
import useThemeStore from '@/store/useThemeStore';
import BottomSheet, { BottomSheetScrollView } from '@gorhom/bottom-sheet';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';
import {CompoundOption} from '@/components/common/CompoundOption';
import useModal from '@/hooks/useModal';

interface ScheduleBottomSheetProps {
  schedules: CalendarPost[];
  selectedDate: Date;
  onDeleteSchedule: (id: number) => void;
  onPressAdd: () => void;
}

function ScheduleBottomSheet({
  schedules,
  selectedDate,
  onDeleteSchedule,
  onPressAdd,
}: ScheduleBottomSheetProps) {
  const {theme} = useThemeStore();
  const styles = styling(theme);
  const deleteModal = useModal();
  const [selectedScheduleId, setSelectedScheduleId] = React.useState<number | null>(null);
  const snapPoints = React.useMemo(() => ['30%', '60%'], []);

  const handleDeleteConfirm = () => {
    if (selectedScheduleId) {
      onDeleteSchedule(selectedScheduleId);
      deleteModal.hide();
    }
  };

  const handleLongPress = (scheduleId: number) => {
    setSelectedScheduleId(scheduleId);
    deleteModal.show();
  };

  return (
    <>
      <BottomSheet
        snapPoints={snapPoints}
        index={0}
        backgroundStyle={styles.bottomSheetBackground}
        handleIndicatorStyle={styles.indicator}
      >
        <View style={styles.container}>
          <View style={styles.headerContainer}>
            <View>
              <Text style={styles.dateText}>
                {selectedDate.getFullYear()}년 {selectedDate.getMonth() + 1}월{' '}
                {selectedDate.getDate()}일
              </Text>
              <Text style={styles.scheduleCount}>
                총 {schedules.length}개의 일정
              </Text>
            </View>
            <Pressable onPress={onPressAdd} style={styles.addButton}>
              <MaterialIcons name="add" size={24} color={colors[theme].PINK_500} />
            </Pressable>
          </View>
          
          <BottomSheetScrollView 
            showsVerticalScrollIndicator={true}
            contentContainerStyle={styles.scrollViewContent}
          >
            {schedules?.length > 0 ? (
              schedules.map(schedule => (
                <Pressable
                  key={schedule.id}
                  style={({pressed}) => [
                    styles.scheduleItem,
                    {
                      backgroundColor: pressed 
                        ? colors[theme].GRAY_100 
                        : colors[theme].GRAY_50,
                    }
                  ]}
                  android_ripple={{
                    color: colors[theme].GRAY_200,
                    borderless: false,
                  }}
                  onLongPress={() => handleLongPress(schedule.id)}>
                  <Text style={styles.scheduleContent}>{schedule.content}</Text>
                </Pressable>
              ))
            ) : (
              <Text style={styles.noScheduleText}>등록된 일정이 없습니다.</Text>
            )}
          </BottomSheetScrollView>
        </View>
      </BottomSheet>

      <CompoundOption isVisible={deleteModal.isVisible} hideOption={deleteModal.hide}>
        <CompoundOption.Background>
          <CompoundOption.Container>
            <CompoundOption.Button onPress={handleDeleteConfirm} isDanger>
              삭제
            </CompoundOption.Button>
            <CompoundOption.Divider />
            <CompoundOption.Button onPress={deleteModal.hide}>
              취소
            </CompoundOption.Button>
          </CompoundOption.Container>
        </CompoundOption.Background>
      </CompoundOption>
    </>
  );
}

const styling = (theme: ThemeMode) =>
  StyleSheet.create({
    bottomSheetBackground: {
      backgroundColor: colors[theme].WHITE,
      borderTopLeftRadius: 20,
      borderTopRightRadius: 20,
      shadowColor: colors[theme].BLACK,
      shadowOffset: {
        width: 0,
        height: -4,
      },
      shadowOpacity: 0.1,
      shadowRadius: 4,
      elevation: 5,
    },
    indicator: {
      backgroundColor: colors[theme].GRAY_300,
      width: 40,
      height: 4,
    },
    container: {
      flex: 1,
      padding: 20,
    },
    scrollViewContent: {
      paddingBottom: 20,
    },
    headerContainer: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      marginBottom: 20,
      borderBottomWidth: 1,
      borderBottomColor: colors[theme].GRAY_200,
      paddingBottom: 15,
    },
    dateText: {
      fontSize: 20,
      fontWeight: '600',
      color: colors[theme].BLACK,
      marginBottom: 8,
    },
    scheduleCount: {
      fontSize: 14,
      color: colors[theme].GRAY_500,
    },
    scheduleItem: {
      backgroundColor: colors[theme].GRAY_50,
      padding: 16,
      borderRadius: 12,
      marginBottom: 10,
      borderLeftWidth: 4,
      borderLeftColor: colors[theme].PINK_500,
      shadowColor: colors[theme].BLACK,
      shadowOffset: {
        width: 0,
        height: 2,
      },
      shadowOpacity: 0.1,
      shadowRadius: 3,
      elevation: 2,
    },
    scheduleContent: {
      fontSize: 16,
      color: colors[theme].BLACK,
    },
    noScheduleText: {
      textAlign: 'center',
      color: colors[theme].GRAY_500,
      fontSize: 16,
      marginTop: 20,
    },
    addButton: {
      width: 40,
      height: 40,
      borderRadius: 20,
      backgroundColor: colors[theme].PINK_50,
      justifyContent: 'center',
      alignItems: 'center',
    },
  });

export default ScheduleBottomSheet; 
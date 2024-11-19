import React, {useState} from 'react';
import {Modal, Pressable, StyleSheet, Text, View} from 'react-native';
import {colors} from '@/constants';
import useThemeStore from '@/store/useThemeStore';
import {ThemeMode} from '@/types';
import WheelPicker from './WheelPicker';

interface MonthSelectorProps {
  isVisible: boolean;
  currentMonth: number;
  onChangeMonth: (month: number) => void;
  hide: () => void;
}

function MonthSelector({isVisible, currentMonth, onChangeMonth, hide}: MonthSelectorProps) {
  const {theme} = useThemeStore();
  const styles = styling(theme);
  const [selectedMonth, setSelectedMonth] = useState(currentMonth);

  const handleConfirm = () => {
    onChangeMonth(selectedMonth);
    hide();
  };

  const months = Array.from({length: 12}, (_, i) =>
    (i + 1).toString().padStart(2, '0'),
  );

  return (
    <Modal visible={isVisible} transparent animationType="fade">
      <View style={styles.modalContainer}>
        <View style={styles.pickerContainer}>
          <View style={styles.header}>
            <Text style={styles.headerText}>월 선택</Text>
          </View>
          <View style={styles.pickerWrapper}>
            <WheelPicker
              items={months}
              onItemChange={month => setSelectedMonth(parseInt(month))}
              itemHeight={50}
              initValue={currentMonth.toString().padStart(2, '0')}
              containerStyle={styles.monthPicker}
            />
            <Text style={styles.monthText}>월</Text>
          </View>
          <Pressable style={styles.confirmButton} onPress={handleConfirm}>
            <Text style={styles.confirmText}>확인</Text>
          </Pressable>
        </View>
      </View>
    </Modal>
  );
}

const styling = (theme: ThemeMode) =>
  StyleSheet.create({
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
    header: {
      padding: 20,
      borderBottomWidth: 1,
      borderBottomColor: colors[theme].GRAY_200,
      alignItems: 'center',
    },
    headerText: {
      fontSize: 18,
      fontWeight: '600',
      color: colors[theme].BLACK,
    },
    pickerWrapper: {
      flexDirection: 'row',
      justifyContent: 'center',
      alignItems: 'center',
      padding: 30,
    },
    monthPicker: {
      width: 100,
      height: 150,
    },
    monthText: {
      fontSize: 16,
      fontWeight: '600',
      color: colors[theme].BLACK,
      marginLeft: 10,
    },
    confirmButton: {
      padding: 18,
      borderTopWidth: 1,
      borderTopColor: colors[theme].GRAY_200,
      alignItems: 'center',
    },
    confirmText: {
      fontSize: 16,
      fontWeight: '600',
      color: colors[theme].PINK_500,
    },
  });

export default MonthSelector; 
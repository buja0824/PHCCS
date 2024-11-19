import React, {useState} from 'react';
import {Modal, StyleSheet, View, Text, TextInput, TouchableOpacity} from 'react-native';
import {colors} from '@/constants';
import useThemeStore from '@/store/useThemeStore';
import {CalendarPost} from '@/types/calendar';
import {ThemeMode} from '@/types';

interface AddScheduleModalProps {
  isVisible: boolean;
  onClose: () => void;
  onSave: (schedule: Omit<CalendarPost, 'date'>) => void;
  date: Date;
}

function AddScheduleModal({isVisible, onClose, onSave, date}: AddScheduleModalProps) {
  const {theme} = useThemeStore();
  const [content, setContent] = useState('');
  const [error, setError] = useState('');

  const handleSave = () => {
    if (!content.trim()) {
      setError('내용을 입력해주세요');
      return;
    }
    setError('');
    onSave({
      id: Date.now(),
      content: content.trim(),
    });
    setContent('');
    onClose();
  };

  return (
    <Modal visible={isVisible} transparent animationType="fade">
      <View style={styles.overlay}>
        <View style={[styles.container, {backgroundColor: colors[theme].WHITE}]}>
          <Text style={[styles.dateText, {color: colors[theme].BLACK}]}>
            {date.getFullYear()}년 {date.getMonth() + 1}월 {date.getDate()}일
          </Text>
          <TextInput
            style={[styles.input, {borderColor: colors[theme].GRAY_200}]}
            placeholder="내용을 입력하세요"
            value={content}
            onChangeText={(text: string) => setContent(text)}
            multiline={false}
            maxLength={100}
            placeholderTextColor={colors[theme].GRAY_500}
          />
          {error ? (
            <Text style={[styles.errorText, {color: colors[theme].RED_500}]}>
              {error}
            </Text>
          ) : null}
          <View style={styles.buttonContainer}>
            <TouchableOpacity onPress={onClose}>
              <Text style={[styles.buttonText, {color: colors[theme].GRAY_500}]}>취소</Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={handleSave}>
              <Text style={[styles.buttonText, {color: colors[theme].BLUE_500}]}>저장</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  container: {
    width: '100%',
    borderRadius: 10,
    padding: 20,
  },
  dateText: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 20,
    textAlign: 'center',
  },
  input: {
    borderWidth: 1,
    borderRadius: 5,
    padding: 10,
    marginBottom: 15,
    fontSize: 16,
  },
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 20,
  },
  buttonText: {
    fontSize: 16,
    fontWeight: '500',
    paddingVertical: 10,
    paddingHorizontal: 20,
  },
  errorText: {
    fontSize: 14,
    marginBottom: 10,
    textAlign: 'center',
  },
});

export default AddScheduleModal; 
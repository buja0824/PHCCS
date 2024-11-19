import React from 'react';
import {StyleSheet, Text, TouchableOpacity} from 'react-native';
import useThemeStore from '@/store/useThemeStore';
import {colors} from '@/constants';

function CalendarHomeHeaderRight(onPress: () => void) {
  const {theme} = useThemeStore();
  
  return (
    <TouchableOpacity style={styles.button} onPress={onPress}>
      <Text style={[styles.text, {color: colors[theme].BLACK}]}>오늘</Text>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  button: {
    padding: 8,
    marginRight: 16,
  },
  text: {
    fontSize: 16,
    fontWeight: '500',
  },
});

export default CalendarHomeHeaderRight; 
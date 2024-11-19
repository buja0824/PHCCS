import React from 'react';
import {View, StyleSheet, Pressable, Text, StyleProp, ViewStyle} from 'react-native';
import {colors} from '@/constants';
import useThemeStore from '@/store/useThemeStore';
import {ThemeMode} from '@/types';

interface GenderSelectorProps {
  value: string;
  onChange: (gender: string) => void;
  error?: string;
  touched?: boolean;
}

function GenderSelector({value, onChange, error, touched}: GenderSelectorProps) {
  const {theme} = useThemeStore();
  const styles = styling(theme);

  const getButtonStyle = (gender: string): StyleProp<ViewStyle>[] => {
    return [
      styles.button,
      value === gender && styles.selectedButton,
      touched && error ? styles.errorButton : undefined,
    ].filter(Boolean) as StyleProp<ViewStyle>[];
  };

  return (
    <View>
      <Text style={styles.label}>성별</Text>
      <View style={styles.container}>
        <Pressable
          style={getButtonStyle('수컷')}
          onPress={() => onChange('수컷')}>
          <Text
            style={[
              styles.buttonText,
              value === '수컷' && styles.selectedButtonText,
            ]}>
            수컷
          </Text>
        </Pressable>
        <Pressable
          style={getButtonStyle('암컷')}
          onPress={() => onChange('암컷')}>
          <Text
            style={[
              styles.buttonText,
              value === '암컷' && styles.selectedButtonText,
            ]}>
            암컷
          </Text>
        </Pressable>
      </View>
      {error && touched && <Text style={styles.errorText}>{error}</Text>}
    </View>
  );
}

const styling = (theme: ThemeMode) =>
  StyleSheet.create({
    label: {
      fontSize: 16,
      color: colors[theme].GRAY_500,
      marginBottom: 8,
      marginTop: 16,
    },
    container: {
      flexDirection: 'row',
      gap: 8,
      marginBottom: 16,
    },
    button: {
      flex: 1,
      paddingVertical: 15,
      borderWidth: 1,
      borderColor: colors[theme].GRAY_200,
      borderRadius: 8,
      alignItems: 'center',
    },
    selectedButton: {
      backgroundColor: colors[theme].PINK_50,
      borderColor: colors[theme].PINK_400,
    },
    errorButton: {
      borderColor: colors[theme].RED_500,
    },
    buttonText: {
      color: colors[theme].GRAY_500,
      fontSize: 16,
    },
    selectedButtonText: {
      color: colors[theme].PINK_700,
      fontWeight: '600',
    },
    errorText: {
      color: colors[theme].RED_500,
      fontSize: 12,
      marginTop: -12,
      marginBottom: 16,
    },
  });

export default GenderSelector;

import React from 'react';
import {Pressable, StyleSheet, Text, View} from 'react-native';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';
import {colors} from '@/constants';
import useThemeStore from '@/store/useThemeStore';
import {ThemeMode} from '@/types/common';

interface SettingItemProps {
  title: string;
  onPress?: () => void;
  color?: string;
  icon?: React.ReactNode;
}

function SettingItem({title, onPress, color, icon}: SettingItemProps) {
  const {theme} = useThemeStore();
  const styles = styling(theme);

  return (
    <Pressable 
      style={({pressed}) => [
        styles.container,
        pressed && {
          backgroundColor: colors[theme].GRAY_100,
        },
      ]} 
      android_ripple={{
        color: colors[theme].GRAY_200,
      }}
      onPress={onPress}
    >
      <Text style={[styles.title, color ? {color} : null]}>{title}</Text>
      <View style={styles.iconContainer}>
        {icon || (
          <MaterialIcons
            name="keyboard-arrow-right"
            color={colors[theme].GRAY_500}
            size={20}
          />
        )}
      </View>
    </Pressable>
  );
}

const styling = (theme: ThemeMode) =>
  StyleSheet.create({
    container: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      paddingVertical: 15,
      paddingHorizontal: 20,
      backgroundColor: colors[theme].WHITE,
    },
    title: {
      fontSize: 15,
      color: colors[theme].BLACK,
    },
    iconContainer: {
      flexDirection: 'row',
      alignItems: 'center',
      gap: 5,
    },
  });

export default SettingItem; 
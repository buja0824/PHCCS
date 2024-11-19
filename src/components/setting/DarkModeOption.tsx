import React from 'react';
import {View, Text, Pressable, StyleSheet} from 'react-native';
import {CompoundOption} from '@/components/common/CompoundOption';
import {colors} from '@/constants';
import useThemeStore from '@/store/useThemeStore';

interface DarkModeOptionProps {
  isVisible: boolean;
  hideOption: () => void;
}

function DarkModeOption({isVisible, hideOption}: DarkModeOptionProps) {
  const {theme, setTheme} = useThemeStore();

  const handlePressLight = () => {
    setTheme('light');
    hideOption();
  };

  const handlePressDark = () => {
    setTheme('dark');
    hideOption();
  };

  return (
    <CompoundOption isVisible={isVisible} hideOption={hideOption}>
      <CompoundOption.Background>
        <CompoundOption.Container>
          <CompoundOption.Button
            onPress={handlePressLight}
            isChecked={theme === 'light'}>
            라이트 모드
          </CompoundOption.Button>
          <CompoundOption.Divider />
          <CompoundOption.Button
            onPress={handlePressDark}
            isChecked={theme === 'dark'}>
            다크 모드
          </CompoundOption.Button>
        </CompoundOption.Container>
        <CompoundOption.Container>
          <CompoundOption.Button onPress={hideOption}>
            취소
          </CompoundOption.Button>
        </CompoundOption.Container>
      </CompoundOption.Background>
    </CompoundOption>
  );
}

export default DarkModeOption; 
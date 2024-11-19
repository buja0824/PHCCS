import React from 'react';
import {
  Modal as RNModal,
  Pressable,
  StyleSheet,
  View,
  ViewStyle,
} from 'react-native';
import {colors} from '@/constants';
import useThemeStore from '@/store/useThemeStore';

interface ModalProps {
  visible: boolean;
  onClose: () => void;
  children: React.ReactNode;
  style?: ViewStyle;
}

function Modal({visible, onClose, children, style}: ModalProps) {
  const {theme} = useThemeStore();

  return (
    <RNModal
      visible={visible}
      transparent
      statusBarTranslucent
      animationType="fade"
      onRequestClose={onClose}>
      <Pressable style={styles.overlay} onPress={onClose}>
        <View
          style={[
            styles.content,
            {backgroundColor: colors[theme].WHITE},
            style,
          ]}>
          {children}
        </View>
      </Pressable>
    </RNModal>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  content: {
    width: '80%',
    borderRadius: 8,
    overflow: 'hidden',
  },
});

export default Modal; 
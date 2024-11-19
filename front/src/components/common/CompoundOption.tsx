import {PropsWithChildren, ReactNode, createContext, useContext} from 'react';
import {
  Modal,
  Pressable,
  SafeAreaView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import {colors} from '@/constants';
import useThemeStore from '@/store/useThemeStore';

interface CompoundOptionProps {
  isVisible: boolean;
  hideOption: () => void;
  children: ReactNode;
}

interface ButtonProps extends PropsWithChildren {
  onPress: () => void;
  isChecked?: boolean;
  isDanger?: boolean;
}

const CompoundOptionContext = createContext<{
  hideOption: () => void;
} | null>(null);

const CompoundOption = ({isVisible, hideOption, children}: CompoundOptionProps) => {
  return (
    <Modal visible={isVisible} transparent animationType="fade">
      <CompoundOptionContext.Provider value={{hideOption}}>
        {children}
      </CompoundOptionContext.Provider>
    </Modal>
  );
};

const Background = ({children}: PropsWithChildren) => {
  const {theme} = useThemeStore();
  const context = useContext(CompoundOptionContext);
  
  return (
    <Pressable 
      style={styles.background}
      onPress={context?.hideOption}
    >
      <Pressable style={styles.safeArea}>
        {children}
      </Pressable>
    </Pressable>
  );
};

const Container = ({children}: PropsWithChildren) => {
  const {theme} = useThemeStore();
  
  return (
    <View style={[styles.container, {backgroundColor: colors[theme].WHITE}]}>
      {children}
    </View>
  );
};

const Button = ({children, onPress, isChecked, isDanger}: ButtonProps) => {
  const {theme} = useThemeStore();
  
  return (
    <Pressable 
      style={({pressed}) => [
        styles.button,
        pressed && {
          backgroundColor: colors[theme].GRAY_100
        }
      ]} 
      onPress={onPress}
      android_ripple={{
        color: colors[theme].GRAY_200,
      }}
    >
      <Text
        style={[
          styles.buttonText,
          {
            color: isDanger
              ? colors[theme].RED_500
              : isChecked
              ? colors[theme].BLUE_500
              : colors[theme].BLACK,
          },
        ]}>
        {children}
      </Text>
    </Pressable>
  );
};

const Divider = () => {
  const {theme} = useThemeStore();
  return (
    <View
      style={[styles.divider, {backgroundColor: colors[theme].GRAY_200}]}
    />
  );
};

CompoundOption.Background = Background;
CompoundOption.Container = Container;
CompoundOption.Button = Button;
CompoundOption.Divider = Divider;

const styles = StyleSheet.create({
  background: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.4)',
    justifyContent: 'flex-end',
  },
  safeArea: {
    width: '100%',
  },
  container: {
    marginHorizontal: 10,
    marginBottom: 10,
    borderRadius: 14,
    overflow: 'hidden',
  },
  button: {
    paddingVertical: 15,
    paddingHorizontal: 20,
    width: '100%',
  },
  buttonText: {
    fontSize: 16,
    textAlign: 'center',
  },
  divider: {
    height: 1,
  },
});

export {CompoundOption}; 
import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Dimensions, Animated, Easing } from 'react-native';
import { StackScreenProps } from '@react-navigation/stack';
import { AuthStackParamList } from '@/navigations/stack/AuthStackNavigator';
import { authNavigations, colors } from '@/constants';
import CustomButton from '@/components/CustomButton';

type Props = StackScreenProps<AuthStackParamList, typeof authNavigations.USER_TYPE_SELECTION>;

function UserTypeSelectionScreen({ navigation }: Props) {
  const fadeAnim1 = React.useRef(new Animated.Value(0)).current;
  const fadeAnim2 = React.useRef(new Animated.Value(0)).current;

  React.useEffect(() => {
    Animated.sequence([
      Animated.timing(fadeAnim1, {
        toValue: 1,
        duration: 1000,
        useNativeDriver: true,
        easing: Easing.ease,
      }),
      Animated.timing(fadeAnim2, {
        toValue: 1,
        duration: 1000,
        useNativeDriver: true,
        easing: Easing.ease,
      })
    ]).start();
  }, [fadeAnim1, fadeAnim2]);

  const handleUserTypeSelection = (role: number) => {
    navigation.navigate(authNavigations.SIGNUP, { role });
  };

  return (
    <View style={styles.container}>
      <View style={styles.titleContainer}>
        <Animated.Text style={[styles.titleTop, { opacity: fadeAnim1 }]}>어떤 유형의</Animated.Text>
        <Animated.Text style={[styles.titleBottom, { opacity: fadeAnim2 }]}>사용자인가요?</Animated.Text>
      </View>
      <View style={styles.buttonContainer}>
        <CustomButton
          label="일반 사용자"
          variant="filled"
          onPress={() => handleUserTypeSelection(0)}
        />
        <CustomButton
          label="수의사"
          variant="outlined"
          onPress={() => handleUserTypeSelection(1)}
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    marginHorizontal: 30,
    marginVertical: 30,
  },
  titleContainer: {
    flex: 1.8,
    width: '100%',
    alignItems: 'flex-start',
    justifyContent: 'center',
  },
  titleTop: {
    fontSize: Dimensions.get('screen').width * 0.1,
    fontWeight: '900',
    color: colors.light.BLACK,
    textShadowColor: 'rgba(0, 0, 0, 0.3)',
    textShadowOffset: { width: 3, height: 2 },
    textShadowRadius: 5,
  },
  titleBottom: {
    fontSize: Dimensions.get('screen').width * 0.1,
    fontWeight: '900',
    color: colors.light.BLACK,
    marginTop: 10,
    textShadowColor: 'rgba(0, 0, 0, 0.3)',
    textShadowOffset: { width: 3, height: 2 },
    textShadowRadius: 5,
  },
  buttonContainer: {
    flex: 1,
    alignItems: 'center',
    gap: 10,
  },
});

export default UserTypeSelectionScreen;

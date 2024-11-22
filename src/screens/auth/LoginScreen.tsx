import React, {useRef} from 'react';
import {SafeAreaView, StyleSheet, TextInput, View} from 'react-native';

import InputField from '@/components/InputField';
import CustomButton from '@/components/CustomButton';
import useForm from '@/hooks/useForm';
import useAuth from '@/hooks/queries/useAuth';
import {validateLogin} from '@/utils';

function LoginScreen() {
  const {loginMutation} = useAuth();
  const pwdRef = useRef<TextInput | null>(null);
  const login = useForm({
    initialValue: {email: '', pwd: ''},
    validate: validateLogin,
  });

  const handleSubmit = async () => {
    // 유효성 검사
    const errors = validateLogin(login.values);
    const hasErrors = Object.values(errors).some(error => error !== '');

    if (hasErrors) {
      login.setErrors(errors);
      return;
    }

    try {
      console.log('Attempting login with:', login.values);
      await loginMutation.mutateAsync(login.values);
      console.log('로그인 성공');
    } catch (error: any) {
      console.error('Error during login:', error);
      if (error.response) {
        console.log('Server response:', error.response.data);
        const errorMessage = error.response.data.includes('아이디 또는 비밀번호가') 
          ? '아이디 또는 비밀번호가 다릅니다.'
          : '로그인 중 오류가 발생했습니다.';
        
        login.setErrors({
          email: errorMessage,
          pwd: '',
        });
      } else {
        login.setErrors({
          email: '네트워크 오류가 발생했습니다.',
          pwd: '',
        });
      }
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.inputContainer}>
        <InputField
          autoFocus
          placeholder="이메일"
          error={login.errors.email}
          touched={login.touched.email}
          inputMode="email"
          returnKeyType="next"
          blurOnSubmit={false}
          onSubmitEditing={() => pwdRef.current?.focus()}
          {...login.getTextInputProps('email')}
        />
        <InputField
          ref={pwdRef}
          placeholder="비밀번호"
          error={login.errors.pwd}
          touched={login.touched.pwd}
          secureTextEntry
          returnKeyType="join"
          onSubmitEditing={handleSubmit}
          {...login.getTextInputProps('pwd')}
        />
      </View>
      <CustomButton
        label="로그인"
        variant="filled"
        size="large"
        onPress={handleSubmit}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    margin: 30,
  },
  inputContainer: {
    gap: 20,
    marginBottom: 30,
  },
});

export default LoginScreen;

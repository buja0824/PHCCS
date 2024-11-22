import React, {useRef, useState} from 'react';
import {SafeAreaView, StyleSheet, TextInput, View, ScrollView, TouchableOpacity, Text} from 'react-native';
import InputField from '@/components/InputField';
import CustomButton from '@/components/CustomButton';
import useForm from '@/hooks/useForm';
import useAuth from '@/hooks/queries/useAuth';
import {validateSignup, validateVetSignup} from '@/utils';
import { StackScreenProps } from '@react-navigation/stack';
import { AuthStackParamList } from '@/navigations/stack/AuthStackNavigator';
import { authNavigations } from '@/constants';
import Postcode from '@actbase/react-daum-postcode';
import { CompoundOption } from '@/components/common/CompoundOption';
import { colors } from '@/constants';
import { useTheme } from '@react-navigation/native';

function SignupScreen({ route }: StackScreenProps<AuthStackParamList, typeof authNavigations.SIGNUP>) {
  const { role } = route.params;
  const [showPostcode, setShowPostcode] = useState(false);
  const emailRef = useRef<TextInput | null>(null);
  const pwdRef = useRef<TextInput | null>(null);
  const pwdConfirmRef = useRef<TextInput | null>(null);
  const nicknameRef = useRef<TextInput | null>(null);
  const hospitalNameRef = useRef<TextInput | null>(null);
  const licenseNoRef = useRef<TextInput | null>(null);
  const {signupMutation, vetSignupMutation, loginMutation} = useAuth();
  const [showApprovalModal, setShowApprovalModal] = useState(false);

  const signup = useForm({
    initialValue: {
      email: '', 
      pwd: '', 
      pwdConfirm: '', 
      nickName: '', 
      role: role,
      hospitalName: '',
      hospitalAddr: '',
      licenseNo: ''
    },
    validate: role === 1 ? validateVetSignup : validateSignup,
  });

  const handleSubmit = async () => {
    // 기본 유효성 검사
    const baseErrors = validateSignup(signup.values);
    let hasErrors = Object.values(baseErrors).some(error => error !== '');
    
    // 기본 필드에 에러가 있을 경우 처리
    if (hasErrors) {
      signup.setErrors(baseErrors);
      if (baseErrors.email) emailRef.current?.focus();
      else if (baseErrors.pwd) pwdRef.current?.focus();
      else if (baseErrors.pwdConfirm) pwdConfirmRef.current?.focus();
      else if (baseErrors.nickName) nicknameRef.current?.focus();
      return;
    }

    // 수의사인 경우 추가 유효성 검사
    if (role === 1) {
      const vetErrors = validateVetSignup(signup.values);
      hasErrors = Object.values(vetErrors).some(error => error !== '');
      
      if (hasErrors) {
        signup.setErrors(vetErrors);
        if (vetErrors.hospitalName) hospitalNameRef.current?.focus();
        else if (vetErrors.hospitalAddr) setShowPostcode(true);
        else if (vetErrors.licenseNo) licenseNoRef.current?.focus();
        return;
      }
      // 수의사인 경우 승인 안내 모달 표시
      setShowApprovalModal(true);
      return;
    }

    try {
      const {email, pwd, nickName, hospitalName, hospitalAddr, licenseNo} = signup.values;
      
      console.log('Current role value:', role);
      
      console.log('Signup data:', {
        email, 
        pwd, 
        nickName, 
        role,
        hospitalName, 
        hospitalAddr, 
        licenseNo
      });
      
      if (role === 1) {
        await vetSignupMutation.mutateAsync({
          email, 
          pwd, 
          nickName, 
          role,
          hospitalName, 
          hospitalAddr, 
          licenseNo
        });
      } else {
        await signupMutation.mutateAsync({
          email,
          pwd,
          nickName,
          role
        });
      }
      
      await loginMutation.mutateAsync({email, pwd, role});
      
    } catch (error: any) {
      console.error('회원가입 중 오류가 발생했습니다:', error);
      if (error.response) {
        console.log('서버 응답:', error.response.data);
        signup.setErrors({
          email: error.response.data.includes('이메일 중복') ? '이미 사용 중인 이메일입니다.' : '',
          nickName: error.response.data.includes('닉네임 중복') ? '이미 사용 중인 닉네임입니다.' : '',
        });
        if (error.response.data.includes('이메일 중복')) {
          emailRef.current?.focus();
        } else if (error.response.data.includes('닉네임 중복')) {
          nicknameRef.current?.focus();
        }
      } else {
        signup.setErrors({
          email: '회원가입 중 오류가 발생했습니다.',
        });
        emailRef.current?.focus();
      }
    }
  };

  const handleApprovalConfirm = async () => {
    setShowApprovalModal(false);
    try {
      const {email, pwd, nickName, hospitalName, hospitalAddr, licenseNo} = signup.values;
      
      await vetSignupMutation.mutateAsync({
        email,
        pwd,
        nickName,
        role,
        hospitalName,
        hospitalAddr,
        licenseNo
      });
      
      await loginMutation.mutateAsync({email, pwd, role});
    } catch (error: any) {
      console.error('회원가입 중 오류가 발생했습니다:', error);
      if (error.response) {
        console.log('서버 응답:', error.response.data);
        signup.setErrors({
          email: error.response.data.includes('이메일 중복') ? '이미 사용 중인 이메일입니다.' : '',
          nickName: error.response.data.includes('닉네임 중복') ? '이미 사용 중인 닉네임입니다.' : '',
        });
        if (error.response.data.includes('이메일 중복')) {
          emailRef.current?.focus();
        } else if (error.response.data.includes('닉네임 중복')) {
          nicknameRef.current?.focus();
        }
      } else {
        signup.setErrors({
          email: '회원가입 중 오류가 발생했습니다.',
        });
        emailRef.current?.focus();
      }
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView 
        style={styles.scrollView}
        contentContainerStyle={{ paddingBottom: 100 }}
      >
        <View style={styles.inputContainer}>
          <InputField
            ref={emailRef}
            autoFocus
            placeholder="이메일"
            error={signup.errors.email}
            touched={signup.touched.email}
            inputMode="email"
            returnKeyType="next"
            blurOnSubmit={false}
            onSubmitEditing={() => pwdRef.current?.focus()}
            {...signup.getTextInputProps('email')}
          />
          <InputField
            ref={pwdRef}
            placeholder="비밀번호"
            error={signup.errors.pwd}
            touched={signup.touched.pwd}
            secureTextEntry
            returnKeyType="next"
            blurOnSubmit={false}
            onSubmitEditing={() => pwdConfirmRef.current?.focus()}
            {...signup.getTextInputProps('pwd')}
          />
          <InputField
            ref={pwdConfirmRef}
            placeholder="비밀번호 확인"
            error={signup.errors.pwdConfirm}
            touched={signup.touched.pwdConfirm}
            secureTextEntry
            returnKeyType="next"
            blurOnSubmit={false}
            onSubmitEditing={() => nicknameRef.current?.focus()}
            {...signup.getTextInputProps('pwdConfirm')}
          />
          <InputField
            ref={nicknameRef}
            placeholder="닉네임"
            error={signup.errors.nickName}
            touched={signup.touched.nickName}
            returnKeyType={role === 1 ? "next" : "done"}
            onSubmitEditing={() => role === 1 ? hospitalNameRef.current?.focus() : handleSubmit()}
            {...signup.getTextInputProps('nickName')}
          />
          
          {role === 1 && (
            <>
              <View style={{ marginTop: 5 }} />
              <InputField
                value={signup.values.hospitalAddr}
                placeholder="병원 주소"
                error={signup.errors.hospitalAddr}
                touched={signup.touched.hospitalAddr}
                editable={false}
              />
              <TouchableOpacity 
                style={styles.addressButton}
                onPress={() => setShowPostcode(true)}
              >
                <Text>주소 찾기</Text>
              </TouchableOpacity>

              <InputField
                ref={hospitalNameRef}
                placeholder="병원 이름"
                error={signup.errors.hospitalName}
                touched={signup.touched.hospitalName}
                returnKeyType="next"
                onSubmitEditing={() => licenseNoRef.current?.focus()}
                {...signup.getTextInputProps('hospitalName')}
              />
              <InputField
                ref={licenseNoRef}
                placeholder="라이센스 번호"
                error={signup.errors.licenseNo}
                touched={signup.touched.licenseNo}
                returnKeyType="done"
                onSubmitEditing={handleSubmit}
                {...signup.getTextInputProps('licenseNo')}
              />
            </>
          )}
        </View>
        
        <CustomButton 
          label="회원가입" 
          onPress={handleSubmit}
        />

      </ScrollView>

      {showPostcode && (
        <View style={styles.postcodeContainer}>
          <Postcode 
            style={styles.postcode}
            onSelected={data => {
              signup.setValue('hospitalAddr', data.address);
              setShowPostcode(false);
            }}
            onError={error => {
              console.error(error);
              setShowPostcode(false);
            }}
          />
        </View>
      )}

      <CompoundOption isVisible={showApprovalModal} hideOption={() => setShowApprovalModal(false)}>
        <CompoundOption.Background>
          <CompoundOption.Container>
            <View style={styles.modalContainer}>
              <Text style={styles.modalTitle}>수의사 회원 승인 안내</Text>
              <Text style={styles.modalText}>
                수의사 회원은 관리자 승인 후{'\n'}
                서비스 이용이 가능합니다.{'\n\n'}
                <Text style={styles.highlightText}>* 승인 완료 시 이메일로 알려드립니다.</Text>
              </Text>
            </View>
          </CompoundOption.Container>
          <CompoundOption.Container>
            <CompoundOption.Button onPress={handleApprovalConfirm}>
              확인
            </CompoundOption.Button>
          </CompoundOption.Container>
          <CompoundOption.Container>
            <CompoundOption.Button onPress={() => setShowApprovalModal(false)} isDanger>
              취소
            </CompoundOption.Button>
          </CompoundOption.Container>
        </CompoundOption.Background>
      </CompoundOption>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  scrollView: {
    flex: 1,
    padding: 30,
  },
  inputContainer: {
    gap: 20,
    marginBottom: 30,
  },
  addressButton: {
    padding: 15,
    backgroundColor: '#f5f5f5',
    borderRadius: 8,
    alignItems: 'center',
    borderWidth: 1,
    borderStyle: 'dotted',
    borderColor: '#000000',
    marginTop: -10,
  },
  postcodeContainer: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'white',
    zIndex: 1,
  },
  postcode: {
    width: '100%',
    height: '100%',
  },
  modalContainer: {
    padding: 24,
    alignItems: 'center',
    gap: 16,
  },
  modalTitle: {
    fontSize: 20,
    fontWeight: '700',
    color: colors.light.BLACK,
    textAlign: 'center',
  },
  modalText: {
    fontSize: 16,
    color: colors.light.GRAY_600,
    textAlign: 'center',
    lineHeight: 24,
  },
  highlightText: {
    fontSize: 14,
    color: colors.light.GRAY_600,
  },
});

export default SignupScreen;

// validate: (values) => role === 1 ? validateVetSignup(values) : validateSignup(values),


// signup.setValue('hospitalAddr', data.address, false);
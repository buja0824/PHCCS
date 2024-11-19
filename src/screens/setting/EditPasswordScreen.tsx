import React, {useState} from 'react';
import {View, StyleSheet, Text} from 'react-native';
import {StackScreenProps} from '@react-navigation/stack';
import {SettingStackParamList} from '@/navigations/stack/SettingStackNavigator';
import useAuth from '@/hooks/queries/useAuth';
import useForm from '@/hooks/useForm';
import {colors, settingNavigations} from '@/constants';
import useThemeStore from '@/store/useThemeStore';
import InputField from '@/components/InputField';
import CustomButton from '@/components/CustomButton';
import {validateEditPassword} from '@/utils/validate';
import useModal from '@/hooks/useModal';
import {CompoundOption} from '@/components/common/CompoundOption';
import {ThemeMode} from '@/types/common';

type EditPasswordScreenProps = StackScreenProps<SettingStackParamList>;

function EditPasswordScreen({navigation}: EditPasswordScreenProps) {
  const {theme} = useThemeStore();
  const styles = styling(theme);
  const {profileMutation} = useAuth();
  const {isVisible, show, hide} = useModal();
  const [errorModalVisible, setErrorModalVisible] = useState(false);

  const editPassword = useForm({
    initialValue: {
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
    },
    validate: validateEditPassword,
  });

  const handleSubmit = () => {
    const {currentPassword, newPassword, confirmPassword} = editPassword.values;
    
    // 유효성 검사
    const errors = validateEditPassword(editPassword.values);
    
    // 모든 필드를 touched 상태로 변경
    Object.keys(editPassword.values).forEach(key => {
      editPassword.handleBlur(key as keyof typeof editPassword.values)();
    });
    
    const hasErrors = Object.values(errors).some(error => error !== '');
    
    if (hasErrors) {
      editPassword.setErrors(errors);
      return;
    }

    profileMutation.mutate(
      {
        currentPwd: currentPassword,
        pwd: newPassword.trim(),
      },
      {
        onSuccess: () => {
          show();
        },
        onError: (error: any) => {
          if (error.response?.data?.includes('현재 비밀번호가 일치하지 않습니다')) {
            editPassword.setErrors({
              currentPassword: '현재 비밀번호가 일치하지 않습니다.',
            });
          } else {
            setErrorModalVisible(true);
          }
        },
      },
    );
  };

  const handleConfirm = () => {
    hide();
    navigation.navigate(settingNavigations.SETTING_HOME);
  };

  return (
    <View style={styles.container}>
      <View style={styles.inputContainer}>
        <InputField
          {...editPassword.getTextInputProps('currentPassword')}
          error={editPassword.errors.currentPassword}
          touched={editPassword.touched.currentPassword}
          placeholder="현재 비밀번호"
          secureTextEntry
        />
        <InputField
          {...editPassword.getTextInputProps('newPassword')}
          error={editPassword.errors.newPassword}
          touched={editPassword.touched.newPassword}
          placeholder="새 비밀번호"
          secureTextEntry
        />
        <InputField
          {...editPassword.getTextInputProps('confirmPassword')}
          error={editPassword.errors.confirmPassword}
          touched={editPassword.touched.confirmPassword}
          placeholder="새 비밀번호 확인"
          secureTextEntry
        />
      </View>
      <CustomButton
        label="변경하기"
        onPress={handleSubmit}
        variant="filled"
      />

      {/* 성공 모달 */}
      <CompoundOption isVisible={isVisible} hideOption={hide}>
        <CompoundOption.Background>
          <CompoundOption.Container>
            <View style={styles.modalContainer}>
              <Text style={styles.modalText}>비밀번호가 변경되었습니다.</Text>
              <CompoundOption.Button onPress={handleConfirm}>
                확인
              </CompoundOption.Button>
            </View>
          </CompoundOption.Container>
        </CompoundOption.Background>
      </CompoundOption>

      {/* 에러 모달 */}
      <CompoundOption isVisible={errorModalVisible} hideOption={() => setErrorModalVisible(false)}>
        <CompoundOption.Background>
          <CompoundOption.Container>
            <View style={styles.modalContainer}>
              <Text style={styles.modalText}>비밀번호 변경 중 오류가 발생했습니다.</Text>
              <CompoundOption.Button onPress={() => setErrorModalVisible(false)}>
                확인
              </CompoundOption.Button>
            </View>
          </CompoundOption.Container>
        </CompoundOption.Background>
      </CompoundOption>
    </View>
  );
}

const styling = (theme: ThemeMode) =>
  StyleSheet.create({
    container: {
      flex: 1,
      padding: 20,
    },
    inputContainer: {
      gap: 20,          // 입력필드들 사이 간격
      marginBottom: 30,  // 입력필드와 버튼 사이 간격
    },
    modalContainer: {
      padding: 20,
      alignItems: 'center',
    },
    modalText: {
      fontSize: 16,
      marginBottom: 20,
      color: colors.light.BLACK,
      textAlign: 'center',
    },
  });

export default EditPasswordScreen; 
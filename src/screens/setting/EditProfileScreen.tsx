import React, {useState} from 'react';
import {View, StyleSheet, Image, Text} from 'react-native';
import {StackScreenProps} from '@react-navigation/stack';
import {SettingStackParamList} from '@/navigations/stack/SettingStackNavigator';
import useAuth from '@/hooks/queries/useAuth';
import useForm from '@/hooks/useForm';
import useThemeStore from '@/store/useThemeStore';
import InputField from '@/components/InputField';
import {colors, settingNavigations} from '@/constants';
import CustomButton from '@/components/CustomButton';
import {CompoundOption} from '@/components/common/CompoundOption';
import useModal from '@/hooks/useModal';
import {ThemeMode} from '@/types/common';
import {ResponseProfile} from '@/api/auth';

type EditProfileScreenProps = StackScreenProps<SettingStackParamList>;

function EditProfileScreen({navigation}: EditProfileScreenProps) {
  const {theme} = useThemeStore();
  const styles = styling(theme);
  const {getProfileQuery, profileMutation} = useAuth();
  const { nickName, imageUri } = getProfileQuery.data || {};  

  const {isVisible, show, hide} = useModal();
  const [errorModalVisible, setErrorModalVisible] = useState(false);

  const editProfile = useForm({
    initialValue: {
      nickName: nickName || '',
    },
    validate: values => {
      const errors: Partial<typeof values> = {};
      if (!values.nickName) {
        errors.nickName = '닉네임을 입력해주세요.';
      }
      return errors;
    },
  });

  const handleSubmit = () => {
    const {nickName} = editProfile.values;
    if (!nickName?.trim()) return;
    
    const updateData = {
      nickname: nickName.trim(),
    };
    
    profileMutation.mutate(
      updateData,
      {
        onSuccess: () => {
          show();
        },
        onError: () => {
          setErrorModalVisible(true);
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
      <View style={styles.imageContainer}>
        {!imageUri ? (
          <Image
            source={require('@/assets/user-default.png')}
            style={styles.profileImage}
          />
        ) : (
          <Image source={{uri: imageUri}} style={styles.profileImage} />
        )}
      </View>
      <View style={styles.inputContainer}>
        <InputField
          {...editProfile.getTextInputProps('nickName')}
          error={editProfile.errors.nickName}
          touched={editProfile.touched.nickName}
          placeholder="닉네임을 입력해주세요."
        />
      </View>
      <CustomButton label="저장" onPress={handleSubmit} variant="filled" />

      <CompoundOption isVisible={isVisible} hideOption={hide}>
        <CompoundOption.Background>
          <CompoundOption.Container>
            <View style={styles.modalContainer}>
              <Text style={styles.modalText}>닉네임이 변경되었습니다.</Text>
              <CompoundOption.Button onPress={handleConfirm}>
                확인
              </CompoundOption.Button>
            </View>
          </CompoundOption.Container>
        </CompoundOption.Background>
      </CompoundOption>

      <CompoundOption isVisible={errorModalVisible} hideOption={() => setErrorModalVisible(false)}>
        <CompoundOption.Background>
          <CompoundOption.Container>
            <View style={styles.modalContainer}>
              <Text style={styles.modalText}>닉네임 수정 중 오류가 발생했습니다.</Text>
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
    imageContainer: {
      alignItems: 'center',
      marginBottom: 30,
    },
    profileImage: {
      width: 100,
      height: 100,
      borderRadius: 50,
    },
    inputContainer: {
      marginBottom: 30,
    },
    modalContainer: {
      padding: 20,
      alignItems: 'center',
    },
    modalText: {
      fontSize: 16,
      marginBottom: 20,
      color: colors[theme].BLACK,
      textAlign: 'center',
    },
  });

export default EditProfileScreen; 
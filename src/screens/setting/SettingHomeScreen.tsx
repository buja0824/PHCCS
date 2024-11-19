import React from 'react';
import {ScrollView, SafeAreaView, StyleSheet, View, Image, Pressable, Text} from 'react-native';
import Octicons from 'react-native-vector-icons/Octicons';
import SettingItem from '@/components/setting/SettingItem';
import {colors, settingNavigations} from '@/constants';
import {StackScreenProps} from '@react-navigation/stack';
import {SettingStackParamList} from '@/navigations/stack/SettingStackNavigator';
import useAuth from '@/hooks/queries/useAuth';
import useThemeStore, { ThemeMode } from '@/store/useThemeStore';
import DarkModeOption from '@/components/setting/DarkModeOption';
import useModal from '@/hooks/useModal';

type SettingHomeScreenProps = StackScreenProps<SettingStackParamList>;

function SettingHomeScreen({navigation}: SettingHomeScreenProps) {
  const {theme} = useThemeStore();
  const styles = styling(theme);
  const {logoutMutation, getProfileQuery} = useAuth();
  const darkModeOption = useModal();
  const { nickName, imageUri } = getProfileQuery.data || {};

  const handlePressEditProfile = () => {
    navigation.navigate(settingNavigations.EDIT_PROFILE);
  };

  const handlePressEditPassword = () => {
    navigation.navigate(settingNavigations.EDIT_PASSWORD);
  };

  const handlePressLogout = () => {
    logoutMutation.mutate(null);
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView>
        <View style={styles.profileContainer}>
          <Pressable style={styles.imageContainer} onPress={handlePressEditProfile}>
            {!imageUri ? (
              <Image
                source={require('@/assets/user-default.png')}
                style={styles.profileImage}
              />
            ) : (
              <Image
                source={{uri: imageUri}}
                style={styles.profileImage}
              />
            )}
          </Pressable>
          <Text style={styles.nickNameText}>{nickName}</Text>
        </View>

        <View style={styles.menuContainer}>
          <View style={styles.menuItem}>
            <SettingItem title="닉네임 수정" onPress={handlePressEditProfile} />
          </View>
          <View style={styles.menuItem}>
            <SettingItem title="비밀번호 변경" onPress={handlePressEditPassword} />
          </View>
          <View style={styles.menuItem}>
            <SettingItem title="다크 모드" onPress={darkModeOption.show} />
          </View>
        </View>

        <View style={styles.logoutContainer}>
          <View style={styles.menuItem}>
            <SettingItem
              title="로그아웃"
              onPress={handlePressLogout}
              color={colors[theme].RED_500}
              icon={
                <Octicons
                  name={'sign-out'}
                  color={colors[theme].RED_500}
                  size={16}
                />
              }
            />
          </View>
        </View>

        <View style={[styles.logoutContainer, {marginTop: 20}]}>
          <View style={styles.menuItem}>
            <SettingItem
              title="회원탈퇴"
              onPress={() => navigation.navigate(settingNavigations.DELETE_ACCOUNT)}
              color={colors[theme].RED_500}
              icon={
                <Octicons
                  name="trash"
                  color={colors[theme].RED_500}
                  size={16}
                />
              }
            />
          </View>
        </View>

        <DarkModeOption
          isVisible={darkModeOption.isVisible}
          hideOption={darkModeOption.hide}
        />
      </ScrollView>
    </SafeAreaView>
  );
}

const styling = (theme: ThemeMode) =>
  StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: colors[theme].GRAY_100,
    },
    profileContainer: {
      alignItems: 'center',
      padding: 20,
      backgroundColor: colors[theme].WHITE,
    },
    imageContainer: {
      width: 100,
      height: 100,
      borderRadius: 50,
      overflow: 'hidden',
      marginBottom: 15,
    },
    profileImage: {
      width: '100%',
      height: '100%',
    },
    nickNameText: {
      fontSize: 18,
      fontWeight: '600',
      color: colors[theme].BLACK,
    },
    menuContainer: {
      backgroundColor: colors[theme].WHITE,
    },
    menuItem: {
      borderTopWidth: 1,
      borderBottomWidth: 1,
      borderColor: colors[theme].GRAY_200,
    },
    logoutContainer: {
      marginTop: 20,
      backgroundColor: colors[theme].WHITE,
    }
  });

export default SettingHomeScreen; 
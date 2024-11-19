import CustomButton from '@/components/CustomButton';
import {alerts, colors, errorMessages} from '@/constants';
import useAuth from '@/hooks/queries/useAuth';
import useThemeStore from '@/store/useThemeStore';
import {ThemeMode} from '@/types/common';
import React from 'react';
import {Alert, Text} from 'react-native';
import {StyleSheet, View} from 'react-native';
import Toast from 'react-native-toast-message';
import { CompoundOption } from '@/components/common/CompoundOption';
import useModal from '@/hooks/useModal';

function DeleteAccountScreen() {
  const {theme} = useThemeStore();
  const styles = styling(theme);
  const {deleteAccountMutation} = useAuth();
  const {isVisible: isConfirmVisible, show: showConfirm, hide: hideConfirm} = useModal();
  const {isVisible: isErrorVisible, show: showError, hide: hideError} = useModal();

  const handlePressDeleteAccount = () => {
    showConfirm();
  };

  const handleConfirmDelete = async () => {
    try {
      await deleteAccountMutation.mutateAsync(undefined);
      Toast.show({
        type: 'success',
        text1: '탈퇴가 완료되었습니다.',
        position: 'bottom',
      });
      hideConfirm();
    } catch (error: any) {
      hideConfirm();
      showError();
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.messageContainer}>
        <Text style={styles.infoText}>
          • 작성한 데이터를 모두 삭제해야 회원 탈퇴가 가능해요.
        </Text>
        <Text style={styles.infoText}>
          • 작성한 게시글과 댓글이 남아있다면 삭제해 주세요.
        </Text>
        <View style={styles.warningContainer}>
          <Text style={styles.warningText}>
            • 탈퇴 후에는 더 이상 PetCare 서비스를 이용하지 못해요.
          </Text>
          <Text style={styles.warningText}>
            • 그래도 탈퇴 하시겠습니까?
          </Text>
        </View>
      </View>

      <CustomButton 
        label="회원 탈퇴" 
        onPress={handlePressDeleteAccount}
        style={{backgroundColor: '#FF3333'}}
      />

      <CompoundOption isVisible={isConfirmVisible} hideOption={hideConfirm}>
        <CompoundOption.Background>
          <CompoundOption.Container>
            <CompoundOption.Button onPress={handleConfirmDelete} isDanger>
              탈퇴
            </CompoundOption.Button>
            <CompoundOption.Divider />
            <CompoundOption.Button onPress={hideConfirm}>
              취소
            </CompoundOption.Button>
          </CompoundOption.Container>
        </CompoundOption.Background>
      </CompoundOption>

      <ErrorModal 
        isVisible={isErrorVisible}
        hideOption={hideError}
        styles={styles}
      />
    </View>
  );
}

const styling = (theme: ThemeMode) =>
  StyleSheet.create({
    container: {
      flex: 1,
      padding: 20,
      marginBottom: 20,
    },
    messageContainer: {
      alignItems: 'flex-start',
      marginTop: 10,
      marginBottom: 30,
      borderWidth: 1.5,
      borderColor: '#FF3333',
      borderRadius: 3,
      padding: 15,
    },
    infoText: {
      color: colors[theme].PINK_700,
      fontSize: 15,
      fontWeight: '600',
      marginBottom: 10,
    },
    warningText: {
      color: '#FF3333',
      fontSize: 15,
      fontWeight: '700',
      marginBottom: 10,
    },
    warningContainer: {
      marginTop: 20,
    },
    errorModalContainer: {
      padding: 20,
      alignItems: 'center',
    },
    errorModalTitle: {
      fontSize: 18,
      fontWeight: 'bold',
      color: colors[theme].BLACK,
      marginBottom: 10,
    },
    errorModalMessage: {
      fontSize: 15,
      color: colors[theme].BLACK,
      textAlign: 'center',
    },
  });

function ErrorModal({isVisible, hideOption, styles}: {
  isVisible: boolean;
  hideOption: () => void;
  styles: ReturnType<typeof styling>;
}) {
  return (
    <CompoundOption isVisible={isVisible} hideOption={hideOption}>
      <CompoundOption.Background>
        <CompoundOption.Container>
          <View style={styles.errorModalContainer}>
            <Text style={styles.errorModalTitle}>탈퇴 불가</Text>
            <Text style={styles.errorModalMessage}>
              작성한 게시글 또는 댓글을 먼저 삭제해 주세요.
            </Text>
          </View>
          <CompoundOption.Button onPress={hideOption}>
            확인
          </CompoundOption.Button>
        </CompoundOption.Container>
      </CompoundOption.Background>
    </CompoundOption>
  );
}

export default DeleteAccountScreen; 
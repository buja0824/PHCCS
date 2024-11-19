import React, {useState} from 'react';
import {View, StyleSheet, Alert, Pressable, Text, SafeAreaView, ScrollView} from 'react-native';
import {useMutation, useQueryClient} from '@tanstack/react-query';
import {addPet} from '@/api/pet';
import InputField from '@/components/InputField';
import CustomButton from '@/components/CustomButton';
import useForm from '@/hooks/useForm';
import {colors} from '@/constants';
import useThemeStore, { ThemeMode } from '@/store/useThemeStore';
import {petNavigations} from '@/constants';
import {StackScreenProps} from '@react-navigation/stack';
import {PetStackParamList} from '@/navigations/stack/PetStackNavigator';
import DateSelector from '@/components/pet/DateSelector';
import useModal from '@/hooks/useModal';
import {calculateAge } from '@/utils/pet';
import GenderSelector from '@/components/pet/GenderSelector';
import AdoptionDateSelector from '@/components/pet/AdoptionDateSelector';
import { saveAdoptionDate } from '@/types/pet';


type PetAddScreenProps = StackScreenProps<
  PetStackParamList,
  typeof petNavigations.PET_ADD
>;

function PetAddScreen({navigation}: PetAddScreenProps) {
  const {theme} = useThemeStore();
  const queryClient = useQueryClient();
  const [birthDate, setBirthDate] = useState(new Date());
  const [adoptionDate, setAdoptionDate] = useState(new Date());
  const dateSelector = useModal();
  const maxDate = new Date();

  const petForm = useForm({
    initialValue: {
      petRegNo: '',
      petName: '',
      petBreed: '',
      petGender: '',
    },
    validate: values => {
      const errors: Partial<typeof values> = {};
      if (!values.petRegNo) {
        errors.petRegNo = '반려동물 등록번호를 입력해주세요.';
      }
      if (!values.petName) {
        errors.petName = '반려동물 이름을 입력해주세요.';
      }
      if (!values.petBreed) {
        errors.petBreed = '반려동물 종을 입력해주세요.';
      }
      if (!values.petGender) {
        errors.petGender = '반려동물 성별을 입력해주세요.';
      }
      return errors;
    },
  });

  const addPetMutation = useMutation({
    mutationFn: addPet,
    onSuccess: () => {
      queryClient.invalidateQueries({queryKey: ['pets']});
      Alert.alert('성공', '반려동물이 등록되었습니다.', [
        {
          text: '확인',
          onPress: () => navigation.navigate(petNavigations.PET_HOME),
        },
      ]);
    },
    onError: () => {
      Alert.alert('오류', '반려동물 등록에 실패했니다.');
    },
  });

  const handleSubmit = () => {
    const {petRegNo, petName, petBreed, petGender} = petForm.values;
    if (!petRegNo || !petName || !petBreed || !petGender) return;

    const age = calculateAge(birthDate);

    saveAdoptionDate(petName, adoptionDate);

    addPetMutation.mutate({
      petRegNo,
      petName,
      petBreed,
      petAge: age,
      petGender
    });
  };

  const handleBirthDateChange = (date: Date) => {
    setBirthDate(date);
    // 입양일이 출생일보다 이전이면 입양일을 출생일로 설정
    if (adoptionDate < date) {
      setAdoptionDate(date);
    }
  };

  const styles = styling(theme);

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView 
        style={styles.scrollView}
        keyboardShouldPersistTaps="handled"
      >
        <View style={styles.content}>
          <View style={styles.inputField}>
            <InputField
              {...petForm.getTextInputProps('petName')}
              error={petForm.errors.petName}
              touched={petForm.touched.petName}
              placeholder="이름"
            />
          </View>
          <GenderSelector
            value={petForm.values.petGender}
            onChange={value => petForm.setValue('petGender', value)}
            error={petForm.errors.petGender}
            touched={petForm.touched.petGender}
          />
          <InputField
            {...petForm.getTextInputProps('petBreed')}
            error={petForm.errors.petBreed}
            touched={petForm.touched.petBreed}
            placeholder="종"
          />
          <View style={styles.birthSelector}>
            <Text style={styles.birthLabel}>출생일</Text>
            <Pressable style={styles.birthContent} onPress={dateSelector.show}>
              <Text style={styles.birthText}>
                {birthDate.getFullYear()}년 {birthDate.getMonth() + 1}월 {birthDate.getDate()}일
              </Text>
              <Text style={styles.ageText}>(만 {calculateAge(birthDate)}세)</Text>
            </Pressable>
          </View>
          <AdoptionDateSelector
            date={adoptionDate}
            birthDate={birthDate}
            onChangeDate={setAdoptionDate}
          />
          <View style={styles.registerNumber}>
            <InputField
              {...petForm.getTextInputProps('petRegNo')}
              error={petForm.errors.petRegNo}
              touched={petForm.touched.petRegNo}
              placeholder="반려동물 등록번호"
              keyboardType="numeric"
            />
          </View>
          <DateSelector
            isVisible={dateSelector.isVisible}
            currentDate={birthDate}
            onChangeDate={handleBirthDateChange}
            hide={dateSelector.hide}
            maximumDate={maxDate}
            title="출생일 선택"
          />
          <CustomButton label="등록하기" onPress={handleSubmit} variant="filled" />
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styling = (theme: ThemeMode) =>
  StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: colors.light.WHITE,
    },
    scrollView: {
      flex: 1,
    },
    content: {
      padding: 30,
    },
    birthSelector: {
      padding: 15,
      borderWidth: 1,
      borderColor: colors[theme].GRAY_200,
      borderRadius: 8,
      marginBottom: 16,
      marginTop: 16,
    },
    birthLabel: {
      fontSize: 12,
      color: colors[theme].GRAY_500,
      marginBottom: 4,
    },
    birthContent: {
      flexDirection: 'row',
      alignItems: 'center',
    },
    birthText: {
      color: colors[theme].BLACK,
      flex: 1,
    },
    ageText: {
      color: colors[theme].GRAY_500,
    },
    registerNumber: {
      marginTop: 16,
      marginBottom: 32,
    },
    inputField: {
      marginBottom: 16,
    }
  });

export default PetAddScreen;

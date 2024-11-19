import React, {useState, useEffect} from 'react';
import {View, StyleSheet, Alert, SafeAreaView, ScrollView} from 'react-native';
import {StackScreenProps} from '@react-navigation/stack';
import {PetStackParamList} from '@/navigations/stack/PetStackNavigator';
import {petNavigations, colors} from '@/constants';
import InputField from '@/components/InputField';
import CustomButton from '@/components/CustomButton';
import useForm from '@/hooks/useForm';
import {useMutation, useQueryClient} from '@tanstack/react-query';
import {modifyPet} from '@/api/pet';
import useThemeStore from '@/store/useThemeStore';
import {ThemeMode} from '@/types';
import DateSelector from '@/components/pet/DateSelector';
import useModal from '@/hooks/useModal';
import {calculateAge} from '@/utils/pet';
import {Pressable, Text} from 'react-native';
import GenderSelector from '@/components/pet/GenderSelector';
import { getAdoptionDate, Pet, saveAdoptionDate } from '@/types/pet';
import AdoptionDateSelector from '@/components/pet/AdoptionDateSelector';


type Props = StackScreenProps<PetStackParamList, typeof petNavigations.PET_EDIT>;

function PetEditScreen({route, navigation}: Props) {
  const {theme} = useThemeStore();
  const queryClient = useQueryClient();
  const {petName} = route.params;
  const [birthDate, setBirthDate] = useState(new Date());
  const [adoptionDate, setAdoptionDate] = useState(new Date());
  const dateSelector = useModal();
  const adoptionDateSelector = useModal();
  const maxDate = new Date();

  useEffect(() => {
    const loadAdoptionDate = async () => {
      const savedDate = await getAdoptionDate(petName);
      if (savedDate) {
        setAdoptionDate(savedDate);
      }
    };
    loadAdoptionDate();
  }, [petName]);

  const petForm = useForm({
    initialValue: {
      petRegNo: '',
      petName: '',
      petBreed: '',
      petGender: '',
    },
    validate: values => {
      const errors: Partial<typeof values> = {};
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

  const modifyPetMutation = useMutation({
    mutationFn: (updateData: Omit<Pet, 'petRegNo'>) => modifyPet(petName, updateData),
    onSuccess: () => {
      queryClient.invalidateQueries({queryKey: ['pets']});
      Alert.alert('성공', '반려동물 정보가 수정되었습니다.', [
        {
          text: '확인',
          onPress: () => navigation.navigate(petNavigations.PET_HOME),
        },
      ]);
    },
    onError: () => {
      Alert.alert('오류', '반려동물 정보 수정에 실패했습니다.');
    },
  });

  const handleSubmit = () => {
    const {petName: newPetName, petBreed, petGender} = petForm.values;
    if (!newPetName || !petBreed || !petGender) return;

    const age = calculateAge(birthDate);

    saveAdoptionDate(newPetName, adoptionDate);

    modifyPetMutation.mutate({
      petName: newPetName,
      petBreed,
      petAge: age,
      petGender,
    });
  };

  const handleBirthDateChange = (date: Date) => {
    setBirthDate(date);
    if (adoptionDate < date) {
      setAdoptionDate(date);
    }
  };

  const styles = styling(theme);

  return (
    <SafeAreaView style={[styles.container, {backgroundColor: colors[theme].WHITE}]}>
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
          <View style={styles.inputField}>
            <InputField
              {...petForm.getTextInputProps('petBreed')}
              error={petForm.errors.petBreed}
              touched={petForm.touched.petBreed}
              placeholder="종"
            />
          </View>
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
              keyboardType="default"
              editable={false}
              style={{backgroundColor: '#f0f0f0'}}
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
          <CustomButton label="수정하기" onPress={handleSubmit} variant="filled" />
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styling = (theme: ThemeMode) =>
  StyleSheet.create({
    container: {
      flex: 1,
    },
    scrollView: {
      flex: 1,
    },
    content: {
      padding: 20,
    },
    birthSelector: {
      padding: 15,
      borderWidth: 1,
      borderColor: colors[theme].GRAY_200,
      borderRadius: 8,
      marginBottom: 4,
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
    },
    adoptionSelector: {
      padding: 15,
      borderWidth: 1,
      borderColor: colors[theme].GRAY_200,
      borderRadius: 8,
      marginBottom: 16,
    },
  });

export default PetEditScreen;

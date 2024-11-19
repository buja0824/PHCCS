import React, { useState } from 'react';
import { View, Text, FlatList, TouchableOpacity, Alert, StyleSheet } from 'react-native';
import useThemeStore, { ThemeMode } from '@/store/useThemeStore';
import { Vaccination } from '@/types/petHealth';
import { getVaccinations } from '@/utils/PetHealthStorage';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { colors } from '@/constants';
import { useFocusEffect } from '@react-navigation/native';
import useModal from '@/hooks/useModal';
import { CompoundOption } from '@/components/common/CompoundOption';
import { getEncryptStorage, setEncryptStorage } from '@/utils';

type Props = {
  navigation: any;
  route: any;
};

function VaccinationListScreen({ navigation, route }: Props) {
  const {theme} = useThemeStore();
  const {petName} = route.params;
  const [vaccinations, setVaccinations] = useState<Vaccination[]>([]);
  const [selectedVaccination, setSelectedVaccination] = useState<Vaccination | null>(null);
  const optionModal = useModal();
  const deleteConfirmModal = useModal();

  useFocusEffect(
    React.useCallback(() => {
      loadVaccinations();
    }, [])
  );

  const loadVaccinations = async () => {
    const data = await getVaccinations(petName);
    setVaccinations(data.sort((a: Vaccination, b: Vaccination) => 
      new Date(b.date).getTime() - new Date(a.date).getTime()
    ));
  };

  const handleAddVaccination = () => {
    navigation.navigate('Vaccination', {petName});
  };

  const handleDelete = async () => {
    if (!selectedVaccination) return;
    try {
      const data = await getVaccinations(petName);
      const newVaccinations = data.filter(
        (vaccination: Vaccination) => vaccination.id !== selectedVaccination.id
      );
      await setEncryptStorage(`vaccinations_${petName}`, newVaccinations);
      
      if (selectedVaccination.nextVaccinationDate) {
        const schedules = await getEncryptStorage('schedules') || {};
        const dateKey = selectedVaccination.nextVaccinationDate.split('T')[0];
        if (schedules[dateKey]) {
          schedules[dateKey] = schedules[dateKey].filter(
            (schedule: any) => schedule.type !== 'vaccination' || 
              schedule.content !== `${petName} 예방접종`
          );
          if (schedules[dateKey].length === 0) {
            delete schedules[dateKey];
          }
          await setEncryptStorage('schedules', schedules);
        }
      }
      
      setVaccinations(newVaccinations);
      Alert.alert('알림', '예방접종 기록이 삭제되었습니다.');
    } catch (error) {
      Alert.alert('오류', '삭제에 실패했습니다.');
    }
  };

  const handleEdit = () => {
    if (!selectedVaccination) return;
    navigation.navigate('Vaccination', {
      petName,
      vaccination: selectedVaccination,
      mode: 'edit'
    });
  };

  const styles = styling(theme);

  const renderHeader = () => (
    <View style={styles.header}>
      <View style={styles.headerContent}>
        <Text style={styles.title}>예방접종 기록</Text>
        <Text style={styles.subtitle}>
          {petName}의 예방접종 기록을{'\n'}
          체계적으로 관리하세요
        </Text>
      </View>
      <TouchableOpacity 
        style={styles.addButton}
        onPress={handleAddVaccination}
      >
        <Ionicons name="add-circle" size={24} color={colors[theme].WHITE} />
      </TouchableOpacity>
    </View>
  );

  const renderVaccinationCard = ({item}: {item: Vaccination}) => (
    <TouchableOpacity 
      style={styles.vaccinationCard}
      onPress={() => {
        setSelectedVaccination(item);
        optionModal.show();
      }}
    >
      <View style={styles.cardHeader}>
        <View style={styles.dateContainer}>
          <Text style={styles.dateYear}>
            {new Date(item.date).getFullYear()}
          </Text>
          <Text style={styles.dateMonthDay}>
            {new Date(item.date).getMonth() + 1}월 {new Date(item.date).getDate()}일
          </Text>
        </View>
        <View style={styles.typeContainer}>
          <Text style={styles.typeLabel}>접종 종류</Text>
          <Text style={styles.typeValue}>{item.type}</Text>
        </View>
      </View>
      <View style={styles.cardContent}>
        <Text style={styles.description}>{item.description}</Text>
        {item.nextVaccinationDate && (
          <View style={styles.nextDateContainer}>
            <Ionicons name="calendar" size={16} color={colors[theme].GREEN_600} />
            <Text style={styles.nextDate}>
              다음 접종일: {new Date(item.nextVaccinationDate).getFullYear()}년 {' '}
              {new Date(item.nextVaccinationDate).getMonth() + 1}월 {' '}
              {new Date(item.nextVaccinationDate).getDate()}일
            </Text>
          </View>
        )}
      </View>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      {renderHeader()}
      <FlatList
        data={vaccinations}
        renderItem={renderVaccinationCard}
        keyExtractor={item => item.id.toString()}
        contentContainerStyle={styles.listContainer}
        showsVerticalScrollIndicator={false}
      />

      <CompoundOption isVisible={optionModal.isVisible} hideOption={optionModal.hide}>
        <CompoundOption.Background>
          <CompoundOption.Container>
            <CompoundOption.Button onPress={() => {
              handleEdit();
              optionModal.hide();
            }}>
              수정
            </CompoundOption.Button>
            <CompoundOption.Divider />
            <CompoundOption.Button onPress={() => {
              deleteConfirmModal.show();
              optionModal.hide();
            }} isDanger>
              삭제
            </CompoundOption.Button>
          </CompoundOption.Container>
          <CompoundOption.Container>
            <CompoundOption.Button onPress={optionModal.hide}>
              취소
            </CompoundOption.Button>
          </CompoundOption.Container>
        </CompoundOption.Background>
      </CompoundOption>

      <CompoundOption isVisible={deleteConfirmModal.isVisible} hideOption={deleteConfirmModal.hide}>
        <CompoundOption.Background>
          <CompoundOption.Container>
            <View style={styles.deleteConfirmContainer}>
              <Text style={styles.deleteConfirmTitle}>예방접종 기록 삭제</Text>
              <Text style={styles.deleteConfirmMessage}>
                이 예방접종 기록을 삭제하시겠습니까?
              </Text>
            </View>
            <CompoundOption.Button onPress={() => {
              handleDelete();
              deleteConfirmModal.hide();
            }} isDanger>
              삭제
            </CompoundOption.Button>
            <CompoundOption.Divider />
            <CompoundOption.Button onPress={deleteConfirmModal.hide}>
              취소
            </CompoundOption.Button>
          </CompoundOption.Container>
        </CompoundOption.Background>
      </CompoundOption>
    </View>
  );
}

const styling = (theme: ThemeMode) => StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors[theme].WHITE,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 24,
    backgroundColor: colors[theme].GREEN_500,
  },
  headerContent: {
    flex: 1,
  },
  title: {
    fontSize: 24,
    fontWeight: '600',
    color: colors[theme].WHITE,
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 14,
    color: colors[theme].WHITE,
    opacity: 0.8,
    lineHeight: 20,
  },
  addButton: {
    width: 48,
    height: 48,
    borderRadius: 24,
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    justifyContent: 'center',
    alignItems: 'center',
    marginLeft: 16,
  },
  listContainer: {
    padding: 16,
  },
  vaccinationCard: {
    backgroundColor: colors[theme].WHITE,
    borderRadius: 16,
    marginBottom: 16,
    padding: 16,
    shadowColor: colors[theme].BLACK,
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 2,
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
  },
  dateContainer: {
    alignItems: 'center',
    backgroundColor: colors[theme].GREEN_50,
    padding: 12,
    borderRadius: 12,
    minWidth: 80,
  },
  dateYear: {
    fontSize: 14,
    color: colors[theme].GREEN_600,
    marginBottom: 4,
  },
  dateMonthDay: {
    fontSize: 16,
    fontWeight: '600',
    color: colors[theme].GREEN_600,
  },
  typeContainer: {
    alignItems: 'flex-end',
  },
  typeLabel: {
    fontSize: 12,
    color: colors[theme].GRAY_500,
    marginBottom: 4,
  },
  typeValue: {
    fontSize: 18,
    fontWeight: '600',
    color: colors[theme].BLACK,
  },
  cardContent: {
    borderTopWidth: 1,
    borderTopColor: colors[theme].GRAY_100,
    paddingTop: 16,
  },
  description: {
    fontSize: 14,
    color: colors[theme].GRAY_700,
    lineHeight: 20,
    marginBottom: 12,
  },
  nextDateContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  nextDate: {
    fontSize: 14,
    color: colors[theme].GREEN_600,
  },
  deleteConfirmContainer: {
    padding: 16,
  },
  deleteConfirmTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: colors[theme].BLACK,
    marginBottom: 8,
  },
  deleteConfirmMessage: {
    fontSize: 14,
    color: colors[theme].GRAY_600,
    marginBottom: 16,
  },
});

export default VaccinationListScreen;

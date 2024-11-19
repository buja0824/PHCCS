import React, { useEffect, useState } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet, Alert } from 'react-native';
import useThemeStore, { ThemeMode } from '@/store/useThemeStore';
import { HealthCheckup } from '@/types/petHealth';
import { getHealthCheckups } from '@/utils/PetHealthStorage';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { colors } from '@/constants';
import { getEncryptStorage, setEncryptStorage } from '@/utils/encryptStorage';
import useModal from '@/hooks/useModal';
import { CompoundOption } from '@/components/common/CompoundOption';
import { useFocusEffect } from '@react-navigation/native';


type Props = {
  navigation: any;
  route: any;
};

function HealthCheckupListScreen({ navigation, route }: Props) {
  const {theme} = useThemeStore();
  const {petName} = route.params;
  const [checkups, setCheckups] = useState<HealthCheckup[]>([]);
  const [selectedCheckup, setSelectedCheckup] = useState<HealthCheckup | null>(null);
  const optionModal = useModal();
  const deleteConfirmModal = useModal();

  useFocusEffect(
    React.useCallback(() => {
      loadHealthCheckups();
    }, [])
  );

  const loadHealthCheckups = async () => {
    const data = await getHealthCheckups(petName);
    setCheckups(data.sort((a: HealthCheckup, b: HealthCheckup) => 
      new Date(b.date).getTime() - new Date(a.date).getTime()
    ));
  };

  const handleAddCheckup = () => {
    navigation.navigate('HealthCheckup', {petName});
  };

  const handleDelete = async () => {
    if (!selectedCheckup) return;
    try {
      const data = await getHealthCheckups(petName);
      const newCheckups = data.filter(
        (checkup: HealthCheckup) => checkup.id !== selectedCheckup.id
      );
      await setEncryptStorage(`health_checkups_${petName}`, newCheckups);
      
      if (selectedCheckup.nextCheckupDate) {
        const schedules = await getEncryptStorage('schedules') || {};
        const dateKey = selectedCheckup.nextCheckupDate.split('T')[0];
        if (schedules[dateKey]) {
          schedules[dateKey] = schedules[dateKey].filter(
            (schedule: any) => schedule.type !== 'health_checkup' || 
              schedule.content !== `${petName} 건강검진`
          );
          if (schedules[dateKey].length === 0) {
            delete schedules[dateKey];
          }
          await setEncryptStorage('schedules', schedules);
        }
      }
      
      setCheckups(newCheckups);
      Alert.alert('알림', '건강검진 기록이 삭제되었습니다.');
    } catch (error) {
      Alert.alert('오류', '삭제에 실패했습니다.');
    }
  };

  const handleEdit = () => {
    if (!selectedCheckup) return;
    navigation.navigate('HealthCheckup', {
      petName,
      checkup: selectedCheckup,
      mode: 'edit'
    });
  };

  const styles = styling(theme);

  const renderHeader = () => (
    <View style={styles.header}>
      <View style={styles.headerContent}>
        <Text style={styles.title}>건강검진 기록</Text>
        <Text style={styles.subtitle}>
          {petName}의 건강검진 기록을{'\n'}
          체계적으로 관리하세요
        </Text>
      </View>
      <TouchableOpacity 
        style={styles.addButton}
        onPress={handleAddCheckup}
      >
        <Ionicons name="add-circle" size={24} color={colors[theme].WHITE} />
      </TouchableOpacity>
    </View>
  );

  const renderCheckupCard = ({item}: {item: HealthCheckup}) => (
    <TouchableOpacity 
      style={styles.checkupCard}
      onPress={() => {
        setSelectedCheckup(item);
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
        <View style={styles.weightContainer}>
          <Text style={styles.weightLabel}>체중</Text>
          <Text style={styles.weightValue}>{item.weight}kg</Text>
        </View>
      </View>
      <View style={styles.cardContent}>
        <Text style={styles.description}>{item.description}</Text>
        {item.nextCheckupDate && (
          <View style={styles.nextDateContainer}>
            <Ionicons name="calendar" size={16} color={colors[theme].BLUE_500} />
            <Text style={styles.nextDate}>
              다음 검진일: {new Date(item.nextCheckupDate).getFullYear()}년 {' '}
              {new Date(item.nextCheckupDate).getMonth() + 1}월 {' '}
              {new Date(item.nextCheckupDate).getDate()}일
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
        data={checkups}
        renderItem={renderCheckupCard}
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
              <Text style={styles.deleteConfirmTitle}>건강진 기록 삭제</Text>
              <Text style={styles.deleteConfirmMessage}>
                정말 이 기록을 삭제하시겠습니까?
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
    backgroundColor: colors[theme].BLUE_500,
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
  checkupCard: {
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
    backgroundColor: colors[theme].BLUE_50,
    padding: 12,
    borderRadius: 12,
    minWidth: 80,
  },
  dateYear: {
    fontSize: 14,
    color: colors[theme].BLUE_600,
    marginBottom: 4,
  },
  dateMonthDay: {
    fontSize: 16,
    fontWeight: '600',
    color: colors[theme].BLUE_600,
  },
  weightContainer: {
    alignItems: 'flex-end',
  },
  weightLabel: {
    fontSize: 12,
    color: colors[theme].GRAY_500,
    marginBottom: 4,
  },
  weightValue: {
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
    color: colors[theme].BLUE_500,
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
    color: colors[theme].GRAY_700,
  },
});

export default HealthCheckupListScreen;

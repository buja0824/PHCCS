import React, { useState } from 'react';
import { View, Text, FlatList, TouchableOpacity, Alert, StyleSheet } from 'react-native';
import useThemeStore, { ThemeMode } from '@/store/useThemeStore';
import { MedicalHistory } from '@/types/petHealth';
import { getMedicalHistories } from '@/utils/PetHealthStorage';
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

function MedicalHistoryListScreen({ navigation, route }: Props) {
  const {theme} = useThemeStore();
  const {petName} = route.params;
  const [histories, setHistories] = useState<MedicalHistory[]>([]);
  const [selectedHistory, setSelectedHistory] = useState<MedicalHistory | null>(null);
  const optionModal = useModal();
  const deleteConfirmModal = useModal();

  useFocusEffect(
    React.useCallback(() => {
      loadHistories();
    }, [])
  );

  const loadHistories = async () => {
    const data = await getMedicalHistories(petName);
    setHistories(data.sort((a: MedicalHistory, b: MedicalHistory) => 
      new Date(b.date).getTime() - new Date(a.date).getTime()
    ));
  };

  const handleAddHistory = () => {
    navigation.navigate('MedicalHistory', {petName});
  };

  const handleDelete = async () => {
    if (!selectedHistory) return;
    try {
      const data = await getMedicalHistories(petName);
      const newHistories = data.filter(
        (history: MedicalHistory) => history.id !== selectedHistory.id
      );
      await setEncryptStorage(`medical_history_${petName}`, newHistories);
      
      if (selectedHistory.nextVisitDate) {
        const schedules = await getEncryptStorage('schedules') || {};
        const dateKey = selectedHistory.nextVisitDate.split('T')[0];
        if (schedules[dateKey]) {
          schedules[dateKey] = schedules[dateKey].filter(
            (schedule: any) => schedule.type !== 'medical_history' || 
              schedule.content !== `${petName} 병원 방문`
          );
          if (schedules[dateKey].length === 0) {
            delete schedules[dateKey];
          }
          await setEncryptStorage('schedules', schedules);
        }
      }
      
      setHistories(newHistories);
      Alert.alert('알림', '질병 기록이 삭제되었습니다.');
    } catch (error) {
      Alert.alert('오류', '삭제에 실패했습니다.');
    }
  };

  const handleEdit = () => {
    if (!selectedHistory) return;
    navigation.navigate('MedicalHistory', {
      petName,
      history: selectedHistory,
      mode: 'edit'
    });
  };

  const styles = styling(theme);

  const renderHeader = () => (
    <View style={styles.header}>
      <View style={styles.headerContent}>
        <Text style={styles.title}>질병 기록</Text>
        <Text style={styles.subtitle}>
          {petName}의 질병 기록을{'\n'}
          체계적으로 관리하세요
        </Text>
      </View>
      <TouchableOpacity 
        style={styles.addButton}
        onPress={handleAddHistory}
      >
        <Ionicons name="add-circle" size={24} color={colors[theme].WHITE} />
      </TouchableOpacity>
    </View>
  );

  const renderHistoryCard = ({item}: {item: MedicalHistory}) => (
    <TouchableOpacity 
      style={styles.historyCard}
      onPress={() => {
        setSelectedHistory(item);
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
        <View style={styles.diseaseContainer}>
          <Text style={styles.diseaseLabel}>질병명</Text>
          <Text style={styles.diseaseValue}>{item.condition}</Text>
        </View>
      </View>
      <View style={styles.cardContent}>
        <Text style={styles.description}>{item.treatment}</Text>
        {item.nextVisitDate && (
          <View style={styles.nextDateContainer}>
            <Ionicons name="calendar" size={16} color={colors[theme].RED_600} />
            <Text style={styles.nextDate}>
              다음 내원일: {new Date(item.nextVisitDate).getFullYear()}년 {' '}
              {new Date(item.nextVisitDate).getMonth() + 1}월 {' '}
              {new Date(item.nextVisitDate).getDate()}일
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
        data={histories}
        renderItem={renderHistoryCard}
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
              <Text style={styles.deleteConfirmTitle}>질병 기록 삭제</Text>
              <Text style={styles.deleteConfirmMessage}>
                이 질병 기록을 삭제하시겠습니까?
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
    backgroundColor: colors[theme].RED_500,
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
  historyCard: {
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
    backgroundColor: colors[theme].RED_50,
    padding: 12,
    borderRadius: 12,
    minWidth: 80,
  },
  dateYear: {
    fontSize: 14,
    color: colors[theme].RED_600,
    marginBottom: 4,
  },
  dateMonthDay: {
    fontSize: 16,
    fontWeight: '600',
    color: colors[theme].RED_600,
  },
  diseaseContainer: {
    alignItems: 'flex-end',
  },
  diseaseLabel: {
    fontSize: 12,
    color: colors[theme].GRAY_500,
    marginBottom: 4,
  },
  diseaseValue: {
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
  treatmentContainer: {
    marginBottom: 12,
  },
  treatmentLabel: {
    fontSize: 12,
    color: colors[theme].GRAY_500,
    marginBottom: 4,
  },
  treatmentValue: {
    fontSize: 14,
    color: colors[theme].GRAY_700,
    lineHeight: 20,
  },
  nextDateContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  nextDate: {
    fontSize: 14,
    color: colors[theme].RED_600,
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

export default MedicalHistoryListScreen;

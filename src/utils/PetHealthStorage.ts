import { getEncryptStorage, setEncryptStorage } from './encryptStorage';
import { HealthCheckup, Vaccination, MedicalHistory } from '@/types/petHealth';

export const saveHealthCheckup = async (checkup: HealthCheckup) => {
  const key = `health_checkups_${checkup.petName}`;
  const existing = await getEncryptStorage(key) || [];
  await setEncryptStorage(key, [...existing, checkup]);

  if (checkup.nextCheckupDate) {
    const calendarKey = 'schedules';
    const schedules = await getEncryptStorage(calendarKey) || {};
    const dateKey = checkup.nextCheckupDate.split('T')[0];
    
    if (!schedules[dateKey]) {
      schedules[dateKey] = [];
    }
    
    schedules[dateKey].push({
      id: Date.now(),
      content: `${checkup.petName} 건강검진`,
      date: new Date(checkup.nextCheckupDate),
      type: 'health_checkup'
    });

    await setEncryptStorage(calendarKey, schedules);
  }
};

export const getHealthCheckups = async (petName: string) => {
  const key = `health_checkups_${petName}`;
  return await getEncryptStorage(key) || [];
};

export const saveVaccination = async (vaccination: Vaccination) => {
  const key = `vaccinations_${vaccination.petName}`;
  const existing = await getEncryptStorage(key) || [];
  await setEncryptStorage(key, [...existing, vaccination]);

  if (vaccination.nextVaccinationDate) {
    const calendarKey = 'schedules';
    const schedules = await getEncryptStorage(calendarKey) || {};
    const dateKey = vaccination.nextVaccinationDate.split('T')[0];
    
    if (!schedules[dateKey]) {
      schedules[dateKey] = [];
    }
    
    schedules[dateKey].push({
      id: Date.now(),
      content: `${vaccination.petName} ${vaccination.type} 예방접종`,
      date: new Date(vaccination.nextVaccinationDate),
      type: 'vaccination'
    });

    await setEncryptStorage(calendarKey, schedules);
  }
};

export const saveMedicalHistory = async (history: MedicalHistory) => {
  const key = `medical_history_${history.petName}`;
  const existing = await getEncryptStorage(key) || [];
  await setEncryptStorage(key, [...existing, history]);

  if (history.nextVisitDate) {
    const calendarKey = 'schedules';
    const schedules = await getEncryptStorage(calendarKey) || {};
    const dateKey = history.nextVisitDate.split('T')[0];
    
    if (!schedules[dateKey]) {
      schedules[dateKey] = [];
    }
    
    schedules[dateKey].push({
      id: Date.now(),
      content: `${history.petName} 병원 재방문`,
      date: new Date(history.nextVisitDate),
      type: 'medical_history'
    });

    await setEncryptStorage(calendarKey, schedules);
  }
};

export const getVaccinations = async (petName: string) => {
  const key = `vaccinations_${petName}`;
  return await getEncryptStorage(key) || [];
};

export const getMedicalHistories = async (petName: string) => {
  const key = `medical_history_${petName}`;
  return await getEncryptStorage(key) || [];
};

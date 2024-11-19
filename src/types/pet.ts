import EncryptedStorage from 'react-native-encrypted-storage';

export interface PetBirthInfo {
  birthDate: Date;
  encryptedBirthDate: string;
}

export interface PetFormData {
  petRegNo: string;
  petName: string;
  petBreed: string;
  petGender: string;
  birthDate: Date;
}

export const saveAdoptionDate = async (petName: string, date: Date) => {
  try {
    await EncryptedStorage.setItem(
      `adoption_date_${petName}`,
      date.toISOString(),
    );
  } catch (error) {
    console.error('Error saving adoption date:', error);
  }
};

export const getAdoptionDate = async (petName: string): Promise<Date | null> => {
  try {
    const dateStr = await EncryptedStorage.getItem(`adoption_date_${petName}`);
    return dateStr ? new Date(dateStr) : null;
  } catch (error) {
    console.error('Error getting adoption date:', error);
    return null;
  }
};

export interface Pet {
  petName: string;
  petBreed: string;
  petAge: number;
  petGender: string;
  petRegNo: string;
}

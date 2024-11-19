import axiosInstance from './axios';
import { Pet } from '@/types/pet';

export const addPet = async (pet: Pet) => {
  const {data} = await axiosInstance.post('/pet/add', pet);
  return data;
};

export const getPets = async () => {
  const {data} = await axiosInstance.get('/pet/showAll');
  return data;
};

export const modifyPet = async (name: string, pet: Omit<Pet, 'petRegNo'>) => {
  const {data} = await axiosInstance.put(`/pet/modify/${name}`, pet);
  return data;
};

export const deletePet = async (name: string) => {
  const {data} = await axiosInstance.delete(`/pet/delete?petName=${name}`);
  return data;
};

import axiosInstance from './axios';
import { VetResponse } from '@/types/vet';

export const getVets = async (page: number, size: number): Promise<VetResponse> => {
  const { data } = await axiosInstance.get(`/vets?page=${page}&size=${size}`);
  return data;
}; 
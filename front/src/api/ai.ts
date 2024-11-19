import axiosInstance from './axios';

interface AiScanResult {
  result: string;
  confidence: number;
  recommendation: string;
}

export const sendAiScan = async (
  image: string,
  isDog: boolean,
  hasSymptom: boolean,
): Promise<AiScanResult> => {
  const formData = new FormData();
  formData.append('image', {
    uri: image,
    type: 'image/jpeg',
    name: 'photo.jpg',
  });
  formData.append('isDog', isDog.toString());
  formData.append('hasSymptom', hasSymptom.toString());

  const {data} = await axiosInstance.post<AiScanResult>('/camera', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });

  return data;
};

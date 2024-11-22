import axiosInstance from './axios';

interface AiScanRequest {
  breed: number;  // 0: 강아지, 1: 고양이
  symptom: number; // 0: 무증상, 1: 유증상
}

interface AiScanResponse {
  imgResult: string;
  fileName: string;
}

export const sendAiScan = async (
  image: string,
  petType: 'dog' | 'cat',
  hasSymptom: boolean,
): Promise<AiScanResponse> => {
  const formData = new FormData();
  
  // 이미지 파일 추가
  const filename = image.split('/').pop() || 'photo.jpg';
  formData.append('imageFile', {
    uri: image,
    type: 'image/jpeg',
    name: filename,
  } as any);

  // 차트 데이터 추가
  formData.append('chart', JSON.stringify({
    breed: petType === 'dog' ? 0 : 1,
    symptom: hasSymptom ? 1 : 0
  }));

  const { data } = await axiosInstance.post<AiScanResponse>('/camera', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });

  return data;
};

export const getAiScanImage = async (uuid: string): Promise<string> => {
  const response = await axiosInstance.get(`/camera/file/${uuid}`, {
    responseType: 'blob'
  });
  
  return new Promise<string>((resolve, reject) => {
    const reader = new FileReader();
    reader.onloadend = () => {
      const result = reader.result;
      if (typeof result === 'string') {
        resolve(result);
      } else {
        reject(new Error('Failed to convert blob to base64 string'));
      }
    };
    reader.onerror = reject;
    reader.readAsDataURL(response.data);
  });
};

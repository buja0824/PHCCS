interface ImageUri {
  id?: number;
  uri: string;
}

interface Profile {
  id: number;
  email: string;
  nickName: string | null;
  imageUri: string | null;
  kakaoImageUri: string | null;
  loginType: 'email' | 'kakao' | 'apple';
  isVeterinarian: boolean; 
}

export type { ImageUri, Profile};
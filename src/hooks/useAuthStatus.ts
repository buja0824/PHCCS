import { useState, useEffect } from 'react';
import { getEncryptStorage } from '@/utils/encryptStorage';

function useAuthStatus() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const checkLoginStatus = async () => {
      const token = await getEncryptStorage('refreshToken'); 
      setIsLoggedIn(!!token);
    };
    checkLoginStatus();
  }, []);

  return isLoggedIn;
}

export default useAuthStatus;

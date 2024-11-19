import { useEffect } from 'react';
import useAuth from './queries/useAuth';
import { connectSSE, disconnectSSE } from '@/api/sse';


export const useSSE = () => {
  const { isLogin } = useAuth();

  useEffect(() => {
    if (isLogin) {
      connectSSE();
    }

    return () => {
      disconnectSSE();
    };
  }, [isLogin]);
};

import { useState, useEffect } from 'react';
import { useQueryClient, useQuery } from '@tanstack/react-query';
import { getLikedPosts } from '@/api/post';
import { getLikedComments } from '@/api/comment';

interface UseLikeToggleProps {
  itemId: number;
  postId?: number;
  category: string;
  initialLikeCount: number;
  initialIsLiked?: boolean;
  queryKey: (string | number)[];
  toggleLikeFn: (...args: any[]) => Promise<any>;
  onLikeToggle?: (isLiked: boolean, likeCnt: number) => void;
}

export const useLikeToggle = ({
  itemId,
  postId,
  category,
  initialLikeCount,
  initialIsLiked = false,
  queryKey,
  toggleLikeFn,
  onLikeToggle
}: UseLikeToggleProps) => {
  const [isLiked, setIsLiked] = useState(initialIsLiked);
  const [localLikeCount, setLocalLikeCount] = useState(initialLikeCount);
  const [isProcessing, setIsProcessing] = useState(false);
  const queryClient = useQueryClient();

  useEffect(() => {
    setIsLiked(initialIsLiked);
  }, [initialIsLiked]);

  useEffect(() => {
    setLocalLikeCount(initialLikeCount);
  }, [initialLikeCount]);

  const handleLike = async () => {
    if (isProcessing) return;
    
    try {
      setIsProcessing(true);
      const newLikedState = !isLiked;
      
      // API 호출
      await toggleLikeFn(
        category,
        postId || itemId,
        postId ? itemId : undefined
      );

      // 상태 업데이트
      const newLikeCount = newLikedState ? localLikeCount + 1 : localLikeCount - 1;
      setIsLiked(newLikedState);
      setLocalLikeCount(newLikeCount);
      
      if (onLikeToggle) {
        onLikeToggle(newLikedState, newLikeCount);
      }

      // 캐시 무효화
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ['likedComments'] }),
        queryClient.invalidateQueries({ queryKey })
      ]);
      
    } catch (error) {
      // 에러 발생 시 상태 복구
      setIsLiked(!isLiked);
      setLocalLikeCount(localLikeCount);
      console.error('Failed to toggle like:', error);
    } finally {
      setIsProcessing(false);
    }
  };

  return {
    isLiked,
    localLikeCount,
    handleLike,
    isProcessing
  };
};

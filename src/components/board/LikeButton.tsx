import React, { useState, useEffect } from 'react';
import { TouchableOpacity, Text, StyleSheet, View } from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';
import { colors } from '@/constants';
import { toggleLikePost, getLikedPosts } from '@/api/post';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { CompoundOption } from '../common/CompoundOption';

interface LikeButtonProps {
  category: string;
  postId: number;
  onLikeToggle: (isLiked: boolean, likeCount: number) => void;
  initialLikeCount: number;
}

function LikeButton({ category, postId, onLikeToggle, initialLikeCount }: LikeButtonProps) {
  const [isLiked, setIsLiked] = useState(false);
  const [localLikeCount, setLocalLikeCount] = useState(initialLikeCount);
  const [isProcessing, setIsProcessing] = useState(false);
  const queryClient = useQueryClient();

  const { data: likedPosts } = useQuery({
    queryKey: ['likedPosts'],
    queryFn: getLikedPosts,
  });

  useEffect(() => {
    if (likedPosts) {
      const isCurrentPostLiked = likedPosts.some(post => post.id === postId);
      setIsLiked(isCurrentPostLiked);
    }
  }, [likedPosts, postId]);

  const handleLikePress = async () => {
    if (isProcessing) return;
    
    try {
      setIsProcessing(true);
      const likeCnt = await toggleLikePost(category, postId);
      console.log('좋아요 응답 (likeCnt):', likeCnt);
      
      const newLikedState = !isLiked;
      setIsLiked(newLikedState);
      setLocalLikeCount(likeCnt);
      onLikeToggle(newLikedState, likeCnt);
      
      await Promise.all([
        queryClient.invalidateQueries({ 
          queryKey: ['post', category, postId.toString()]
        }),
        queryClient.invalidateQueries({ 
          queryKey: ['likedPosts']
        })
      ]);
    } catch (error) {
      console.error('Failed to toggle like:', error);
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <TouchableOpacity 
      style={[styles.container, isProcessing && styles.disabled]} 
      onPress={handleLikePress}
      activeOpacity={0.7}
      disabled={isProcessing}
    >
      <Icon 
        name={isLiked ? 'heart' : 'heart-outline'} 
        size={18} 
        color={isLiked ? colors.light.PINK_700 : colors.light.GRAY_600} 
      />
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 4,
    justifyContent: 'center',
    alignItems: 'center',
    height: 28,
  },
  modalContainer: {
    padding: 20,
    alignItems: 'center',
  },
  modalText: {
    fontSize: 16,
    color: colors.light.BLACK,
    textAlign: 'center',
  },
  likeCount: {
    marginLeft: 4,
    fontSize: 16,
    color: colors.light.GRAY_400,
  },
  disabled: {
    opacity: 0.5,
  },
});

export default LikeButton;


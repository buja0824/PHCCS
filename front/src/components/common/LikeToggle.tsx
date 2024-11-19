import React, { useState, useEffect } from 'react';
import { TouchableOpacity, Text, StyleSheet } from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';
import { colors } from '@/constants';
import { useLikeToggle } from '@/hooks/useLikeToggle';

interface LikeToggleProps {
  itemId: number;
  postId?: number;
  category: string;
  initialLikeCount: number;
  initialIsLiked?: boolean;
  queryKey: (string | number)[];
  toggleLikeFn: (...args: any[]) => Promise<any>;
  onLikeToggle?: (isLiked: boolean, likeCnt: number) => void;
}

export const LikeToggle = ({
  itemId,
  postId,
  category,
  initialLikeCount,
  initialIsLiked = false,
  queryKey,
  toggleLikeFn,
  onLikeToggle
}: LikeToggleProps) => {
  const { isLiked, localLikeCount, handleLike, isProcessing } = useLikeToggle({
    itemId,
    postId,
    category,
    initialLikeCount,
    initialIsLiked,
    queryKey,
    toggleLikeFn,
    onLikeToggle
  });

  return (
    <TouchableOpacity 
      onPress={handleLike} 
      style={styles.container}
      disabled={isProcessing}
    >
      <Icon 
        name={isLiked ? "heart" : "heart-outline"} 
        size={17} 
        color={isLiked ? colors.light.PINK_700 : colors.light.GRAY_600} 
      />
      <Text style={styles.count}>{localLikeCount}</Text>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
  },
  count: {
    fontSize: 14,
  }
});

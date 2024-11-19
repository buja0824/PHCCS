import React, { memo } from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { Post } from '@/types/post';
import Icon from 'react-native-vector-icons/Ionicons';
import { colors } from '@/constants';
import { formatDate } from '@/utils/dateFormat';

interface PostItemProps extends Pick<Post, 'title' | 'partOfContent' | 'createDate' | 'nickName'> {
  viewCnt: number;
  likeCnt: number;
  onPress: () => void;
}

const PostItem: React.FC<PostItemProps> = memo(({ 
  title, 
  nickName,
  partOfContent, 
  viewCnt, 
  likeCnt, 
  createDate, 
  onPress 
}) => {
  const formattedDate = formatDate(createDate);
  
  return (
    <TouchableOpacity 
      style={styles.container} 
      onPress={onPress}
      activeOpacity={0.7}
    >
      <View style={styles.contentContainer}>
        <View style={styles.header}>
          <Text style={styles.title} numberOfLines={1}>{title}</Text>
          <Text style={styles.date}>{formattedDate}</Text>
        </View>
        <Text style={styles.content} numberOfLines={3}>{partOfContent}</Text>
        <View style={styles.footer}>
          <Text style={styles.nickname}>{nickName}</Text>
          <View style={styles.stats}>
            <View style={styles.statItem}>
              <Icon name="eye-outline" size={16} color={colors.light.GRAY_600} />
              <Text style={[styles.statText, { color: colors.light.GRAY_600 }]}>{viewCnt}</Text>
            </View>
            <View style={styles.statItem}>
              <Icon 
                name={likeCnt > 0 ? 'heart' : 'heart-outline'} 
                size={16} 
                color={likeCnt > 0 ? colors.light.PINK_700 : colors.light.GRAY_600} 
              />
              <Text style={[
                styles.statText,
                likeCnt > 0 ? { color: colors.light.PINK_700 } : { color: colors.light.GRAY_600 }
              ]}>
                {likeCnt}
              </Text>
            </View>
          </View>
        </View>
      </View>
    </TouchableOpacity>
  );
});

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#ffffff',
    padding: 14,
    borderRadius: 12,
    marginHorizontal: 16,
    marginVertical: 8,
    elevation: 3,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.15,
    shadowRadius: 3,
  },
  contentContainer: {
    flex: 1,
  },
  title: {
    fontSize: 16,
    fontWeight: '600',
    color: colors.light.GRAY_900,
    marginBottom: 6,
    letterSpacing: -0.3,
  },
  content: {
    fontSize: 14,
    color: colors.light.GRAY_600,
    lineHeight: 18,
    marginBottom: 10,
    letterSpacing: -0.2,
  },
  footer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 8,
    paddingTop: 8,
    borderTopWidth: 1,
    borderTopColor: colors.light.GRAY_200,
  },
  leftFooter: {
    flex: 1,
  },
  date: {
    fontSize: 12,
    color: colors.light.GRAY_600,
  },
  rightFooter: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
  },
  nickname: {
    fontSize: 13,
    color: colors.light.GRAY_600,
    fontWeight: '500',
  },
  stats: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  statItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginLeft: 16,
    backgroundColor: colors.light.GRAY_50,
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 12,
  },
  statText: {
    fontSize: 12,
    color: colors.light.GRAY_600,
    marginLeft: 4,
    fontWeight: '500',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
});

export default PostItem;

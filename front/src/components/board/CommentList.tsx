import React from 'react';
import { View, StyleSheet, ActivityIndicator, ScrollView, Text } from 'react-native';
import { useQuery } from '@tanstack/react-query';
import { getComments, getMyComments, getLikedComments } from '@/api/comment';
import CommentItem from './CommentItem';
import useAuth from '@/hooks/queries/useAuth';
import { colors } from '@/constants';
import { NavigationProp } from '@react-navigation/native';
import { BoardStackParamList } from '@/navigations/stack/BoardStackNavigator';

interface CommentListProps {
  category: string;
  postId: number;
  navigation: NavigationProp<BoardStackParamList>;
}

function CommentList({ category, postId, navigation }: CommentListProps) {
  const { getProfileQuery } = useAuth();
  const { data: userProfile } = getProfileQuery;
  
  const { data: myComments } = useQuery({
    queryKey: ['myComments'],
    queryFn: getMyComments,
    enabled: !!userProfile,
  });

  const { data: comments, isLoading } = useQuery({
    queryKey: ['comments', category, postId],
    queryFn: async () => {
      const commentsData = await getComments(category, postId);
      const likedComments = await getLikedComments();
      
      return commentsData.map(comment => ({
        ...comment,
        isMine: myComments?.some(myComment => 
          myComment.id === comment.id && 
          myComment.postId === comment.postId
        ) || false,
        isLiked: likedComments.some(
          likedComment => likedComment.id === comment.id
        ) || false
      }));
    },
    enabled: !!myComments,
  });

  if (isLoading) {
    return <ActivityIndicator style={styles.loader} color="#c62917" />;
  }

  return (
    <View style={styles.container}>
      <ScrollView style={styles.scrollView}>
        {comments?.length ? (
          comments.map((comment) => (
            <CommentItem
              key={comment.id}
              comment={comment}
              category={category}
              postId={postId}
              navigation={navigation}
            />
          ))
        ) : (
          <View style={styles.emptyContainer}>
            <Text style={styles.emptyText}>아직 댓글이 없습니다.</Text>
          </View>
        )}
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  scrollView: {
    flex: 1,
  },
  loader: {
    marginVertical: 20,
  },
  emptyContainer: {
    padding: 20,
    alignItems: 'center',
  },
  emptyText: {
    fontSize: 14,
    color: colors.light.GRAY_400,
  },
});

export default CommentList; 
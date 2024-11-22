import React, { useState, useEffect, useMemo } from 'react';
import { 
  View, 
  Text, 
  StyleSheet, 
  TouchableOpacity, 
  TextInput,
  Alert 
} from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';
import { boardNavigations, colors } from '@/constants';
import { Comment } from '@/types/comment';
import { updateComment, deleteComment, likeComment, getLikedComments } from '@/api/comment';
import { useQueryClient, useQuery } from '@tanstack/react-query';
import { formatDate } from '@/utils/dateFormat';
import useModal from '@/hooks/useModal';
import { CompoundOption } from '../common/CompoundOption';
import { LikeToggle } from '../common/LikeToggle';
import { useNavigation } from '@react-navigation/native';
import { NavigationProp } from '@react-navigation/native';
import { BoardStackParamList } from '@/navigations/stack/BoardStackNavigator';
import { createChatRoom } from '@/api/chat';

interface CommentItemProps {
  comment: Comment;
  category: string;
  postId: number;
  navigation: NavigationProp<BoardStackParamList>;
  onPress?: () => void;
}

function CommentItem({ comment, category, postId, navigation, onPress }: CommentItemProps) {
  const [isEditing, setIsEditing] = useState(false);
  const [editContent, setEditContent] = useState(comment.comment);
  const [isLiked, setIsLiked] = useState(false);
  const [localLikeCount, setLocalLikeCount] = useState(comment.likeCnt || 0);
  const [isProcessing, setIsProcessing] = useState(false);
  const optionModal = useModal();
  const deleteConfirmModal = useModal();
  const queryClient = useQueryClient();

  const { data: likedComments } = useQuery({
    queryKey: ['likedComments'],
    queryFn: getLikedComments
  });

  const isCommentLiked = useMemo(() => {
    if (!likedComments) return false;
    return likedComments.some(
      likedComment => likedComment.id === comment.id
    );
  }, [likedComments, comment.id]);

  const handleUpdate = async () => {
    if (!editContent.trim()) {
      Alert.alert('알림', '댓글 내용을 입력해주세요.');
      return;
    }

    try {
      await updateComment(category, postId, comment.id, editContent);
      setIsEditing(false);
      queryClient.invalidateQueries({ 
        queryKey: ['comments', category, postId] 
      });
    } catch (error) {
      Alert.alert('오류', '댓글 수정에 실패했습니다.');
    }
  };

  const handleDelete = async () => {
    try {
      await deleteComment(category, postId, comment.id);
      queryClient.invalidateQueries({ 
        queryKey: ['comments', category, postId] 
      });
    } catch (error) {
      Alert.alert('오류', '댓글 삭제에 실패했습니다.');
    }
  };

  const handleLike = async () => {
    if (isProcessing) return;
    
    try {
      setIsProcessing(true);
      const likeCnt = await likeComment(category, postId, comment.id);
      
      const newLikedState = !isLiked;
      setIsLiked(newLikedState);
      
      const newLikeCount = newLikedState ? comment.likeCnt + 1 : comment.likeCnt - 1;
      setLocalLikeCount(newLikeCount);
      
      queryClient.setQueryData(
        ['comments', category, postId],
        (oldData: any) => {
          if (!oldData) return oldData;
          return oldData.map((c: any) => 
            c.id === comment.id 
              ? { ...c, likeCnt: newLikeCount }
              : c
          );
        }
      );
      
      await queryClient.invalidateQueries({ 
        queryKey: ['comments', category, postId]
      });
    } catch (error) {
      console.error('Failed to toggle like:', error);
    } finally {
      setIsProcessing(false);
    }
  };

  const handleChatRequest = async () => {
    try {
      const chatRoom = await createChatRoom(
        comment.memberId,
        `${comment.nickName}님과의 대화`
      );
      
      navigation.navigate(boardNavigations.CHAT_ROOM, {
        roomId: chatRoom.roomId,
        otherUserName: comment.nickName
      });
    } catch (error) {
      Alert.alert('오류', '채팅방 생성에 실패했습니다.');
    }
  };

  return (
    <TouchableOpacity 
      style={styles.container} 
      onPress={onPress}
      activeOpacity={0.7}
    >
      <View style={styles.header}>
        <View style={styles.userInfo}>
          <Text style={styles.nickname}>
            {comment.nickName}
          </Text>
          <Text style={styles.date}>{formatDate(comment.writeTime)}</Text>
        </View>
        <View style={styles.actions}>
          {!comment.isMine && (
            <TouchableOpacity 
              style={styles.chatButton}
              onPress={handleChatRequest}
            >
              <Icon 
                name="chatbubble-outline" 
                size={16} 
                color={colors.light.PINK_500} 
              />
            </TouchableOpacity>
          )}
          <LikeToggle
            itemId={comment.id}
            postId={postId}
            category={category}
            initialLikeCount={comment.likeCnt}
            initialIsLiked={isCommentLiked}
            queryKey={['comments', category, postId]}
            toggleLikeFn={likeComment}
          />
          {comment.isMine && (
            <TouchableOpacity 
              style={styles.moreButton}
              onPress={optionModal.show}
            >
              <Icon 
                name="ellipsis-vertical" 
                size={20} 
                color={colors.light.GRAY_600} 
              />
            </TouchableOpacity>
          )}
        </View>
      </View>
      
      {isEditing ? (
        <View style={styles.editContainer}>
          <TextInput
            style={styles.editInput}
            value={editContent}
            onChangeText={setEditContent}
            multiline
          />
          <View style={styles.editActions}>
            <TouchableOpacity 
              onPress={() => setIsEditing(false)}
              style={styles.cancelButton}
            >
              <Text style={styles.cancelText}>취소</Text>
            </TouchableOpacity>
            <TouchableOpacity 
              onPress={handleUpdate}
              style={styles.updateButton}
            >
              <Text style={styles.updateText}>수정</Text>
            </TouchableOpacity>
          </View>
        </View>
      ) : (
        <Text style={styles.content}>{comment.comment}</Text>
      )}

      <CompoundOption isVisible={optionModal.isVisible} hideOption={optionModal.hide}>
        <CompoundOption.Background>
          <CompoundOption.Container>
            <CompoundOption.Button onPress={() => {
              setIsEditing(true);
              optionModal.hide();
            }}>
              수정
            </CompoundOption.Button>
            <CompoundOption.Divider />
            <CompoundOption.Button onPress={() => {
              deleteConfirmModal.show();
              optionModal.hide();
            }} isDanger>
              삭제
            </CompoundOption.Button>
          </CompoundOption.Container>
          <CompoundOption.Container>
            <CompoundOption.Button onPress={optionModal.hide}>
              취소
            </CompoundOption.Button>
          </CompoundOption.Container>
        </CompoundOption.Background>
      </CompoundOption>

      <CompoundOption isVisible={deleteConfirmModal.isVisible} hideOption={deleteConfirmModal.hide}>
        <CompoundOption.Background>
          <CompoundOption.Container>
            <View style={styles.deleteConfirmContainer}>
              <Text style={styles.deleteConfirmTitle}>댓글 삭제</Text>
              <Text style={styles.deleteConfirmMessage}>
                정말 이 댓글을 삭제하시겠습니까?
              </Text>
            </View>
            <CompoundOption.Button onPress={() => {
              handleDelete();
              deleteConfirmModal.hide();
            }} isDanger>
              삭제
            </CompoundOption.Button>
            <CompoundOption.Divider />
            <CompoundOption.Button onPress={deleteConfirmModal.hide}>
              취소
            </CompoundOption.Button>
          </CompoundOption.Container>
        </CompoundOption.Background>
      </CompoundOption>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
    backgroundColor: '#fff',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 8,
  },
  userInfo: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  nickname: {
    fontSize: 13,
    fontWeight: 'bold',
    color: colors.light.GRAY_700,
    marginRight: 8,
  },
  date: {
    fontSize: 12,
    color: colors.light.GRAY_400,
  },
  content: {
    fontSize: 14,
    color: colors.light.GRAY_900,
    marginBottom: 8,
    lineHeight: 20,
  },
  actions: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  actionButton: {
    padding: 6,
    backgroundColor: colors.light.GRAY_100,
    borderRadius: 6,
    borderWidth: 1,
    borderColor: colors.light.GRAY_200,
  },
  likeButton: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.light.GRAY_50,
    paddingVertical: 6,
    paddingHorizontal: 8,
    borderRadius: 6,
  },
  likeCount: {
    fontSize: 13,
    color: colors.light.GRAY_500,
    marginLeft: 4,
    fontWeight: '500',
  },
  likedCount: {
    color: colors.light.PINK_700,
  },
  editContainer: {
    marginBottom: 8,
  },
  editInput: {
    borderWidth: 1,
    borderColor: colors.light.GRAY_200,
    borderRadius: 8,
    padding: 12,
    marginBottom: 8,
    minHeight: 60,
    backgroundColor: colors.light.GRAY_50,
    color: colors.light.GRAY_900,
  },
  editActions: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    gap: 8,
  },
  cancelButton: {
    padding: 8,
    borderRadius: 6,
    backgroundColor: colors.light.GRAY_100,
  },
  cancelText: {
    color: colors.light.GRAY_700,
    fontSize: 13,
    fontWeight: '500',
  },
  updateButton: {
    backgroundColor: colors.light.PINK_700,
    padding: 8,
    borderRadius: 6,
  },
  updateText: {
    color: colors.light.WHITE,
    fontSize: 13,
    fontWeight: '500',
  },
  moreButton: {
    padding: 6,
    borderRadius: 6,
  },
  deleteConfirmContainer: {
    padding: 20,
    alignItems: 'center',
  },
  deleteConfirmTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: colors.light.BLACK,
    marginBottom: 10,
  },
  deleteConfirmMessage: {
    fontSize: 15,
    color: colors.light.BLACK,
    textAlign: 'center',
  },
  chatButton: {
    padding: 6,
    marginRight: 8,
  },
  actionButtons: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 8,
  }
});

export default CommentItem; 
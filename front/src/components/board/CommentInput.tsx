import React, { useState } from 'react';
import { 
  View, 
  TextInput, 
  TouchableOpacity, 
  StyleSheet,
  Alert,
} from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';
import { colors } from '@/constants';
import { useQueryClient } from '@tanstack/react-query';
import { createComment } from '@/api/comment';
import useAuth from '@/hooks/queries/useAuth';
interface CommentInputProps {
  category: string;
  postId: number;
}

function CommentInput({ category, postId }: CommentInputProps) {
  const [content, setContent] = useState('');
  const queryClient = useQueryClient();
  const { getProfileQuery } = useAuth();
  const { nickName } = getProfileQuery.data || {};

  const handleSubmit = async () => {
    if (!content.trim()) {
      Alert.alert('알림', '댓글 내용을 입력해주세요.');
      return;
    }

    if (!nickName) {
      Alert.alert('알림', '로그인이 필요합니다.');
      return;
    }

    try {
      const newComment = await createComment(category, postId, content, nickName);
      setContent('');
      
      const commentWithIsMine = {
        ...newComment,
        isMine: true
      };

      queryClient.setQueryData(['comments', category, postId], (oldData: any) => {
        if (!oldData) return [commentWithIsMine];
        return [...oldData, commentWithIsMine];
      });

      queryClient.invalidateQueries({ 
        queryKey: ['comments', category, postId] 
      });
    } catch (error) {
      Alert.alert('오류', '댓글 작성에 실패했습니다.');
    }
  };

  return (
    <View style={styles.container}>
      <TextInput
        style={styles.input}
        value={content}
        onChangeText={setContent}
        placeholder="댓글을 입력하세요"
        multiline
      />
      <TouchableOpacity style={styles.submitButton} onPress={handleSubmit}>
        <Icon name="send" size={24} color={colors.light.PINK_700} />
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    padding: 8,
    backgroundColor: '#fff',
    minHeight: 60,
  },
  input: {
    flex: 1,
    minHeight: 40,
    maxHeight: 100,
    backgroundColor: '#f5f5f5',
    borderRadius: 20,
    paddingHorizontal: 15,
    paddingVertical: 8,
    marginRight: 8,
  },
  submitButton: {
    justifyContent: 'center',
    alignItems: 'center',
    width: 40,
  },
});

export default CommentInput; 
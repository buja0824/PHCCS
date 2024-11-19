import axiosInstance from './axios';
import { Comment } from '@/types/comment';

// 댓글 목록 조회
export const getComments = async (category: string, postId: number): Promise<Comment[]> => {
  const { data } = await axiosInstance.get(`/comment/show/${category}/${postId}`);
  return data;
};

// 댓글 작성
export const createComment = async (
  category: string, 
  postId: number, 
  comment: string,
  nickName: string
): Promise<Comment> => {
  const { data } = await axiosInstance.post(
    `/comment/add/${category}/${postId}`, 
    {
      comment,
      nickName
    }
  );
  return data;
};

// 댓글 수정
export const updateComment = async (
  category: string,
  postId: number, 
  commentId: number, 
  content: string
): Promise<Comment> => {
  const { data } = await axiosInstance.put(
    `/comment/update/${category}/${postId}/${commentId}`, 
    { comment: content }
  );
  return data;
};

// 댓글 삭제
export const deleteComment = async (
  category: string,
  postId: number, 
  commentId: number
): Promise<void> => {
  await axiosInstance.delete(`/comment/delete/${category}/${postId}/${commentId}`);
};

// 내가 좋아요한 댓글 목록 조회
export const getLikedComments = async (): Promise<Comment[]> => {
  const { data } = await axiosInstance.get('/comment/liked-comments');
  return data;
};

// 댓글 좋아요 토글
export const likeComment = async (
  category: string,
  postId: number,
  commentId: number
): Promise<number> => {
  const { data } = await axiosInstance.post(`/comment/like/${category}/${postId}/${commentId}`);
  return data.likeCnt || 0;
};

// 내 댓글 목록 조회
export const getMyComments = async (): Promise<Comment[]> => {
  const { data } = await axiosInstance.get('/comment/my');
  return data;
}; 
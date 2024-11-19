export interface Comment {
  id: number;
  postId: number;
  memberId: number;
  comment: string;
  nickName: string;
  likeCnt: number;
  writeTime: string;
  isLiked?: boolean;
  isMine?: boolean;
  category?: string;
} 
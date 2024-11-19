import axiosInstance from './axios';
import { Post } from '@/types/post';

export interface PostsResponse {
  posts: Post[];
  nextCursor: number | null;
}
//게시글 목록 가져오기
export const getPosts = async (category: string, page: number = 1, size: number = 10, searchName: string = ''): Promise<Post[]> => {
  const { data } = await axiosInstance.get(`/board/show/${category}`, {
    params: { page, size, searchName },
  });
  return data;
};
//게시글 상세 가져오기
export const getPost = async (category: string, id: number): Promise<Post> => {
  const { data } = await axiosInstance.get(`/board/show/${category}/${id}`);
  return {
    ...data,
    fileList: data.fileList || [], // fileList가 null이면 빈 배열로
  };
};
//게시글 작성
export const createPost = async (category: string, title: string, content: string, images: string[]) => {
  try {
    const formData = new FormData();
    
    // PostDTO를 JSON 문자열로 직접 추가
    formData.append('dto', JSON.stringify({
      category,
      title,
      content
    }));

    // 이미지 파일들 추가
    if (images && images.length > 0) {
      images.forEach((uri) => {
        const filename = uri.split('/').pop() || 'image.jpg';
        formData.append('imageFiles', {
          uri,
          type: 'image/jpeg',
          name: filename,
        } as any);
      });
    }

    const response = await axiosInstance.post('/board/post', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    
    return response.data;
    
  } catch (error) {
    console.error('Create post error:', error);
    throw error;
  }
};
//좋아요 누르기
export const toggleLikePost = async (category: string, id: number): Promise<number> => {
  const { data } = await axiosInstance.post(`/board/like/${category}/${id}`);
  return typeof data?.likeCnt === 'number' ? data.likeCnt : 0;
};
//내가 쓴 게시글 가져오기
export const getMyPosts = async (): Promise<Post[]> => {
  try {
    const { data } = await axiosInstance.get('/board/my'); 
    return Array.isArray(data) ? data : [];
  } catch (error) {
    console.error('Failed to fetch my posts:', error);
    return [];
  }
};

//게시글 이미지 가져오기
export const getPostImage = async (uuid: string, category: string, postId: number): Promise<string> => {
  const response = await axiosInstance.get(`/board/file/${uuid}/${category}/${postId}`, {
    responseType: 'blob'
  });
  
  return new Promise<string>((resolve, reject) => {
    const reader = new FileReader();
    reader.onloadend = () => {
      const result = reader.result;
      if (typeof result === 'string') {
        resolve(result);
      } else {
        reject(new Error('Failed to convert blob to base64 string'));
      }
    };
    reader.onerror = reject;
    reader.readAsDataURL(response.data);
  });
};

//좋아요 누른 게시글 가져오기
export const getLikedPosts = async (): Promise<Post[]> => {
  try {
    const { data } = await axiosInstance.get('/board/liked-posts');
    return Array.isArray(data) ? data : [];
  } catch (error) {
    console.error('Failed to fetch liked posts:', error);
    return [];
  }
};

// 게시글 수정
export const updatePost = async (
  category: string,
  postId: number,
  title: string,
  content: string
): Promise<void> => {
  const formData = new FormData();
  const updateParam = {
    category,
    title,
    content
  };
  
  formData.append('updateParam', JSON.stringify(updateParam));
  
  await axiosInstance.put(
    `/board/update/${category}/${postId}`,
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    }
  );
};

// 게시글 삭제
export const deletePost = async (
  category: string,
  postId: number
): Promise<void> => {
  await axiosInstance.delete(`/board/delete/${category}/${postId}`);
};

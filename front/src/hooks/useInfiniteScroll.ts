import { useInfiniteQuery } from '@tanstack/react-query';
import { getPosts, getMyPosts } from '../api/post';
import { Post } from '@/types/post';

export interface InfinitePostsResponse {
  posts: Post[];
  nextPage: number | null;
}

export const useInfiniteScroll = (category: string = '', searchName: string = '', isMyPosts: boolean = false) => {
  return useInfiniteQuery<InfinitePostsResponse>({
    queryKey: isMyPosts ? ['myPosts'] : ['posts', category, searchName],
    queryFn: async ({ pageParam = 1 }) => {
      const response = isMyPosts 
        ? await getMyPosts()
        : await getPosts(category, pageParam as number, 20, searchName);
      return {
        posts: response,
        nextPage: response.length === 20 ? (pageParam as number) + 1 : null,
      };
    },
    initialPageParam: 1,
    getNextPageParam: (lastPage) => lastPage.nextPage,
    staleTime: 1000 * 60 * 5,
    gcTime: 1000 * 60 * 30,
  });
};

// import { useInfiniteQuery } from '@tanstack/react-query';
// import { getPosts } from '../api/post';
// import { Post } from '@/types/post';

// export interface InfinitePostsResponse {
//   posts: Post[];
//   nextPage: number | null;
// }

// export const useInfiniteScroll = (category: string, searchName: string = '') => {
//   return useInfiniteQuery<InfinitePostsResponse>({
//     queryKey: ['posts', category, searchName],
//     queryFn: async ({ pageParam = 1 }) => {
//       const response = await getPosts(category, pageParam as number, 20, searchName);
//       return {
//         posts: response,
//         nextPage: response.length === 20 ? (pageParam as number) + 1 : null,
//       };
//     },
//     initialPageParam: 1,
//     getNextPageParam: (lastPage) => lastPage.nextPage,
//     staleTime: 1000 * 60 * 5, // 5분
//     gcTime: 1000 * 60 * 30, // 30분 (이전의 cacheTime)
//   });
// };


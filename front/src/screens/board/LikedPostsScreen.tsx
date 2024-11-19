import React, { useState, useRef, useCallback } from 'react';
import { 
  View, 
  Text, 
  StyleSheet, 
  ActivityIndicator,
  FlatList,
  RefreshControl,
  TouchableOpacity,
  Animated,
  ScrollView
} from 'react-native';
import { StackScreenProps } from '@react-navigation/stack';
import { BoardStackParamList } from '@/navigations/stack/BoardStackNavigator';
import PostItem from '@/components/board/PostItem';
import Icon from 'react-native-vector-icons/Ionicons';
import { useQuery } from '@tanstack/react-query';
import { getLikedPosts } from '@/api/post';
import { colors } from '@/constants';
import { Post } from '@/types/post';
import useAuth from '@/hooks/queries/useAuth';

type Props = StackScreenProps<BoardStackParamList, 'LikedPosts'>;

interface CategoryPosts {
  [key: string]: Post[];
}

const BOARD_CATEGORIES: { [key: string]: string } = {
  community_board: '자유 게시판',
  qna_board: '질문 게시판',
  vet_board: '수의사 게시판'
};

function LikedPostsScreen({ navigation }: Props) {
  const { isLogin, isLoginLoading } = useAuth();
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [showScrollTop, setShowScrollTop] = useState(false);
  const flatListRef = useRef<FlatList>(null);
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  
  const { data: posts, isLoading, refetch } = useQuery({
    queryKey: ['likedPosts'],
    queryFn: getLikedPosts,
    enabled: isLogin,
  });

  const filteredPosts = posts?.filter(post => {
    if (selectedCategory === 'all') return true;
    return post.category === selectedCategory;
  });

  const handleRefresh = useCallback(async () => {
    setIsRefreshing(true);
    await refetch();
    setIsRefreshing(false);
  }, [refetch]);

  const handleScroll = useCallback((event: any) => {
    const offsetY = event.nativeEvent.contentOffset.y;
    setShowScrollTop(offsetY > 150);
  }, []);

  const scrollToTop = useCallback(() => {
    flatListRef.current?.scrollToOffset({ offset: 0, animated: true });
  }, []);

  const renderCategory = useCallback(({ item: [category, title] }: { item: [string, string] }) => {
    const categoryPosts = posts?.reduce((acc: CategoryPosts, post: Post) => {
      const postCategory = post.category || 'community_board';
      if (!acc[postCategory]) {
        acc[postCategory] = [];
      }
      acc[postCategory].push(post);
      return acc;
    }, {} as CategoryPosts)[category] || [];

    if (categoryPosts.length === 0) return null;

    return (
      <View key={category} style={styles.categorySection}>
        <View style={styles.categoryHeader}>
          <Text style={styles.categoryTitle}>{title}</Text>
          <Text style={styles.postCount}>총 {categoryPosts.length}개</Text>
        </View>
        {categoryPosts.map((post: Post) => (
          <PostItem
            key={post.id}
            title={post.title}
            partOfContent={post.partOfContent}
            viewCnt={post.viewCnt}
            likeCnt={post.likeCnt}
            createDate={post.createDate}
            nickName={post.nickName}
            onPress={() => navigation.navigate('PostDetail', { 
              id: post.id.toString(), 
              category: post.category 
            })}
          />
        ))}
      </View>
    );
  }, [posts, navigation]);

  if (isLoginLoading) {
    return <ActivityIndicator style={styles.loader} color={colors.light.PINK_700} />;
  }

  if (!isLogin) {
    return (
      <View style={styles.container}>
        <Icon name="alert-circle-outline" size={48} color={colors.light.PINK_700} />
        <Text style={styles.message}>로그인이 필요합니다.</Text>
      </View>
    );
  }

  if (!posts) {
    return (
      <View style={styles.emptyContainer}>
        <Text style={styles.emptyMessage}>공감한 게시글이 없습니다.</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <View style={styles.filterContainer}>
        <ScrollView 
          horizontal 
          showsHorizontalScrollIndicator={false}
          style={styles.categoryScroll}
        >
          <TouchableOpacity
            style={[
              styles.typeButton, 
              selectedCategory === 'all' && styles.typeButtonActive
            ]}
            onPress={() => setSelectedCategory('all')}
          >
            <Text style={[
              styles.typeButtonText,
              selectedCategory === 'all' && styles.typeButtonTextActive
            ]}>
              전체
            </Text>
          </TouchableOpacity>
          
          <TouchableOpacity
            style={[
              styles.typeButton, 
              selectedCategory === 'community_board' && styles.typeButtonActive
            ]}
            onPress={() => setSelectedCategory('community_board')}
          >
            <Text style={[
              styles.typeButtonText,
              selectedCategory === 'community_board' && styles.typeButtonTextActive
            ]}>
              자유
            </Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[
              styles.typeButton, 
              selectedCategory === 'qna_board' && styles.typeButtonActive
            ]}
            onPress={() => setSelectedCategory('qna_board')}
          >
            <Text style={[
              styles.typeButtonText,
              selectedCategory === 'qna_board' && styles.typeButtonTextActive
            ]}>
              질문
            </Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[
              styles.typeButton, 
              selectedCategory === 'vet_board' && styles.typeButtonActive
            ]}
            onPress={() => setSelectedCategory('vet_board')}
          >
            <Text style={[
              styles.typeButtonText,
              selectedCategory === 'vet_board' && styles.typeButtonTextActive
            ]}>
              수의사
            </Text>
          </TouchableOpacity>
        </ScrollView>
      </View>

      <FlatList
        ref={flatListRef}
        data={filteredPosts}
        renderItem={({ item }) => (
          <PostItem
            title={item.title}
            partOfContent={item.partOfContent}
            viewCnt={item.viewCnt}
            likeCnt={item.likeCnt}
            createDate={item.createDate}
            nickName={item.nickName}
            onPress={() => navigation.navigate('PostDetail', { 
              id: item.id.toString(), 
              category: item.category 
            })}
          />
        )}
        keyExtractor={(item) => item.id.toString()}
        onScroll={handleScroll}
        scrollEventThrottle={16}
        contentContainerStyle={{ paddingTop: 16 }}
        refreshControl={
          <RefreshControl
            refreshing={isRefreshing}
            onRefresh={handleRefresh}
            colors={[colors.light.PINK_700]}
            tintColor={colors.light.PINK_700}
          />
        }
      />
      {showScrollTop && (
        <Animated.View style={styles.scrollTopButton}>
          <TouchableOpacity
            onPress={scrollToTop}
            style={styles.scrollTopButtonInner}
          >
            <Icon name="arrow-up-circle" size={50} color={colors.light.PINK_700} />
            <Text style={styles.scrollTopText}>TOP</Text>
          </TouchableOpacity>
        </Animated.View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.light.GRAY_50,
  },
  loader: {
    flex: 1,
  },
  message: {
    marginTop: 10,
    fontSize: 16,
    color: colors.light.PINK_700,
  },
  categorySection: {
    marginBottom: 16,
    backgroundColor: '#fff',
    borderRadius: 12,
    marginHorizontal: 16,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 3,
  },
  categoryHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 16,
    borderBottomWidth: 1,
    borderBottomColor: colors.light.GRAY_100,
    backgroundColor: colors.light.PINK_50,
    borderTopLeftRadius: 12,
    borderTopRightRadius: 12,
  },
  categoryTitle: {
    fontSize: 17,
    fontWeight: '600',
    color: colors.light.PINK_700,
    letterSpacing: -0.3,
  },
  postCount: {
    fontSize: 13,
    color: colors.light.PINK_500,
    backgroundColor: '#fff',
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 12,
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: colors.light.PINK_200,
  },
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  emptyMessage: {
    fontSize: 16,
    color: colors.light.GRAY_500,
  },
  scrollTopButton: {
    position: 'absolute',
    right: 20,
    bottom: 20,
    zIndex: 1000,
  },
  scrollTopButtonInner: {
    alignItems: 'center',
  },
  scrollTopText: {
    color: colors.light.PINK_700,
    fontSize: 12,
    marginTop: -5,
  },
  filterContainer: {
    padding: 16,
    backgroundColor: 'white',
    borderBottomWidth: 1,
    borderBottomColor: colors.light.GRAY_200,
  },
  categoryScroll: {
    flexGrow: 0,
  },
  typeButton: {
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 20,
    backgroundColor: colors.light.GRAY_50,
    marginRight: 8,
    borderWidth: 1,
    borderColor: colors.light.GRAY_200,
  },
  typeButtonActive: {
    backgroundColor: colors.light.PINK_700,
    borderColor: colors.light.PINK_700,
  },
  typeButtonText: {
    fontSize: 14,
    color: colors.light.GRAY_600,
  },
  typeButtonTextActive: {
    color: 'white',
    fontWeight: '500',
  },
});

export default LikedPostsScreen;

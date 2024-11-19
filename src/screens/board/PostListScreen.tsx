import React, { useState, useRef, useCallback, useMemo } from 'react';
import { 
  View, 
  TextInput, 
  ActivityIndicator, 
  StyleSheet, 
  FlatList,
  RefreshControl,
  TouchableOpacity,
  Animated,
  Text,
} from 'react-native';
import { StackScreenProps } from '@react-navigation/stack';
import { BoardStackParamList } from '@/navigations/stack/BoardStackNavigator';
import PostItem from '@/components/board/PostItem';
import { useInfiniteScroll } from '@/hooks/useInfiniteScroll';
import { colors } from '@/constants';
import Icon from 'react-native-vector-icons/Ionicons';
import { Post } from '@/types/post';
import { InfinitePostsResponse } from '@/hooks/useInfiniteScroll';

type Props = StackScreenProps<BoardStackParamList, 'PostList'>;

function PostListScreen({ route, navigation }: Props) {
  const { category } = route.params;
  const [searchKeyword, setSearchKeyword] = useState('');
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [showScrollTop, setShowScrollTop] = useState(false);
  const [sortBy, setSortBy] = useState<'latest' | 'views'>('latest');
  const [showSearch, setShowSearch] = useState(false);
  const searchAnimation = useRef(new Animated.Value(0)).current;
  const { 
    data, 
    fetchNextPage, 
    hasNextPage, 
    isFetchingNextPage,
    refetch 
  } = useInfiniteScroll(category, searchKeyword);

  const flatListRef = useRef<FlatList>(null);

  const handleRefresh = async () => {
    setIsRefreshing(true);
    await refetch();
    setIsRefreshing(false);
  };

  const handleEndReached = () => {
    if (hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  };

  const handleScroll = (event: any) => {
    const offsetY = event.nativeEvent.contentOffset.y;
    setShowScrollTop(offsetY > 150);
  };

  const scrollToTop = () => {
    flatListRef.current?.scrollToOffset({ offset: 0, animated: true });
  };

  const keyExtractor = useCallback((item: Post) => item.id.toString(), []);
  
  const renderItem = useCallback(({ item }: { item: Post }) => (
    <PostItem
      title={item.title}
      partOfContent={item.partOfContent}
      viewCnt={item.viewCnt}
      likeCnt={item.likeCnt || 0}
      createDate={item.createDate}
      nickName={item.nickName}
      onPress={() => navigation.navigate('PostDetail', { id: item.id.toString(), category })}
    />
  ), [navigation, category]);

  const getItemLayout = useCallback(
    (_: ArrayLike<Post> | null | undefined, index: number) => ({
      length: 85,
      offset: 85 * index,
      index,
    }), 
    []
  );

  const posts = data?.pages.flatMap(page => page.posts) ?? [];

  const sortedPosts = useMemo(() => {
    if (!posts) return [];
    return [...posts].sort((a, b) => {
      if (sortBy === 'latest') {
        return new Date(b.createDate).getTime() - new Date(a.createDate).getTime();
      } else {
        return b.viewCnt - a.viewCnt;
      }
    });
  }, [posts, sortBy]);

  const toggleSearch = () => {
    if (showSearch) {
      Animated.spring(searchAnimation, {
        toValue: 0,
        useNativeDriver: false,
        friction: 8,
        tension: 40,
      }).start(() => {
        setShowSearch(false);
        setSearchKeyword('');
        refetch();
      });
    } else {
      setShowSearch(true);
      Animated.spring(searchAnimation, {
        toValue: 1,
        useNativeDriver: false,
        friction: 8,
        tension: 40,
      }).start();
    }
  };

  const searchWidth = searchAnimation.interpolate({
    inputRange: [0, 1],
    outputRange: ['0%', '75%']
  });

  const searchOpacity = searchAnimation.interpolate({
    inputRange: [0, 1],
    outputRange: [0, 1],
  });

  return (
    <View style={styles.container}>
      <View style={styles.headerContainer}>
        <View style={styles.sortButtons}>
          <TouchableOpacity 
            style={[styles.sortButton, sortBy === 'latest' && styles.sortButtonActive]}
            onPress={() => setSortBy('latest')}
          >
            <Text style={[styles.sortButtonText, sortBy === 'latest' && styles.sortButtonTextActive]}>
              최신글
            </Text>
          </TouchableOpacity>
          <TouchableOpacity 
            style={[styles.sortButton, sortBy === 'views' && styles.sortButtonActive]}
            onPress={() => setSortBy('views')}
          >
            <Text style={[styles.sortButtonText, sortBy === 'views' && styles.sortButtonTextActive]}>
              인기글
            </Text>
          </TouchableOpacity>
        </View>
        <View style={styles.searchContainer}>
          <Animated.View style={[
            styles.searchInputWrapper,
            {
              width: searchWidth,
              opacity: searchAnimation
            }
          ]}>
            {showSearch && (
              <TextInput
                style={styles.searchInput}
                placeholder="검색어를 입력하세요"
                value={searchKeyword}
                onChangeText={setSearchKeyword}
                onSubmitEditing={() => refetch()}
                returnKeyType="search"
                clearButtonMode="while-editing"
                autoFocus
              />
            )}
          </Animated.View>
          <TouchableOpacity onPress={toggleSearch}>
            <Icon 
              name={showSearch ? "close-outline" : "search-outline"} 
              size={24} 
              color={colors.light.GRAY_500} 
            />
          </TouchableOpacity>
        </View>
      </View>
      <View style={styles.listContainer}>
        <FlatList
          ref={flatListRef}
          data={sortedPosts}
          renderItem={renderItem}
          keyExtractor={keyExtractor}
          onEndReached={handleEndReached}
          onEndReachedThreshold={0.3}
          onScroll={handleScroll}
          scrollEventThrottle={16}
          refreshControl={
            <RefreshControl
              refreshing={isRefreshing}
              onRefresh={handleRefresh}
              colors={[colors.light.PINK_700]}
              tintColor={colors.light.PINK_700}
            />
          }
          ListFooterComponent={isFetchingNextPage ? (
            <View style={styles.loadingFooter}>
              <ActivityIndicator color={colors.light.PINK_700} />
            </View>
          ) : null}
          removeClippedSubviews={true}
          maxToRenderPerBatch={10}
          windowSize={5}
          initialNumToRender={10}
          getItemLayout={getItemLayout}
          contentContainerStyle={styles.listContent}
        />
        {showScrollTop && (
          <Animated.View style={styles.scrollTopButton}>
            <TouchableOpacity
              onPress={scrollToTop}
              style={styles.scrollTopButtonInner}
            >
              <Icon name="arrow-up-circle" size={50} color={colors.light.PINK_700} />
            </TouchableOpacity>
          </Animated.View>
        )}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  listContainer: {
    flex: 1,
    marginTop: 50,
  },
  headerContainer: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    backgroundColor: 'white',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
    paddingVertical: 8,
    zIndex: 1,
    elevation: 3,
  },
  sortButtons: {
    flexDirection: 'row',
    gap: 8,
    marginRight: 16,
  },
  searchContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    flex: 1,
    justifyContent: 'flex-end',
  },
  searchInputWrapper: {
    backgroundColor: colors.light.GRAY_50,
    borderRadius: 20,
    height: 36,
    borderWidth: 1,
    borderColor: colors.light.GRAY_200,
    paddingHorizontal: 12,
    overflow: 'hidden',
    position: 'absolute',
    right: 40,
  },
  searchInput: {
    flex: 1,
    height: '100%',
    fontSize: 14,
    color: colors.light.GRAY_900,
    padding: 0,
  },
  loadingFooter: {
    paddingVertical: 20,
    alignItems: 'center',

  },
  scrollTopButton: {
    position: 'absolute',
    right: 20,
    bottom: 50,
    alignItems: 'center',
  },
  scrollTopButtonInner: {
    alignItems: 'center',
    justifyContent: 'center',
  },
  listContent: {
    paddingVertical: 8,
  },
  sortButton: {
    paddingVertical: 6,
    paddingHorizontal: 12,
    borderRadius: 15,
    backgroundColor: colors.light.GRAY_50,
    borderWidth: 1,
    borderColor: colors.light.GRAY_200,
  },
  sortButtonActive: {
    backgroundColor: colors.light.PINK_700,
    borderColor: colors.light.PINK_700,
  },
  sortButtonText: {
    fontSize: 13,
    color: colors.light.GRAY_500,
  },
  sortButtonTextActive: {
    color: 'white',
    fontWeight: '500',
  },
});

export default PostListScreen;

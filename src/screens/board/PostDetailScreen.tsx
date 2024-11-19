import React, { useState, useEffect, useMemo } from 'react';
import { 
  View, 
  Text, 
  StyleSheet, 
  ActivityIndicator, 
  TouchableOpacity,
  ScrollView,
  Image,
  Dimensions,
  KeyboardAvoidingView,
  Platform,
  RefreshControl,
  TextInput,
} from 'react-native';
import { StackScreenProps } from '@react-navigation/stack';
import { BoardStackParamList } from '@/navigations/stack/BoardStackNavigator';
import { useQuery } from '@tanstack/react-query';
import { getPost, getPostImage, getMyPosts, toggleLikePost, getLikedPosts } from '@/api/post';
import Icon from 'react-native-vector-icons/Ionicons';
import CommentInput from '@/components/board/CommentInput';
import CommentList from '@/components/board/CommentList';
import { colors } from '@/constants/colors';
import { useQueryClient } from '@tanstack/react-query';
import { createComment } from '@/api/comment';
import { Alert } from 'react-native';
import useAuth from '@/hooks/queries/useAuth';
import useModal from '@/hooks/useModal';
import { CompoundOption } from '@/components/common/CompoundOption';
import { updatePost, deletePost } from '@/api/post';
import { LikeToggle } from '@/components/common/LikeToggle';
type Props = StackScreenProps<BoardStackParamList, 'PostDetail'>;

function PostDetailScreen({ route, navigation }: Props) {
  const { getProfileQuery } = useAuth();
  const { data: userProfile } = getProfileQuery;
  const optionModal = useModal();
  const deleteConfirmModal = useModal();
  
  const { data: myPosts } = useQuery({
    queryKey: ['myPosts'],
    queryFn: getMyPosts,
    enabled: !!userProfile,
  });

  const { data: post, isLoading } = useQuery({
    queryKey: ['post', route.params.category, route.params.id],
    queryFn: () => getPost(route.params.category, Number(route.params.id))
  });

  const { data: likedPosts } = useQuery({
    queryKey: ['likedPosts'],
    queryFn: getLikedPosts
  });

  const isPostLiked = useMemo(() => {
    if (!likedPosts || !post) return false;
    return likedPosts.some(likedPost => likedPost.id === post.id);
  }, [likedPosts, post]);

  const { data: images } = useQuery({
    queryKey: ['postImages', route.params.category, route.params.id, post?.fileList],
    queryFn: async () => {
      if (!post?.fileList || post.fileList.length === 0) return [];
      
      const imageUrls = await Promise.all(
        post.fileList.map(uuid => 
          getPostImage(
            uuid,
            route.params.category,
            Number(route.params.id)
          )
        )
      );
      return imageUrls;
    },
    enabled: !!post?.fileList && post.fileList.length > 0,
  });

  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [localLikeCount, setLocalLikeCount] = useState<number>(0);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const queryClient = useQueryClient();
  const [isEditing, setIsEditing] = useState(false);
  const [editTitle, setEditTitle] = useState('');
  const [editContent, setEditContent] = useState('');

  useEffect(() => {
    if (post?.likeCnt !== undefined) {
      setLocalLikeCount(post.likeCnt);
    }
  }, [post?.likeCnt]);

  const handleLikeToggle = async (isLiked: boolean, likeCount: number) => {
    if (typeof likeCount === 'number') {
      setLocalLikeCount(likeCount);
      
      // 캐시된 게시글 데이터도 업데이트
      queryClient.setQueryData(
        ['post', route.params.category, route.params.id],
        (oldData: any) => {
          if (!oldData) return oldData;
          return {
            ...oldData,
            likeCnt: likeCount
          };
        }
      );

      // 좋아요 목록도 업데이트
      await queryClient.invalidateQueries({ 
        queryKey: ['likedPosts']
      });
    }
  };

  const handleRefresh = async () => {
    setIsRefreshing(true);
    await Promise.all([
      queryClient.invalidateQueries({ 
        queryKey: ['post', route.params.category, route.params.id] 
      }),
      queryClient.invalidateQueries({ 
        queryKey: ['comments', route.params.category, route.params.id] 
      }),
      queryClient.invalidateQueries({ 
        queryKey: ['postImages', route.params.category, route.params.id] 
      })
    ]);
    setIsRefreshing(false);
  };

  const handleUpdate = async () => {
    if (!editTitle.trim() || !editContent.trim()) {
      Alert.alert('알림', '제목과 내용을 모두 입력해주세요.');
      return;
    }

    try {
      await updatePost(
        route.params.category,
        Number(route.params.id),
        editTitle,
        editContent
      );
      setIsEditing(false);
      queryClient.invalidateQueries({ 
        queryKey: ['post', route.params.category, route.params.id] 
      });
    } catch (error) {
      Alert.alert('오류', '게시글 수정에 실패했습니다.');
    }
  };

  const handleDelete = async () => {
    try {
      await deletePost(route.params.category, Number(route.params.id));
      navigation.goBack();
    } catch (error) {
      Alert.alert('오류', '게시글 삭제에 실패했습니다.');
    }
  };

  // 현재 게시글이 내가 쓴 글인지 확인
  const isMyPost = useMemo(() => {
    if (!myPosts || !post) return false;
    return myPosts.some(myPost => myPost.id === post.id);
  }, [myPosts, post]);

  if (isLoading) {
    return <ActivityIndicator style={styles.loader} color="#c62917" />;
  }

  return (
    <View style={styles.container}>
      {isEditing ? (
        <View style={styles.editContainer}>
          <TextInput
            style={styles.editTitleInput}
            value={editTitle}
            onChangeText={setEditTitle}
            placeholder="제목"
          />
          <TextInput
            style={styles.editContentInput}
            value={editContent}
            onChangeText={setEditContent}
            multiline
            placeholder="내용"
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
        <ScrollView 
          style={styles.scrollView}
          refreshControl={
            <RefreshControl
              refreshing={isRefreshing}
              onRefresh={handleRefresh}
              colors={[colors.light.PINK_700]}
              tintColor={colors.light.PINK_700}
            />
          }
        >
          <View style={styles.postContent}>
            <View style={styles.header}>
              <View style={styles.titleContainer}>
                <Text style={styles.title}>{post?.title}</Text>
                {post?.isEdited && (
                  <Text style={styles.editedMark}>(수정됨)</Text>
                )}
              </View>
              {isMyPost && (
                <TouchableOpacity 
                  onPress={optionModal.show}
                  style={styles.moreButton}
                >
                  <Icon 
                    name="ellipsis-vertical" 
                    size={24} 
                    color={colors.light.GRAY_600} 
                  />
                </TouchableOpacity>
              )}
            </View>
            <Text style={styles.content}>{post?.content}</Text>
          </View>
          {images && images.length > 0 && (
            <View style={styles.imageContainer}>
              <ScrollView 
                horizontal 
                pagingEnabled
                showsHorizontalScrollIndicator={false}
                onScroll={(e) => {
                  const offset = e.nativeEvent.contentOffset.x;
                  const currentIndex = Math.round(offset / Dimensions.get('window').width);
                  setCurrentImageIndex(currentIndex);
                }}
                scrollEventThrottle={16}
              >
                {images.map((uri, index) => (
                  <View key={index} style={styles.imageWrapper}>
                    <View style={styles.imageCountBadge}>
                      <Text style={styles.imageCountText}>
                        {`${index + 1}/${images.length}`}
                      </Text>
                    </View>
                    <Image 
                      source={{ uri: uri as string }} 
                      style={styles.image}
                      resizeMode="contain"
                    />
                  </View>
                ))}
              </ScrollView>
            </View>
          )}
          <View style={styles.footer}>
            <View style={styles.statItem}>
              <Icon name="eye-outline" size={18} color={colors.light.GRAY_600} />
              <Text style={[styles.statText, { color: colors.light.GRAY_600 }]}>
                {post?.viewCnt}
              </Text>
            </View>
            <View style={styles.statItem}>
              <LikeToggle
                itemId={Number(route.params.id)}
                category={route.params.category}
                initialLikeCount={post?.likeCnt || 0}
                queryKey={['post', route.params.category, route.params.id]}
                toggleLikeFn={toggleLikePost}
                onLikeToggle={handleLikeToggle}
                initialIsLiked={isPostLiked}
              />
            </View>
          </View>
          <View style={styles.commentSection}>
            <CommentList
              category={route.params.category}
              postId={Number(route.params.id)}
            />
          </View>
        </ScrollView>
      )}
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        keyboardVerticalOffset={Platform.OS === 'ios' ? 90 : 0}
        style={styles.inputContainer}
      >
        <CommentInput 
          category={route.params.category}
          postId={Number(route.params.id)}
        />
      </KeyboardAvoidingView>
      <CompoundOption isVisible={optionModal.isVisible} hideOption={optionModal.hide}>
        <CompoundOption.Background>
          <CompoundOption.Container>
            <CompoundOption.Button onPress={() => {
              setEditTitle(post?.title || '');
              setEditContent(post?.content || '');
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
              <Text style={styles.deleteConfirmTitle}>게시글 삭제</Text>
              <Text style={styles.deleteConfirmMessage}>
                정말 이 게시글을 삭제하시겠습니까?
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
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.light.WHITE,
  },
  scrollView: {
    flex: 1,
    marginBottom: 60,
  },
  postContent: {
    padding: 20,
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    color: colors.light.BLACK,
    lineHeight: 28,
  },
  content: {
    fontSize: 16,
    lineHeight: 24,
    color: colors.light.BLACK,
  },
  imageContainer: {
    width: '100%',
    paddingHorizontal: 16,
    marginVertical: 10,
  },
  imageWrapper: {
    width: Dimensions.get('window').width - 32,
    height: Dimensions.get('window').width - 32,
    position: 'relative',
    borderRadius: 12,
    overflow: 'hidden',
  },
  image: {
    width: '100%',
    height: '100%',
    resizeMode: 'contain',
    backgroundColor: colors.light.GRAY_50,
  },
  imageCountBadge: {
    position: 'absolute',
    top: 16,
    left: 16,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 20,
    zIndex: 1,
  },
  imageCountText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: '600',
  },
  footer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 8,
    paddingHorizontal: 16,
    borderTopWidth: 1,
    borderTopColor: colors.light.GRAY_200,
  },
  stats: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  statItem: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.light.GRAY_50,
    paddingVertical: 4,
    paddingHorizontal: 8,
    borderRadius: 12,
    height: 28,
    width: 65,
    justifyContent: 'center',
  },
  statText: {
    fontSize: 13,
    marginLeft: 4,
    fontWeight: '500',
  },
  commentSection: {
    minHeight: 300,
    backgroundColor: colors.light.WHITE,
    borderTopWidth: 1,
    borderTopColor: colors.light.GRAY_200,
    zIndex: 1,
  },
  loader: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  keyboardAvoid: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
  },
  inputContainer: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    backgroundColor: '#fff',
    borderTopWidth: 1,
    borderTopColor: '#e0e0e0',
    zIndex: 1000,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
  },
  titleContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
  },
  editedMark: {
    fontSize: 13,
    color: colors.light.GRAY_600,
    marginLeft: 8,
    lineHeight: 24,
  },
  moreButton: {
    padding: 8,
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
  editContainer: {
    padding: 16,
  },
  editTitleInput: {
    borderWidth: 1,
    borderColor: colors.light.GRAY_200,
    borderRadius: 8,
    padding: 12,
    marginBottom: 8,
    backgroundColor: colors.light.GRAY_50,
    color: colors.light.GRAY_900,
    fontSize: 16,
    fontWeight: 'bold',
  },
  editContentInput: {
    borderWidth: 1,
    borderColor: colors.light.GRAY_200,
    borderRadius: 8,
    padding: 12,
    marginBottom: 8,
    minHeight: 200,
    backgroundColor: colors.light.GRAY_50,
    color: colors.light.GRAY_900,
    fontSize: 14,
    textAlignVertical: 'top',
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
});

export default PostDetailScreen;


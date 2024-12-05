import React, { useCallback } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  ActivityIndicator,
  Image,
  TouchableOpacity,
  Alert,
  ImageBackground,
  Dimensions,
} from 'react-native';
import { useInfiniteQuery, InfiniteData } from '@tanstack/react-query';
import { getVets } from '@/api/vet';
import { Vet } from '@/types/vet';
import { colors } from '@/constants';
import Icon from 'react-native-vector-icons/Ionicons';
import { createChatRoom } from '@/api/chat';
import { useNavigation, CompositeNavigationProp } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { BoardStackParamList } from '@/navigations/stack/BoardStackNavigator';
import { boardNavigations, mainNavigations } from '@/constants/navigations';
import { DrawerNavigationProp } from '@react-navigation/drawer';
import { MainDrawerParamList } from '@/navigations/drawer/MainDrawerNavigator';
import LinearGradient from 'react-native-linear-gradient';

const PAGE_SIZE = 20;
const SCREEN_WIDTH = Dimensions.get('window').width;

interface PageData {
  data: Vet[];
  nextPage: number | undefined;
}

type VetListScreenNavigationProp = CompositeNavigationProp<
  DrawerNavigationProp<MainDrawerParamList>,
  StackNavigationProp<BoardStackParamList>
>;

function VetListScreen() {
  const navigation = useNavigation<VetListScreenNavigationProp>();

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isLoading,
    error,
  } = useInfiniteQuery<PageData, Error, InfiniteData<PageData>>({
    queryKey: ['vets'],
    queryFn: async ({ pageParam }) => {
      const page = pageParam as number;
      console.log('Fetching page:', page);
      const response = await getVets(page, PAGE_SIZE);
      console.log('Response:', response);
      const vetArray = response as unknown as Vet[];
      return {
        data: vetArray,
        nextPage: vetArray.length === PAGE_SIZE ? page + 1 : undefined,
      };
    },
    getNextPageParam: (lastPage) => lastPage.nextPage,
    initialPageParam: 1,
  });

  const loadMore = useCallback(() => {
    if (hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [hasNextPage, isFetchingNextPage, fetchNextPage]);

  const handleChatRequest = async (vet: Vet) => {
    try {
      const chatRoom = await createChatRoom(
        vet.memberId,
        `${vet.nickName}님과의 대화`
      );
      
      navigation.navigate(mainNavigations.BOARD, {
        screen: boardNavigations.CHAT_ROOM,
        params: {
          roomId: chatRoom.roomId,
          otherUserName: vet.nickName
        }
      });
    } catch (error) {
      Alert.alert('오류', '채팅방 생성에 실패했습니다.');
    }
  };

  const renderHeader = () => (
    <View style={styles.headerContainer}>
      <ImageBackground
        source={require('@/assets/images/vet-consultation.jpg')}
        style={styles.bannerImage}
        resizeMode="cover"
      >
        <LinearGradient
          colors={['rgba(0,0,0,0.3)', 'rgba(0,0,0,0.5)']}
          style={styles.gradient}
        >
          <Text style={styles.bannerTitle}>전국의 수의사와 대화해보세요</Text>
          <Text style={styles.bannerSubtitle}>
            반려동물의 건강에 대해 전문가와 상담하세요
          </Text>
        </LinearGradient>
      </ImageBackground>
      <View style={styles.statsContainer}>
        <View style={styles.statItem}>
          <Icon name="people" size={24} color={colors.light.PINK_500} />
          <Text style={styles.statNumber}>{data?.pages[0]?.data.length ?? 0}명</Text>
          <Text style={styles.statLabel}>수의사</Text>
        </View>
        <View style={styles.statDivider} />
        <View style={styles.statItem}>
          <Icon name="chatbubbles" size={24} color={colors.light.PINK_500} />
          <Text style={styles.statNumber}>24시간</Text>
          <Text style={styles.statLabel}>상담가능</Text>
        </View>
      </View>
    </View>
  );

  const renderVetItem = ({ item }: { item: Vet }) => (
    <TouchableOpacity 
      style={styles.vetCard}
      onPress={() => handleChatRequest(item)}
      activeOpacity={0.7}
    >
      <View style={styles.profileContainer}>
        <Image 
          source={require('@/assets/user-default.png')}
          style={styles.profileImage}
        />
      </View>
      <View style={styles.vetInfo}>
        <Text style={styles.vetName}>{item.nickName}</Text>
        <Text style={styles.hospitalName}>{item.hospitalName}</Text>
        <Text style={styles.hospitalAddr}>{item.hospitalAddr}</Text>
      </View>
      <TouchableOpacity 
        style={styles.chatButton}
        onPress={() => handleChatRequest(item)}
      >
        <Icon name="chatbubble-outline" size={20} color={colors.light.WHITE} />
      </TouchableOpacity>
    </TouchableOpacity>
  );

  if (isLoading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color={colors.light.PINK_500} />
      </View>
    );
  }

  if (error) {
    return (
      <View style={styles.errorContainer}>
        <Text style={styles.errorText}>데이터를 불러오는데 실패했습니다.</Text>
      </View>
    );
  }

  const allVets = data?.pages.flatMap((page: PageData) => page.data) ?? [];

  return (
    <View style={styles.container}>
      <FlatList<Vet>
        data={allVets}
        renderItem={renderVetItem}
        keyExtractor={(item) => item.id.toString()}
        onEndReached={loadMore}
        onEndReachedThreshold={0.3}
        ListHeaderComponent={renderHeader}
        ListEmptyComponent={
          <View style={styles.emptyContainer}>
            <Text style={styles.emptyText}>등록된 수의사가 없습니다.</Text>
          </View>
        }
        ListFooterComponent={
          isFetchingNextPage ? (
            <ActivityIndicator 
              style={styles.footer} 
              color={colors.light.PINK_500} 
            />
          ) : null
        }
        contentContainerStyle={styles.listContainer}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.light.GRAY_50,
  },
  headerContainer: {
    marginBottom: 16,
  },
  bannerImage: {
    width: SCREEN_WIDTH,
    height: 200,
  },
  gradient: {
    flex: 1,
    justifyContent: 'flex-end',
    padding: 20,
  },
  bannerTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: colors.light.WHITE,
    marginBottom: 8,
  },
  bannerSubtitle: {
    fontSize: 16,
    color: colors.light.WHITE,
    opacity: 0.9,
  },
  statsContainer: {
    flexDirection: 'row',
    backgroundColor: colors.light.WHITE,
    borderRadius: 12,
    margin: 16,
    padding: 16,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 3,
  },
  statItem: {
    flex: 1,
    alignItems: 'center',
  },
  statDivider: {
    width: 1,
    backgroundColor: colors.light.GRAY_200,
    marginHorizontal: 16,
  },
  statNumber: {
    fontSize: 18,
    fontWeight: 'bold',
    color: colors.light.GRAY_900,
    marginTop: 8,
  },
  statLabel: {
    fontSize: 14,
    color: colors.light.GRAY_500,
    marginTop: 4,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  errorContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  errorText: {
    fontSize: 16,
    color: colors.light.PINK_700,
  },
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: 32,
  },
  emptyText: {
    fontSize: 16,
    color: colors.light.GRAY_500,
  },
  listContainer: {
    flexGrow: 1,
  },
  vetCard: {
    backgroundColor: colors.light.WHITE,
    borderRadius: 12,
    padding: 16,
    marginHorizontal: 16,
    marginBottom: 12,
    flexDirection: 'row',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 3,
  },
  profileContainer: {
    width: 50,
    height: 50,
    borderRadius: 25,
    overflow: 'hidden',
    marginRight: 12,
    backgroundColor: colors.light.GRAY_100,
  },
  profileImage: {
    width: '100%',
    height: '100%',
    resizeMode: 'cover',
  },
  vetInfo: {
    flex: 1,
  },
  vetName: {
    fontSize: 16,
    fontWeight: 'bold',
    color: colors.light.GRAY_900,
    marginBottom: 4,
  },
  hospitalName: {
    fontSize: 14,
    color: colors.light.GRAY_700,
    marginBottom: 4,
  },
  hospitalAddr: {
    fontSize: 13,
    color: colors.light.GRAY_500,
  },
  chatButton: {
    backgroundColor: colors.light.PINK_500,
    width: 40,
    height: 40,
    borderRadius: 20,
    justifyContent: 'center',
    alignItems: 'center',
    marginLeft: 12,
  },
  footer: {
    paddingVertical: 16,
  },
});

export default VetListScreen; 
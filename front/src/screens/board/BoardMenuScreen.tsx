import React from 'react';
import { View, TouchableOpacity, Text, StyleSheet, Image, ScrollView } from 'react-native';
import { StackNavigationProp } from '@react-navigation/stack';
import { BoardStackParamList } from '@/navigations/stack/BoardStackNavigator';
import { useNavigation } from '@react-navigation/native';
import { boardNavigations } from '@/constants';
import Icon from 'react-native-vector-icons/Ionicons';
import { colors } from '@/constants/colors';

type NavigationProp = StackNavigationProp<BoardStackParamList, typeof boardNavigations.BOARD_MENU>;

function BoardMenuScreen() {
  const navigation = useNavigation<NavigationProp>();

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Image 
          source={require('@/assets/images/board-banner.png')}
          style={styles.banner}
          resizeMode="cover"
        />
        <View style={styles.headerContent}>
          <Text style={styles.title}>커뮤니티</Text>
          <Text style={styles.subtitle}>
            반려동물 보호자들과{'\n'}
            다양한 이야기를 나눠보세요
          </Text>
        </View>
      </View>

      <View style={styles.content}>
        <View style={styles.menuGrid}>
          <TouchableOpacity 
            style={styles.menuCard}
            onPress={() => navigation.navigate(boardNavigations.POST_LIST, { category: 'community_board' })}
          >
            <View style={[styles.iconContainer, { backgroundColor: '#E8F5E9' }]}>
              <Icon name="chatbubbles-outline" size={24} color="#2E7D32" />
            </View>
            <Text style={styles.menuTitle}>자유게시판</Text>
            <Text style={styles.menuDescription}>일상 이야기를 공유해요</Text>
          </TouchableOpacity>

          <TouchableOpacity 
            style={styles.menuCard}
            onPress={() => navigation.navigate(boardNavigations.POST_LIST, { category: 'qna_board' })}
          >
            <View style={[styles.iconContainer, { backgroundColor: '#E3F2FD' }]}>
              <Icon name="help-circle-outline" size={24} color="#1565C0" />
            </View>
            <Text style={styles.menuTitle}>질문게시판</Text>
            <Text style={styles.menuDescription}>궁금한 것을 물어보세요</Text>
          </TouchableOpacity>

          <TouchableOpacity 
            style={styles.menuCard}
            onPress={() => navigation.navigate(boardNavigations.POST_LIST, { category: 'vet_board' })}
          >
            <View style={[styles.iconContainer, { backgroundColor: '#FCE4EC' }]}>
              <Icon name="medical-outline" size={24} color="#C2185B" />
            </View>
            <Text style={styles.menuTitle}>수의사게시판</Text>
            <Text style={styles.menuDescription}>전문가의 답변을 들어보세요</Text>
          </TouchableOpacity>

          <TouchableOpacity 
            style={styles.menuCard}
            onPress={() => navigation.navigate(boardNavigations.MY_POSTS)}
          >
            <View style={[styles.iconContainer, { backgroundColor: '#FFF3E0' }]}>
              <Icon name="person-outline" size={24} color="#E65100" />
            </View>
            <Text style={styles.menuTitle}>내가 쓴 글</Text>
            <Text style={styles.menuDescription}>내가 작성한 글 모아보기</Text>
          </TouchableOpacity>
        </View>

        <TouchableOpacity 
          style={styles.likedPostsButton}
          onPress={() => navigation.navigate(boardNavigations.LIKED_POSTS)}
        >
          <Icon name="heart-outline" size={24} color={colors.light.PINK_700} />
          <View style={styles.likedPostsContent}>
            <Text style={styles.likedPostsTitle}>공감한 글</Text>
            <Text style={styles.likedPostsDescription}>관심있는 글을 모아봤어요</Text>
          </View>
          <Icon name="chevron-forward" size={24} color={colors.light.GRAY_400} />
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.light.WHITE,
  },
  header: {
    height: 240,
  },
  banner: {
    width: '100%',
    height: '100%',
  },
  headerContent: {
    position: 'absolute',
    bottom: 24,
    left: 24,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: colors.light.WHITE,
    marginBottom: 8,
    textShadowColor: 'rgba(0, 0, 0, 0.3)',
    textShadowOffset: {width: 0, height: 1},
    textShadowRadius: 4,
  },
  subtitle: {
    fontSize: 16,
    color: colors.light.WHITE,
    lineHeight: 22,
    textShadowColor: 'rgba(0, 0, 0, 0.3)',
    textShadowOffset: {width: 0, height: 1},
    textShadowRadius: 4,
  },
  content: {
    padding: 24,
  },
  menuGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
    gap: 16,
  },
  menuCard: {
    width: '47%',
    backgroundColor: colors.light.WHITE,
    borderRadius: 16,
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
  iconContainer: {
    width: 48,
    height: 48,
    borderRadius: 12,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 12,
  },
  menuTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: colors.light.GRAY_900,
    marginBottom: 4,
  },
  menuDescription: {
    fontSize: 13,
    color: colors.light.GRAY_600,
  },
  likedPostsButton: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.light.WHITE,
    borderRadius: 16,
    padding: 16,
    marginTop: 24,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 3,
  },
  likedPostsContent: {
    flex: 1,
    marginLeft: 12,
  },
  likedPostsTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: colors.light.GRAY_900,
    marginBottom: 2,
  },
  likedPostsDescription: {
    fontSize: 13,
    color: colors.light.GRAY_600,
  },
});

export default BoardMenuScreen;

import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import BoardMenuScreen from '@/screens/board/BoardMenuScreen';
import PostListScreen from '@/screens/board/PostListScreen';
import PostDetailScreen from '@/screens/board/PostDetailScreen';
import PostCreateScreen from '@/screens/board/PostCreateScreen';
import MyPostsScreen from '@/screens/board/MyPostsScreen';
import LikedPostsScreen from '@/screens/board/LikedPostsScreen';
import { boardNavigations, colors } from '@/constants';
import { TouchableOpacity } from 'react-native-gesture-handler';
import { Image } from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';
import Ionicons from 'react-native-vector-icons/Ionicons';

export type BoardStackParamList = {
  [boardNavigations.BOARD_MENU]: undefined;
  [boardNavigations.POST_LIST]: { category: string };
  [boardNavigations.POST_DETAIL]: { id: string; category: string };
  [boardNavigations.POST_CREATE]: { category: string };
  [boardNavigations.MY_POSTS]: undefined;
  [boardNavigations.LIKED_POSTS]: undefined;
};

const Stack = createStackNavigator<BoardStackParamList>();

function BoardStackNavigator() {
  return (
    <Stack.Navigator
      screenOptions={({ navigation }) => ({
        headerLeft: () => (
          <TouchableOpacity onPress={() => navigation.openDrawer()} style={{ marginLeft: 10 }}>
            <Ionicons name="menu" size={25} color={colors.light.GRAY_700} />
          </TouchableOpacity>
        ),
        headerTitleAlign: 'center',
        headerTitleStyle: {
          fontSize: 16,
          fontWeight: 'bold',
        },
      })}
    >
      <Stack.Screen
        name={boardNavigations.BOARD_MENU}
        component={BoardMenuScreen}
        options={{ title: '게시판' }}
      />
      <Stack.Screen
        name={boardNavigations.POST_LIST}
        component={PostListScreen}
        options={({ route, navigation }) => ({
          title: route.params.category === 'community_board' ? '자유 게시판' :
                 route.params.category === 'qna_board' ? '질문게시판' :
                 route.params.category === 'vet_board' ? '수의사게시판' : '게시글 목록',
          headerRight: () => (
            <TouchableOpacity onPress={() => navigation.navigate(boardNavigations.POST_CREATE, { category: route.params.category })}>
              <Icon name="create-outline" size={24} color={colors.light.GRAY_700} style={{ marginRight: 15 }} />
            </TouchableOpacity>
          ),
        })}
      />
      <Stack.Screen
        name={boardNavigations.POST_DETAIL}
        component={PostDetailScreen}
        options={({ navigation }) => ({
          title: '게시글 상세',
          headerLeft: () => (
            <TouchableOpacity onPress={() => navigation.goBack()}>
              <Image 
                source={require('@/assets/images/back.png')} 
                style={{ width: 24, height: 24, marginLeft: 10 }} 
              />
            </TouchableOpacity>
          ),
        })}
      />
      <Stack.Screen
        name={boardNavigations.POST_CREATE}
        component={PostCreateScreen}
        options={({ navigation }) => ({
          title: '게시글 작성',
          headerLeft: () => (
            <TouchableOpacity onPress={() => navigation.goBack()}>
              <Image source={require('@/assets/images/back.png')} style={{ width: 24, height: 24, marginLeft: 10 }} />
            </TouchableOpacity>
          ),
        })}
      />
      <Stack.Screen
        name={boardNavigations.MY_POSTS}
        component={MyPostsScreen}
        options={{ title: '내가 쓴 글' }}
      />
      <Stack.Screen
        name={boardNavigations.LIKED_POSTS}
        component={LikedPostsScreen}
        options={{ title: '공감한 글' }}
      />
    </Stack.Navigator>
  );
}

export default BoardStackNavigator;

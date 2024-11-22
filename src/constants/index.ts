export * from './colors';
export * from './navigations';
export * from './keys';
export * from './numbers';
export * from './messages';

export const boardNavigations = {
    BOARD_MENU: 'BoardMenu',
    POST_LIST: 'PostList',
    POST_DETAIL: 'PostDetail',
    POST_CREATE: 'PostCreate',
    MY_POSTS: 'MyPosts',
    LIKED_POSTS: 'LikedPosts',
    CHAT_ROOM: 'ChatRoom'
}as const;

export const settingNavigations = {
    SETTING_HOME: 'SETTING_HOME',
    EDIT_PROFILE: 'EDIT_PROFILE',
    EDIT_PASSWORD: 'EDIT_PASSWORD',
    DELETE_ACCOUNT: 'DELETE_ACCOUNT',
  } as const;
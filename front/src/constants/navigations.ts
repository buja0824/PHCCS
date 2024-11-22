const mainNavigations = {
  HOME: 'Home',
  AI: 'AI',
  CALENDAR: 'Calendar',
  BOARD: 'Board',
  SETTING: 'Setting',
  PET: 'Pet',
} as const;

const authNavigations = {
  AUTH_HOME: 'AuthHome',
  LOGIN: 'Login',
  SIGNUP: 'Signup',
  USER_TYPE_SELECTION: 'UserTypeSelection',
} as const;

const mapNavigations = {
  MAP_HOME: 'MapHome',
} as const;

const settingNavigations = {
  SETTING_HOME: 'SettingHome',
  EDIT_PROFILE: 'EditProfile',
  EDIT_PASSWORD: 'EditPassword',
  DELETE_ACCOUNT: 'DeleteAccount',
} as const;

const petNavigations = {
  PET_HOME: 'PetHome',
  PET_ADD: 'PetAdd',
  PET_EDIT: 'PetEdit',
  PET_HEALTH: 'PetHealth',
  HEALTH_CHECKUP: 'HealthCheckup',
  HEALTH_CHECKUP_LIST: 'HealthCheckupList',
  VACCINATION: 'Vaccination',
  VACCINATION_LIST: 'VaccinationList',
  MEDICAL_HISTORY: 'MedicalHistory',
  MEDICAL_HISTORY_LIST: 'MedicalHistoryList',
} as const;

const aiNavigations = {
  AI_HOME: 'AiHome',
  AI_PET_TYPE: 'AiPetType',
  AI_SYMPTOM_TYPE: 'AiSymptomType',
  AI_CAMERA: 'AiCamera',
  AI_RESULT: 'AiResult',
} as const;

const boardNavigations = {
  BOARD_MENU: 'BoardMenu',
  POST_LIST: 'PostList',
  POST_DETAIL: 'PostDetail',
  POST_CREATE: 'PostCreate',
  MY_POSTS: 'MyPosts',
  LIKED_POSTS: 'LikedPosts',
  CHAT_ROOM: 'ChatRoom'
} as const;

export {authNavigations, mapNavigations, mainNavigations, settingNavigations, petNavigations, aiNavigations, boardNavigations};

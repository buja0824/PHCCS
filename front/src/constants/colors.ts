const common = {
  PINK_50: '#FFF1F3',
  PINK_200: '#FAE2E9',
  PINK_400: '#EC87A5',
  PINK_500: '#BF5C79',
  PINK_700: '#C63B64',
  RED_50: '#FEF2F2',
  RED_100: '#FEE2E2',
  RED_600: '#DC2626',
  RED_700: '#B91C1C',
  BLUE_400: '#B4E0FF',
  BLUE_500: '#3B82F6',
  BLUE_700: '#1D4ED8',
  GREEN_400: '#CCE6BA',
  YELLOW_400: '#FFE594',
  YELLOW_500: '#FACC15',
  PURPLE_400: '#C4C4E7',
  UNCHANGE_WHITE: '#FFF',
  UNCHANGE_BLACK: '#000',
  PINK_50_DARK: '#2C1F22',
  PINK_700_DARK: '#FF4B81',
  GREEN_50: '#F0FDF4',
  GREEN_100: '#DCFCE7',
  GREEN_600: '#16A34A',
  GREEN_700: '#15803D',
  RED_500: '#FF5F5F',
  RED_300: '#FFB4B4',
  YELLOW_50: '#FEFCE8',
  YELLOW_700: '#A16207',
  BLUE_50: '#E3F2FD',
  BLUE_600: '#1565C0',
  PINK_600: '#DB2777',
} as const;

const colors = {
  light: {
    PRIMARY: '#FF6B6B',
    WHITE: '#FFF',
    GRAY_100: '#F8F8F8',
    GRAY_200: '#E7E7E7',
    GRAY_300: '#D8D8D8',
    GRAY_500: '#8E8E8E',
    GRAY_700: '#575757',
    BLACK: '#161616',
    GRAY_50: '#F9FAFB',
    GRAY_400: '#9CA3AF',
    GRAY_600: '#4B5563',
    GRAY_900: '#111827',
    ...common,
    GREEN_50: common.GREEN_50,
    GREEN_700: common.GREEN_700,
    RED_50: common.RED_50,
    RED_700: common.RED_700,
    YELLOW_50: common.YELLOW_50,
    YELLOW_700: common.YELLOW_700,
    PINK_50: common.PINK_50,
    PINK_700: common.PINK_700,
    ERROR: '#DC2626',
    GREEN_500: '#22C55E',
  },
  dark: {
    PRIMARY: '#FF6B6B',
    WHITE: '#161616',
    GRAY_100: '#202124',
    GRAY_200: '#3C4043',
    GRAY_300: '#5e5e5e',
    GRAY_500: '#8E8E8E',
    GRAY_700: '#F8F8F8',
    BLACK: '#fff',
    GRAY_50: '#18191A',
    GRAY_400: '#B0B3B8',
    GRAY_600: '#E4E6EB',
    GRAY_900: '#F5F6F7',
    ...common,
    PINK_50: common.PINK_50_DARK,
    PINK_700: common.PINK_700_DARK,
    GREEN_50: '#1A2E21',
    GREEN_700: '#22C55E',
    RED_50: '#2A1D1D',
    RED_700: '#EF4444',
    YELLOW_50: '#2A2516',
    YELLOW_700: '#EAB308',
    GREEN_500: '#22C55E',
  },
} as const;

const colorHex = {
  RED: colors['light'].PINK_400,
  BLUE: colors['light'].BLUE_400,
  GREEN: colors['light'].GREEN_400,
  YELLOW: colors['light'].YELLOW_400,
  PURPLE: colors['light'].PURPLE_400,
} as const;

export {colors, colorHex};

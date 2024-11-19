import {create} from 'zustand';

export type ThemeMode = 'light' | 'dark';

interface ThemeState {
  theme: ThemeMode;
  setTheme: (theme: ThemeMode) => void;
}

const useThemeStore = create<ThemeState>((set) => ({
  theme: 'light',
  setTheme: (theme) => set({theme}),
}));

export default useThemeStore; 
import React, { useEffect, useRef, useState } from 'react';
import {
  Animated,
  ListRenderItemInfo,
  NativeScrollEvent,
  NativeSyntheticEvent,
  Text,
  View,
  ViewStyle,
  FlatList,
  StyleSheet,
} from 'react-native';
import { colors } from '@/constants';
import useThemeStore from '@/store/useThemeStore';

interface WheelPickerProps {
  items: string[];
  onItemChange: (item: string) => void;
  itemHeight: number;
  initValue?: string;
  containerStyle?: ViewStyle;
}

function WheelPicker({
  items,
  onItemChange,
  itemHeight,
  initValue,
  containerStyle,
}: WheelPickerProps) {
  const { theme } = useThemeStore();
  const scrollY = useRef(new Animated.Value(0)).current;
  const flatListRef = useRef<FlatList>(null);
  const initValueIndex = initValue ? items.indexOf(initValue) : -1;
  const [selectedIndex, setSelectedIndex] = useState(
    initValueIndex >= 0 ? items[initValueIndex] : items[0]
  );

  useEffect(() => {
    if (flatListRef.current && initValueIndex >= 0) {
      flatListRef.current.scrollToIndex({
        index: initValueIndex + 1,
        animated: false,
      });
    }
  }, []);

  const renderItem = ({ item, index }: ListRenderItemInfo<string>) => {
    const inputRange = [
      (index - 2) * itemHeight,
      (index - 1) * itemHeight,
      index * itemHeight,
    ];
    const scale = scrollY.interpolate({
      inputRange,
      outputRange: [0.8, 1, 0.8],
    });

    const isSelected = selectedIndex === item;

    return (
      <Animated.View
        style={[
          {
            height: itemHeight,
            transform: [{ scale }],
            alignItems: 'center',
            justifyContent: 'center',
          },
        ]}>
        <View style={{ width: 60, height: itemHeight, alignItems: 'flex-end', justifyContent: 'center' }}>
          <Text
            style={{
              fontSize: isSelected ? 20 : 16,
              fontWeight: isSelected ? '600' : 'normal',
              color: isSelected ? colors[theme].BLACK : colors[theme].GRAY_500,
              includeFontPadding: false,
              textAlignVertical: 'center',
            }}>
            {item}
          </Text>
        </View>
      </Animated.View>
    );
  };

  const momentumScrollEnd = (event: NativeSyntheticEvent<NativeScrollEvent>) => {
    const y = event.nativeEvent.contentOffset.y;
    const index = Math.round(y / itemHeight);
    setSelectedIndex(items[index]);
  };

  useEffect(() => {
    onItemChange(selectedIndex);
  }, [selectedIndex]);

  return (
    <View style={[{ height: itemHeight * 3 }, containerStyle]}>
      <Animated.FlatList
        ref={flatListRef}
        data={['', ...items, '']}
        renderItem={renderItem}
        showsVerticalScrollIndicator={false}
        snapToInterval={itemHeight}
        onMomentumScrollEnd={momentumScrollEnd}
        scrollEventThrottle={16}
        onScroll={Animated.event(
          [{ nativeEvent: { contentOffset: { y: scrollY } } }],
          { useNativeDriver: true }
        )}
        getItemLayout={(_, index) => ({
          length: itemHeight,
          offset: itemHeight * index,
          index,
        })}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  highlightBox: {
    position: 'absolute',
    top: '33.33%',
    left: 0,
    right: 0,
    height: '33.33%',
    backgroundColor: colors.light.GRAY_50,
    zIndex: -1,
  },
});

export default WheelPicker; 
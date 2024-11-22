import React, {useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Platform,
  Alert,
  Image,
  ScrollView,
  ActivityIndicator,
} from 'react-native';
import {aiNavigations, colors} from '@/constants';
import useThemeStore from '@/store/useThemeStore';
import {ThemeMode} from '@/types';
import {StackScreenProps} from '@react-navigation/stack';
import {AiStackParamList} from '@/navigations/stack/AiStackNavigator';
import {CompoundOption} from '@/components/common/CompoundOption';
import * as ImagePicker from 'react-native-image-picker';
import {request, PERMISSIONS, RESULTS} from 'react-native-permissions';
import {Linking} from 'react-native';
import {alerts} from '@/constants/messages';
import {sendAiScan} from '@/api/ai';
import Ionicons from 'react-native-vector-icons/Ionicons';

type Props = StackScreenProps<AiStackParamList, typeof aiNavigations.AI_CAMERA>;

function AiCameraScreen({route, navigation}: Props) {
  const {theme} = useThemeStore();
  const styles = styling(theme);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [selectedImage, setSelectedImage] = useState<string | null>(null);
  const {petType, hasSymptom} = route.params;
  const [isAnalyzing, setIsAnalyzing] = useState(false);

  const guidelineItems = hasSymptom ? [
    '피부 병변이 잘 보이도록 가까이서 촬영해주세요',
    '피부 발진, 붓기, 상처 등이 선명하게 보이게 해주세요',
    '자연광이나 밝은 조명 아래서 촬영하면 좋아요',
    '여러 각도에서 촬영하면 더 정확한 진단이 가능해요',
  ] : [
    '전체적인 피부와 털 상태가 잘 보이도록 촬영해주세요',
    '피부가 잘 보이도록 털을 살짝 벌려주세요',
    '밝은 곳에서 촬영하면 더 정확합니다',
    '정기적으로 기록하면 변화를 쉽게 파악할 수 있어요',
  ];

  const handleImageSelected = async (uri: string) => {
    setSelectedImage(uri);
  };

  const handleAnalyze = async () => {
    if (!selectedImage) return;

    try {
      setIsAnalyzing(true);
      const result = await sendAiScan(selectedImage, petType, hasSymptom);
      navigation.navigate(aiNavigations.AI_RESULT, {
        result,
        petType,
        hasSymptom
      });
    } catch (error) {
      Alert.alert('오류', '이미지 분석 중 오류가 발생했습니다.');
    } finally {
      setIsAnalyzing(false);
    }
  };

  const launchImageLibrary = async () => {
    const options: ImagePicker.ImageLibraryOptions = {
      mediaType: 'photo',
      includeBase64: false,
      maxHeight: 1024,
      maxWidth: 1024,
    };

    try {
      const permission = await request(
        Platform.OS === 'ios' 
          ? PERMISSIONS.IOS.PHOTO_LIBRARY 
          : PERMISSIONS.ANDROID.READ_MEDIA_IMAGES
      );

      if (permission === RESULTS.GRANTED) {
        const result = await ImagePicker.launchImageLibrary(options);
        if (result.assets && result.assets[0]?.uri) {
          handleImageSelected(result.assets[0].uri);
        }
        return;
      }

      Alert.alert(
        alerts.PHOTO_PERMISSION.TITLE,
        alerts.PHOTO_PERMISSION.DESCRIPTION,
        [
          {
            text: '설정하기',
            onPress: () => Linking.openSettings(),
          },
          {
            text: '취소',
            style: 'cancel',
          }
        ]
      );

    } catch (error) {
      console.log('Image Library Error: ', error);
    }
  };

  const launchCamera = async () => {
    const options: ImagePicker.CameraOptions = {
      mediaType: 'photo',
      includeBase64: false,
      maxHeight: 1024,
      maxWidth: 1024,
    };

    try {
      const permission = await request(
        Platform.OS === 'ios' 
          ? PERMISSIONS.IOS.CAMERA 
          : PERMISSIONS.ANDROID.CAMERA
      );

      if (permission === RESULTS.GRANTED) {
        const result = await ImagePicker.launchCamera(options);
        if (result.assets && result.assets[0]?.uri) {
          handleImageSelected(result.assets[0].uri);
        }
        return;
      }

      Alert.alert(
        alerts.CAMERA_PERMISSION.TITLE,
        alerts.CAMERA_PERMISSION.DESCRIPTION,
        [
          {
            text: '설정하기',
            onPress: () => Linking.openSettings(),
          },
          {
            text: '취소',
            style: 'cancel',
          }
        ]
      );

    } catch (error) {
      console.log('Camera Error: ', error);
    }
  };

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.stepText}>STEP 3/3</Text>
      <Text style={styles.title}>사진 촬영</Text>
      <Text style={styles.subtitle}>
        {petType === 'dog' ? '강아지' : '고양이'}의 
        {hasSymptom ? ' 증상이 있는 부위를' : ' 피부 상태를'} 촬영해주세요
      </Text>

      <View style={styles.guidelineContainer}>
        <Text style={styles.guidelineTitle}>촬영 가이드</Text>
        {guidelineItems.map((item, index) => (
          <View key={index} style={styles.guidelineItem}>
            <Ionicons name="checkmark-circle" size={20} color={colors[theme].PINK_700} />
            <Text style={styles.guidelineText}>{item}</Text>
          </View>
        ))}
      </View>

      <TouchableOpacity 
        style={styles.imageContainer}
        onPress={() => setIsModalVisible(true)}>
        {selectedImage ? (
          <>
            <Image source={{uri: selectedImage}} style={styles.selectedImage} />
            <View style={styles.retakeButton}>
              <Text style={styles.retakeText}>다시 촬영</Text>
            </View>
          </>
        ) : (
          <View style={styles.uploadContainer}>
            <View style={styles.iconCircle}>
              <Ionicons name="camera" size={32} color={colors[theme].PINK_700} />
            </View>
            <Text style={styles.uploadText}>터치하여 촬영하기</Text>
            <Text style={styles.uploadSubtext}>또는 갤러리에서 선택</Text>
          </View>
        )}
      </TouchableOpacity>

      <TouchableOpacity 
        style={[
          styling(theme).analyzeButton,
          !selectedImage && styling(theme).disabledButton
        ]}
        onPress={handleAnalyze}
        disabled={!selectedImage || isAnalyzing}
      >
        {isAnalyzing ? (
          <ActivityIndicator color={colors[theme].WHITE} />
        ) : (
          <Text style={styling(theme).analyzeButtonText}>AI 진단하기</Text>
        )}
      </TouchableOpacity>

      <CompoundOption isVisible={isModalVisible} hideOption={() => setIsModalVisible(false)}>
        <CompoundOption.Background>
          <CompoundOption.Container>
            <CompoundOption.Button 
              onPress={() => {
                launchCamera();
                setIsModalVisible(false);
              }}>
              <View style={styles.optionButton}>
                <Ionicons name="camera" size={24} color={colors[theme].BLACK} />
                <Text style={styles.optionButtonText}>카메라로 촬영</Text>
              </View>
            </CompoundOption.Button>
            <CompoundOption.Divider />
            <CompoundOption.Button 
              onPress={() => {
                launchImageLibrary();
                setIsModalVisible(false);
              }}>
              <View style={styles.optionButton}>
                <Ionicons name="images" size={24} color={colors[theme].BLACK} />
                <Text style={styles.optionButtonText}>갤러리에서 선택</Text>
              </View>
            </CompoundOption.Button>
          </CompoundOption.Container>
          <CompoundOption.Container>
            <CompoundOption.Button onPress={() => setIsModalVisible(false)}>
              취소
            </CompoundOption.Button>
          </CompoundOption.Container>
        </CompoundOption.Background>
      </CompoundOption>
    </ScrollView>
  );
}

const styling = (theme: ThemeMode) =>
  StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: colors[theme].WHITE,
    },
    stepText: {
      fontSize: 14,
      fontWeight: '600',
      color: colors[theme].PINK_700,
      marginTop: 24,
      marginHorizontal: 24,
    },
    title: {
      fontSize: 24,
      fontWeight: 'bold',
      color: colors[theme].BLACK,
      marginTop: 8,
      marginHorizontal: 24,
    },
    subtitle: {
      fontSize: 16,
      color: colors[theme].GRAY_600,
      marginTop: 8,
      marginHorizontal: 24,
    },
    guidelineContainer: {
      backgroundColor: colors[theme].GRAY_50,
      borderRadius: 16,
      padding: 20,
      margin: 24,
    },
    guidelineTitle: {
      fontSize: 16,
      fontWeight: '600',
      color: colors[theme].BLACK,
      marginBottom: 16,
    },
    guidelineItem: {
      flexDirection: 'row',
      alignItems: 'center',
      marginBottom: 12,
    },
    guidelineText: {
      fontSize: 14,
      color: colors[theme].GRAY_700,
      marginLeft: 12,
      flex: 1,
    },
    imageContainer: {
      aspectRatio: 1,
      backgroundColor: colors[theme].GRAY_50,
      marginHorizontal: 24,
      borderRadius: 16,
      overflow: 'hidden',
      borderWidth: 2,
      borderColor: colors[theme].GRAY_200,
      borderStyle: 'dashed',
    },
    selectedImage: {
      width: '100%',
      height: '100%',
    },
    retakeButton: {
      position: 'absolute',
      bottom: 16,
      right: 16,
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      paddingHorizontal: 16,
      paddingVertical: 8,
      borderRadius: 20,
      borderWidth: 1,
      borderColor: colors[theme].PINK_700,
    },
    retakeText: {
      fontSize: 16,
      fontWeight: 'bold',
      color: colors[theme].PINK_700,
    },
    uploadContainer: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
    },
    iconCircle: {
      width: 80,
      height: 80,
      borderRadius: 40,
      backgroundColor: colors[theme].GRAY_50,
      justifyContent: 'center',
      alignItems: 'center',
    },
    uploadText: {
      fontSize: 16,
      fontWeight: 'bold',
      color: colors[theme].BLACK,
      marginTop: 12,
    },
    uploadSubtext: {
      fontSize: 14,
      color: colors[theme].GRAY_500,
    },
    analyzeButton: {
      backgroundColor: colors[theme].PINK_700,
      borderRadius: 12,
      padding: 20,
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'center',
      marginTop: 20,
      marginHorizontal: 20,
      marginBottom: 40,
    },
    analyzeButtonText: {
      color: colors[theme].WHITE,
      fontSize: 17,
      fontWeight: 'bold',
      marginRight: 8,
    },
    optionButton: {
      flexDirection: 'row',
      alignItems: 'center',
      paddingVertical: 8,
    },
    optionButtonText: {
      marginLeft: 12,
      fontSize: 16,
      color: colors[theme].BLACK,
    },
    disabledButton: {
      backgroundColor: colors[theme].GRAY_200,
    },
  });

export default AiCameraScreen;

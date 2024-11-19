import React, { useState, useRef } from 'react';
import { View, TextInput, TouchableOpacity, Text, StyleSheet, Alert, Image, ScrollView, Platform, Modal } from 'react-native';
import { StackScreenProps } from '@react-navigation/stack';
import { BoardStackParamList } from '@/navigations/stack/BoardStackNavigator';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { createPost } from '@/api/post';
import Icon from 'react-native-vector-icons/Ionicons';
import * as ImagePicker from 'react-native-image-picker';
import { request, PERMISSIONS, RESULTS } from 'react-native-permissions';
import { Linking } from 'react-native';
import { alerts } from '@/constants';
import usePermission from '@/hooks/usePermission';
import { CompoundOption } from '@/components/common/CompoundOption';

type Props = StackScreenProps<BoardStackParamList, 'PostCreate'>;

function PostCreateScreen({ route, navigation }: Props) {
  const { category } = route.params;
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [images, setImages] = useState<string[]>([]);
  const queryClient = useQueryClient();
  const [isModalVisible, setIsModalVisible] = useState(false);

  const createMutation = useMutation({
    mutationFn: () => createPost(category, title, content, images),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['posts', category] });
      navigation.goBack();
    },
    onError: (error) => {
      Alert.alert('오류', '게시글 등록에 실패했습니다.');
      console.error('Create post error details:', error);
    },
  });

  const handleSubmit = () => {
    if (!title.trim() || !content.trim()) {
      Alert.alert('오류', '제목과 내용을 모두 입력해주세요.');
      return;
    }
    createMutation.mutate();
  };

  const handleImagePicker = () => {
    if (images.length < 5) {
      setIsModalVisible(true);
    } else {
      Alert.alert('알림', '이미지는 최대 5개까지 첨부할 수 있습니다.');
    }
  };

  const removeImage = (index: number) => {
    setImages(prev => prev.filter((_, i) => i !== index));
  };

  const launchImageLibrary = async () => {
    const options: ImagePicker.ImageLibraryOptions = {
      mediaType: 'photo',
      selectionLimit: 5 - images.length,
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
        if (result.assets) {
          const newImages = result.assets.map(asset => asset.uri!);
          setImages(prev => [...prev, ...newImages]);
        }
        return;
      }

      const result = await request(
        Platform.OS === 'ios' 
          ? PERMISSIONS.IOS.PHOTO_LIBRARY 
          : PERMISSIONS.ANDROID.READ_MEDIA_IMAGES
      );
      
      if (result === RESULTS.GRANTED) {
        const result = await ImagePicker.launchImageLibrary(options);
        if (result.assets) {
          const newImages = result.assets.map(asset => asset.uri!);
          setImages(prev => [...prev, ...newImages]);
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

    const permission = await request(
      Platform.OS === 'ios' 
        ? PERMISSIONS.IOS.CAMERA 
        : PERMISSIONS.ANDROID.CAMERA
    );
    
    if (permission !== RESULTS.GRANTED) {
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
      return;
    }

    try {
      const result = await ImagePicker.launchCamera(options);
      if (result.assets && result.assets.length > 0) {
        const uri = result.assets[0].uri;
        if (uri) {
          setImages(prev => [...prev, uri]);
        }
      }
    } catch (error) {
      console.log('Camera Error: ', error);
    }
  };

  const ImagePickerModal = ({ visible, onClose, onSelectImage, onTakePhoto }: {
    visible: boolean;
    onClose: () => void;
    onSelectImage: () => void;
    onTakePhoto: () => void;
  }) => {
    return (
      <CompoundOption isVisible={visible} hideOption={onClose}>
        <CompoundOption.Background>
          <CompoundOption.Container>
            <CompoundOption.Button onPress={onSelectImage}>
              앨범에서 선택
            </CompoundOption.Button>
            <CompoundOption.Divider />
            <CompoundOption.Button onPress={onTakePhoto}>
              카메라로 촬영
            </CompoundOption.Button>
          </CompoundOption.Container>
          <CompoundOption.Container>
            <CompoundOption.Button onPress={onClose}>
              취소
            </CompoundOption.Button>
          </CompoundOption.Container>
        </CompoundOption.Background>
      </CompoundOption>
    );
  };

  return (
    <View style={styles.container}>
      <TextInput
        placeholder="제목"
        value={title}
        onChangeText={setTitle}
        style={styles.titleInput}
      />
      <View style={styles.separator} />
      <TextInput
        placeholder="내용을 입력하세요"
        value={content}
        onChangeText={setContent}
        style={styles.contentInput}
        multiline
      />
      <ScrollView horizontal style={styles.imageContainer}>
        {images.map((uri, index) => (
          <View key={index} style={styles.imageWrapper}>
            <Image source={{ uri }} style={styles.imagePreview} />
            <TouchableOpacity 
              style={styles.removeImageButton}
              onPress={() => removeImage(index)}
            >
              <Icon name="close-circle" size={24} color="#e74c3c" />
            </TouchableOpacity>
          </View>
        ))}
      </ScrollView>
      <View style={styles.buttonContainer}>
        <TouchableOpacity 
          style={styles.cameraButton} 
          onPress={handleImagePicker}
        >
          <Icon name="images" size={24} color="#ffffff" />
        </TouchableOpacity>
        <TouchableOpacity 
          style={styles.submitButton} 
          onPress={handleSubmit}
        >
          <Icon name="send" size={24} color="#ffffff" />
        </TouchableOpacity>
      </View>
      <ImagePickerModal
        visible={isModalVisible}
        onClose={() => setIsModalVisible(false)}
        onSelectImage={() => {
          setIsModalVisible(false);
          launchImageLibrary();
        }}
        onTakePhoto={() => {
          setIsModalVisible(false);
          launchCamera();
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#ffffff',
    padding: 15,
  },
  titleInput: {
    fontSize: 18,
    fontWeight: 'bold',
  },
  contentInput: {
    fontSize: 16,
    flex: 1,
    textAlignVertical: 'top',
  },
  submitButton: {
    backgroundColor: '#e74c3c',
    width: 50,
    height: 50,
    borderRadius: 25,
    justifyContent: 'center',
    alignItems: 'center',
  },
  separator: {
    height: 1,
    backgroundColor: '#CCCCCC',
    marginTop: 0,
    marginBottom: 10,
  },
  imagePreview: {
    width: 100,
    height: 100,
    marginTop: 10,
    alignSelf: 'center',
  },
  cameraButton: {
    backgroundColor: '#e74c3c',
    width: 50,
    height: 50,
    borderRadius: 25,
    justifyContent: 'center',
    alignItems: 'center',
  },
  actionSheetContainer: {
    padding: 16,
  },
  actionSheetButton: {
    paddingVertical: 20,
    alignItems: 'center',
  },
  actionSheetButtonText: {
    fontSize: 20,
    color: '#007AFF',
  },
  actionSheetCancelButton: {
    paddingVertical: 16,
    alignItems: 'center',
  },
  actionSheetCancelButtonText: {
    fontSize: 18,
    color: '#FF3B30',
  },
  actionSheetSeparator: {
    height: 1,
    backgroundColor: '#CCCCCC',
    marginHorizontal: 16,
  },
  imageContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginTop: 10,
  },
  imageWrapper: {
    marginRight: 10,
    marginBottom: 10,
  },
  addImageButton: {
    position: 'absolute',
    right: 15,
    bottom: 15,
    backgroundColor: '#e74c3c',
    width: 50,
    height: 50,
    borderRadius: 25,
    justifyContent: 'center',
    alignItems: 'center',
  },
  removeImageButton: {
    position: 'absolute',
    top: 0,
    right: 0,
    backgroundColor: '#e74c3c',
    width: 30,
    height: 30,
    borderRadius: 15,
    justifyContent: 'center',
    alignItems: 'center',
  },
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    position: 'absolute',
    left: 15,
    right: 15,
    bottom: 15,
  },
});

export default PostCreateScreen;

import React from 'react';
import { colors, mainNavigations } from "@/constants";
import useAuth from "@/hooks/queries/useAuth";
import { DrawerContentComponentProps, DrawerContentScrollView, DrawerItemList } from "@react-navigation/drawer";
import { Image, Pressable, SafeAreaView, StyleSheet, Text, View } from "react-native";
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';
import useThemeStore, { ThemeMode } from '@/store/useThemeStore';
import LinearGradient from 'react-native-linear-gradient';

function CustomDrawerContent(props: DrawerContentComponentProps) {
    const { getProfileQuery } = useAuth();
    const { email, nickName, imageUri } = getProfileQuery.data || {};  
    const {theme} = useThemeStore();
    const styles = styling(theme);

    const handlePressSetting = () => {
        props.navigation.navigate(mainNavigations.SETTING);
      };
    
    return (
        <SafeAreaView style={styles.container}>
            <DrawerContentScrollView 
                {...props}
                scrollEnabled={false} 
                contentContainerStyle={styles.contentContainer}>
                <LinearGradient
                    colors={[colors[theme].PINK_500, colors[theme].PINK_700]}
                    style={styles.userInfoContainer}
                >
                    <View style={styles.userImageContainer}>
                        {(!imageUri || imageUri === 'null') ? (
                            <Image 
                                source={require('@/assets/user-default.png')}
                                style={styles.userImage}
                            />
                        ) : (
                            <Image 
                                source={{ uri: imageUri }} 
                                style={styles.userImage}
                            />
                        )}
                    </View>
                    <Text style={styles.nameText}>{nickName ?? email}</Text>
                </LinearGradient>
                <View style={styles.menuContainer}>
                    <DrawerItemList {...props} />
                </View>                 
            </DrawerContentScrollView>
            <Pressable 
                style={({pressed}) => [
                    styles.settingButton,
                    {
                        backgroundColor: pressed 
                            ? colors[theme].GRAY_100
                            : colors[theme].WHITE,
                    }
                ]}
                android_ripple={{
                    color: colors[theme].GRAY_200,
                }}
                onPress={handlePressSetting}>
                <MaterialIcons name="settings" size={24} color={colors[theme].GRAY_700} />
                <Text style={styles.settingText}>설정</Text>
            </Pressable>
        </SafeAreaView>
    );
}

const styling = (theme: ThemeMode) =>
    StyleSheet.create({
      container: {
        flex: 1,
      },
      contentContainer: {
        flex: 1,
        backgroundColor: colors[theme].GRAY_100,
      },
      userInfoContainer: {
        padding: 20,
        borderBottomWidth: 1,
        borderBottomColor: colors[theme].GRAY_200,
        backgroundColor: colors[theme].PINK_700,
        borderBottomLeftRadius: 15,
        borderBottomRightRadius: 15,
      },
      userImageContainer: {
        width: 60,
        height: 60,
        borderRadius: 30,
        overflow: 'hidden',
        marginBottom: 10,
        borderWidth: 2,
        borderColor: colors[theme].WHITE,
        backgroundColor: colors[theme].GRAY_100,
      },
      userImage: {
        width: '100%',
        height: '100%',
      },
      nameText: {
        fontSize: 16,
        fontWeight: '600',
        color: colors[theme].WHITE,
      },
      settingButton: {
        flexDirection: 'row',
        alignItems: 'center',
        padding: 15,
        borderTopWidth: 1,
        borderTopColor: colors[theme].GRAY_200,
        backgroundColor: colors[theme].WHITE,
      },
      settingText: {
        marginLeft: 10,
        fontSize: 16,
        color: colors[theme].GRAY_700,
      },
      menuContainer: {
        marginTop: 10,
      },
    });

export default CustomDrawerContent;

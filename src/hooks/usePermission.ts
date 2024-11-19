import { alerts } from "@/constants";
import { useEffect } from "react";
import { Alert, Linking, Platform } from "react-native";
import { check, request, PERMISSIONS, RESULTS, Permission } from "react-native-permissions";

type PermissionType = 'LOCATION' | 'CAMERA' | 'PHOTO' | 'NOTIFICATION'

type PermissionOS = {
    [key in PermissionType]: Permission;
}

const androidPermissions:PermissionOS = {
    LOCATION: PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION,
    PHOTO: PERMISSIONS.ANDROID.READ_EXTERNAL_STORAGE,
    CAMERA: PERMISSIONS.ANDROID.CAMERA,
    NOTIFICATION: PERMISSIONS.ANDROID.POST_NOTIFICATIONS,
}

const iosPermissions:PermissionOS = {
    LOCATION: PERMISSIONS.IOS.LOCATION_WHEN_IN_USE,
    PHOTO: PERMISSIONS.IOS.PHOTO_LIBRARY,
    CAMERA: PERMISSIONS.IOS.CAMERA,
    NOTIFICATION: PERMISSIONS.IOS.APP_TRACKING_TRANSPARENCY,
}

function usePermission(type: PermissionType) {
  useEffect(() => {
    (async () => {
      const isAndroid = Platform.OS === 'android';
      const permissionOS = isAndroid ? androidPermissions : iosPermissions;

      const checked = await check(permissionOS[type]);

      const showPermissionAlert = () => {
        if (type === 'NOTIFICATION') {
          Alert.alert(
            alerts.NOTIFICATION_PERMISSION.TITLE,
            alerts.NOTIFICATION_PERMISSION.DESCRIPTION,
            [
              {
                text: '확인',
                style: 'cancel',
              }
            ]
          );
          return;
        }

        Alert.alert(
          alerts[`${type}_PERMISSION`].TITLE,
          alerts[`${type}_PERMISSION`].DESCRIPTION,
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
      };

      if (checked === RESULTS.DENIED) {
        const result = await request(permissionOS[type]);
        if (result !== RESULTS.GRANTED && type !== 'NOTIFICATION') {
          showPermissionAlert();
        }
      } else if (checked === RESULTS.BLOCKED) {
        showPermissionAlert();
      }
    })();
  }, [type]);
}

export default usePermission;

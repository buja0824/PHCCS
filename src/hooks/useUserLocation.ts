import {useEffect, useState} from 'react';
import {LatLng} from 'react-native-maps';
import GeoLocation from '@react-native-community/geolocation';
import {PERMISSIONS, RESULTS, check} from 'react-native-permissions';
import {getEncryptStorage, setEncryptStorage} from '@/utils/encryptStorage';

import useAppState from './useAppState';

function useUserLocation() {
  const [userLocation, setUserLocation] = useState<LatLng>({
    latitude: 37.5516032365118,
    longitude: 126.98989626020192,
  });
  const [isUserLocationError, setIsUserLocationError] = useState(false);
  const {isComeback} = useAppState();

  const updateUserLocation = async (callback?: (location: LatLng) => void) => {
    GeoLocation.getCurrentPosition(
      async info => {
        const { latitude, longitude } = info.coords;
        setUserLocation({ latitude, longitude });
        setIsUserLocationError(false);
        await setEncryptStorage('userLocation', { latitude, longitude });
        if (callback) {
          callback({ latitude, longitude });
        }
      },
      () => {
        setIsUserLocationError(true);
      },
      {
        enableHighAccuracy: true,
        timeout: 15000,
        maximumAge: 10000,
      },
    );
  };

  useEffect(() => {
    const getLocation = async () => {
      try {
        const savedLocation = await getEncryptStorage('userLocation');
        if (savedLocation) {
          setUserLocation(savedLocation);
        }

        const permission = await check(PERMISSIONS.IOS.LOCATION_WHEN_IN_USE);
        if (permission === RESULTS.GRANTED) {
          GeoLocation.watchPosition(
            info => {
              const {latitude, longitude} = info.coords;
              setUserLocation({latitude, longitude});
              setIsUserLocationError(false);
            },
            () => {
              setIsUserLocationError(true);
            },
            {
              enableHighAccuracy: true,
              distanceFilter: 10,
              interval: 5000,
              fastestInterval: 2000,
            },
          );
        }
      } catch (error) {
        console.error('Error checking location permission:', error);
        setIsUserLocationError(true);
      }
    };

    getLocation();
  }, []);

  return { userLocation, isUserLocationError, updateUserLocation };
}

export default useUserLocation;

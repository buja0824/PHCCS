import axiosInstance from './axios';
import { LatLng } from 'react-native-maps';
import Config from 'react-native-config';

const GOOGLE_MAPS = Config.GOOGLE_MAPS_API_KEY;

export interface AnimalHospital {
  id: string;
  name: string;
  latitude: number;
  longitude: number;
  address: string;
  distance: number;
  openingHours?: string[];
  phoneNumber?: string;
  type: 'hospital' | 'pharmacy';
  isOpen?: boolean;
  rating?: number;
  photos?: string[];
  reviews?: {
    author_name: string;
    rating: number;
    text: string;
    time: number;
  }[];
}

export const fetchNearbyAnimalHospitals = async (
  location: LatLng, 
  type: 'hospital' | 'pharmacy'
): Promise<AnimalHospital[]> => {
  const { latitude, longitude } = location;
  const radius = 2000; // 2km 반경

  const placeType = type === 'hospital' ? 'veterinary_care' : 'pharmacy';
  
  const response = await axiosInstance.get(
    'https://maps.googleapis.com/maps/api/place/nearbysearch/json',
    {
      params: {
        location: `${latitude},${longitude}`,
        radius,
        type: placeType,
        keyword: type === 'pharmacy' ? '동물약국|애견약국|반려동물약국' : '',
        key: GOOGLE_MAPS,
        language: 'ko',
        fields: 'place_id,name,geometry,vicinity,opening_hours,formatted_phone_number',
      },
    }
  );

  if (response.data.status !== 'OK') {
    throw new Error(response.data.error_message || 'Places API 요청 실패');
  }

  const hospitals: AnimalHospital[] = await Promise.all(response.data.results.map(async (place: any) => {
    const detailsResponse = await axiosInstance.get('https://maps.googleapis.com/maps/api/place/details/json', {
      params: {
        place_id: place.place_id,
        fields: 'opening_hours,formatted_phone_number,photos,rating,reviews,current_opening_hours',
        key: GOOGLE_MAPS,
        language: 'ko',
      },
    });

    return {
      id: `${place.place_id}-${type}`,
      name: place.name,
      latitude: place.geometry.location.lat,
      longitude: place.geometry.location.lng,
      address: place.vicinity,
      distance: 0,
      openingHours: detailsResponse.data.result.opening_hours?.weekday_text,
      phoneNumber: detailsResponse.data.result.formatted_phone_number,
      type: type,
      isOpen: detailsResponse.data.result.current_opening_hours?.open_now,
      rating: detailsResponse.data.result.rating,
      photos: detailsResponse.data.result.photos?.map((photo: any) => 
        `https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&maxheight=400&photo_reference=${photo.photo_reference}&key=${GOOGLE_MAPS}`
      ),
      reviews: detailsResponse.data.result.reviews,
    };
  }));

  return hospitals;
};

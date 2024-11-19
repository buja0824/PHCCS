import { useQuery } from '@tanstack/react-query';
import { fetchNearbyAnimalHospitals, AnimalHospital } from '@/api/places';
import useUserLocation from './useUserLocation';

const useNearbyAnimalHospitalsData = (type: 'all' | 'hospital' | 'pharmacy') => {
  const { userLocation, isUserLocationError } = useUserLocation();

  return useQuery<AnimalHospital[], Error>({
    queryKey: ['nearbyAnimalHospitals', userLocation, type],
    queryFn: () => type === 'all' 
      ? Promise.all([
          fetchNearbyAnimalHospitals(userLocation, 'hospital'),
          fetchNearbyAnimalHospitals(userLocation, 'pharmacy')
        ]).then(([hospitals, pharmacies]) => [...hospitals, ...pharmacies])
      : fetchNearbyAnimalHospitals(userLocation, type),
    enabled: !isUserLocationError && userLocation.latitude !== 37.5666805,
    staleTime: 1000 * 60 * 5,
  });
};

export default useNearbyAnimalHospitalsData;
export const calculateAge = (birthDate: Date): number => {
  const today = new Date();
  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();
  
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }
  
  return age;
};

export const calculateDaysFromAdoption = (adoptionDate: Date): number => {
  const today = new Date();
  const diffTime = Math.abs(today.getTime() - adoptionDate.getTime());
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
};

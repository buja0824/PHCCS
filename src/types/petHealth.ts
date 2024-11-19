export interface HealthCheckup {
  id: number;
  petName: string;
  date: string;
  weight: number;
  height?: number;
  description: string;
  nextCheckupDate?: string;
}

export interface Vaccination {
  id: number;
  petName: string;
  date: string;
  type: string;
  description: string;
  nextVaccinationDate?: string;
}

export interface MedicalHistory {
  id: number;
  petName: string;
  date: string;
  condition: string;
  treatment: string;
  medication?: string;
  nextVisitDate?: string;
}

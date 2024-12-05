export interface Vet {
  id: number;
  memberId: number;
  hospitalName: string;
  hospitalAddr: string;
  nickName: string;
}

export interface VetResponse {
  content: Vet[];
  last: boolean;
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
} 
import {Profile} from '../types/domain';
import {getEncryptStorage} from '../utils';
import axiosInstance from './axios';

type RequestUser = {
  email: string;
  pwd: string;
  nickName?: string;
  role?: number;
};

type RequestVet = RequestUser & {
  hospitalName: string;
  hospitalAddr: string;
  licenseNo: string;
};

const postSignup = async (user: RequestUser): Promise<void> => {
  const {data} = await axiosInstance.post('/auth/signup/member', user);
  return data;
};

const postVetSignup = async (vet: RequestVet): Promise<void> => {
  const {data} = await axiosInstance.post('/auth/signup/vet', vet);
  return data;
};

type ResponseToken = {
  accessToken: string;
  refreshToken: string;
};

const postLogin = async ({
  email,
  pwd,
}: RequestUser): Promise<ResponseToken> => {
  const {data} = await axiosInstance.post('/auth/signin', {
    email,
    pwd,
  });

  return data;
};

type ResponseProfile = Profile;

const getProfile = async (): Promise<ResponseProfile> => {
  const {data} = await axiosInstance.get('/auth/me');

  return data;
};

type RequestProfileUpdate = {
  currentPwd?: string;
  pwd?: string;
  nickname?: string;
};

const editProfile = async (body: RequestProfileUpdate): Promise<ResponseProfile> => {
  const {data} = await axiosInstance.patch('/member/update', body);
  return data;
};

const getAccessToken = async (): Promise<ResponseToken> => {
  const refreshToken = await getEncryptStorage('refreshToken');

  const {data} = await axiosInstance.get('/auth/refresh', {
    headers: {
      Authorization: `Bearer ${refreshToken}`,
    },
  });
  console.log(refreshToken);
  return data;
};

const logout = async () => {
  const refreshToken = await getEncryptStorage('refreshToken');
  await axiosInstance.post('/auth/logout', null, {
    headers: {
      Authorization: `Bearer ${refreshToken}`,
    },
  });
};

const deleteAccount = async () => {
  await axiosInstance.delete('/member/delete');
};

export {postSignup, postVetSignup, postLogin, getProfile, getAccessToken, logout, deleteAccount, editProfile};
export type {RequestUser, RequestVet, ResponseToken, ResponseProfile, RequestProfileUpdate };

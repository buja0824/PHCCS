type UserInformation = {
  email: string;
  pwd: string;
  nickName?: string;
  role?: number;
};

export interface VetInformation extends UserInformation {
  hospitalName: string;
  hospitalAddr: string;
  licenseNo: string;
}

function validateUser(values: UserInformation) {
  const errors = {
    email: '',
    pwd: '',
    nickName: '',
  };

  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(values.email)) {
    errors.email = '올바른 이메일 형식이 아닙니다.';
  }
  if (!(values.pwd.length >= 8 && values.pwd.length <= 20)) {
    errors.pwd = '비밀번호는 8~20자 사이로 입력해주세요.';
  }

  return errors;
}

function validateLogin(values: UserInformation) {
  return validateUser(values);
}

function validateSignup(values: UserInformation & {pwdConfirm: string}): Partial<UserInformation & {pwdConfirm: string}> {
  const errors = validateUser(values);
  const signupErrors: Partial<UserInformation & {pwdConfirm: string}> = {...errors, pwdConfirm: ''};

  if (values.pwd !== values.pwdConfirm) {
    signupErrors.pwdConfirm = '비밀번호가 일치하지않습니다.';
  }

  if (values.nickName) {
    if (!values.nickName.trim()) {
      signupErrors.nickName = '닉네임을 입력해주세요.';
    } else if (values.nickName.length < 2 || values.nickName.length > 10) {
      signupErrors.nickName = '닉네임은 2~10자 사이로 입력해주세요.';
    }
  } else {
    signupErrors.nickName = '닉네임을 입력해주세요.';
  }

  return signupErrors;
}
function validateVetSignup(values: VetInformation & {pwdConfirm: string}): Partial<VetInformation & {pwdConfirm: string}> {
  const errors = validateSignup(values);
  const vetErrors: Partial<VetInformation & {pwdConfirm: string}> = {...errors};

  if (!values.hospitalName?.trim()) {
    vetErrors.hospitalName = '병원 이름을 입력해주세요.';
  }

  if (!values.hospitalAddr?.trim()) {
    vetErrors.hospitalAddr = '병원 주소를 입력해주세요.';
  }

  if (!values.licenseNo?.trim()) {
    vetErrors.licenseNo = '라이센스 번호를 입력해주세요.';
  } 

  return vetErrors;
}

function validateEditPassword(values: {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}) {
  const errors = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  };

  if (!values.currentPassword) {
    errors.currentPassword = '현재 비밀번호를 입력해주세요.';
  }

  if (!values.newPassword) {
    errors.newPassword = '새 비밀번호를 입력해주세요.';
  } else if (!(values.newPassword.length >= 8 && values.newPassword.length <= 20)) {
    errors.newPassword = '비밀번호는 8~20자 사이로 입력해주세요.';
  }

  if (!values.confirmPassword) {
    errors.confirmPassword = '새 비밀번호 확인을 입력해주세요.';
  } else if (values.newPassword !== values.confirmPassword) {
    errors.confirmPassword = '비밀번호가 일치하지 않습니다.';
  }

  return errors;
}

export {validateLogin, validateSignup, validateVetSignup, validateEditPassword};

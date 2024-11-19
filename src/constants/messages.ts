const errorMessages = {
    UNEXPECT_ERROR: '알 수 없는 에러가 발생했습니다.',
  } as const;
  
const alerts =  {
    LOCATION_PERMISSION:    {
        TITLE:'위치 권한 허용이 필요합니다.',
        DESCRIPTION:'설정 화면에서 위치 권한을 허용해 주세요',
    },
    PHOTO_PERMISSION: {
        TITLE:'사진 접근 허용이 필요합니다.',
        DESCRIPTION:'설정 화면에서 사진 권한을 허용해 주세요',
    },
    CAMERA_PERMISSION: {
        TITLE:'카메라 권한 허용이 필요합니다.',
        DESCRIPTION:'설정 화면에서 카메라 권한을 허용해 주세요',
    },
    DELETE_ACCOUNT: {
        TITLE: '정말 탈퇴하시겠습니까?',
        DESCRIPTION: '회원 정보는 삭제되며, 복구할 수 없습니다.',
    },
    NOTIFICATION_PERMISSION: {
        TITLE: '알림 설정',
        DESCRIPTION: '알림 권한을 허용하지 않으면 댓글과 채팅 알림을 받을 수 없어요.',
    },
} as const;

export {errorMessages, alerts};
export interface Post {
    id: number;
    memberId: number;
    category: string;
    nickName: string;
    title: string;
    content : string;
    partOfContent: string;
    viewCnt: number;
    likeCnt: number;
    createDate: string;
    updateTime: string;
    fileDir: string;
    fileList: string[];
    images?: string[];
    isEdited?: boolean;
    isMine?: boolean;
}

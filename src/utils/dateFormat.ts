export const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  const now = new Date();
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const targetDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());

  // 시간 포맷
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');
  
  // 오늘 작성된 글
  if (targetDate.getTime() === today.getTime()) {
    return `${hours}:${minutes}`;
  }
  
  // 올해 작성된 글
  if (date.getFullYear() === now.getFullYear()) {
    return `${(date.getMonth() + 1).toString().padStart(2, '0')}.${date.getDate().toString().padStart(2, '0')}`;
  }
  
  // 이전 년도에 작성된 글
  return `${date.getFullYear()}.${(date.getMonth() + 1).toString().padStart(2, '0')}.${date.getDate().toString().padStart(2, '0')}`;
}; 
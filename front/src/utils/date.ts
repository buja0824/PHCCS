export const isSameAsCurrentDate = (year: number, month: number, date: number) => {
  const today = new Date();
  return (
    today.getFullYear() === year &&
    today.getMonth() + 1 === month &&
    today.getDate() === date
  );
};

export const getMonthYearDetails = (date: Date) => {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const firstDay = new Date(year, month - 1, 1);
  const lastDay = new Date(year, month, 0);

  return {
    year,
    month,
    firstDOW: firstDay.getDay(),
    lastDate: lastDay.getDate(),
  };
}; 
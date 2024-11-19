export interface CalendarPost {
  id: number;
  content: string;
  date?: Date;
}

type ResponseCalendarPost = Record<number, CalendarPost[]>;

type MonthYear = {
  month: number;
  year: number;
  startDate: Date;
  firstDOW: number;
  lastDate: number;
};

export type { ResponseCalendarPost, MonthYear}; 
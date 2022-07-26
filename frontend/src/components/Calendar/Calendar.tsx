import { useTheme } from '@emotion/react';
import { useState } from 'react';

import CalendarDate from '@/components/CalendarDate/CalendarDate';

import { DAYS } from '@/constants';

import {
  getBeforeYearMonth,
  getCalendarMonth,
  getNextYearMonth,
  getThisMonth,
  getThisYear,
} from '@/utils/date';

import { AiOutlineLeft, AiOutlineRight } from 'react-icons/ai';

import Button from '../@common/Button/Button';
import {
  calendarGrid,
  calendarHeader,
  dayBar,
  monthPicker,
  navBarGrid,
  navButton,
  navButtonTitle,
  todayButton,
} from './Calendar.styles';

interface CalendarProps {
  current: {
    year: number;
    month: number;
  };
  setCurrent: React.Dispatch<
    React.SetStateAction<{
      year: number;
      month: number;
    }>
  >;
}

function Calendar({ current, setCurrent }: CalendarProps) {
  const theme = useTheme();

  const [calendarMonth, setCalendarMonth] = useState(
    getCalendarMonth(getThisYear(), getThisMonth())
  );

  const handleClickBeforeMonthButton = () => {
    const { year, month } = getBeforeYearMonth(current.year, current.month);

    setCurrent({ year, month });
    setCalendarMonth(getCalendarMonth(year, month));
  };

  const handleClickTodayButton = () => {
    const year = getThisYear();
    const month = getThisMonth();

    setCurrent({ year, month });
    setCalendarMonth(getCalendarMonth(year, month));
  };

  const handleClickNextMonthButton = () => {
    const { year, month } = getNextYearMonth(current.year, current.month);

    setCurrent({ year, month });
    setCalendarMonth(getCalendarMonth(year, month));
  };

  const rowNum = Math.ceil(calendarMonth.length / 7);

  return (
    <>
      <div css={calendarHeader(theme)}>
        <span>
          {current.year}년 {current.month}월
        </span>
        <div css={monthPicker}>
          <Button cssProp={navButton} onClick={handleClickBeforeMonthButton}>
            <AiOutlineLeft />
            <span css={navButtonTitle}>전 달</span>
          </Button>
          <Button cssProp={todayButton} onClick={handleClickTodayButton}>
            오늘
          </Button>
          <Button cssProp={navButton} onClick={handleClickNextMonthButton}>
            <AiOutlineRight />
            <span css={navButtonTitle}>다음 달</span>
          </Button>
        </div>
      </div>

      <div css={navBarGrid}>
        {DAYS.map((day) => (
          <span key={day} css={dayBar(theme, day)}>
            {day}
          </span>
        ))}
      </div>
      <div css={calendarGrid(rowNum)}>
        {calendarMonth.map((info) => {
          const key = `${info.year}${info.month}${info.date}${info.day}`;

          return (
            <CalendarDate key={key} dateInfo={info} isThisMonth={current.month === info.month} />
          );
        })}
      </div>
    </>
  );
}

export default Calendar;

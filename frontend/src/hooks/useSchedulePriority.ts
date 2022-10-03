import { ScheduleType } from '@/@types/schedule';

import { CALENDAR } from '@/constants';
import { DATE_TIME } from '@/constants/date';

import { getDayOffsetDateTime, getISODateString } from '@/utils/date';

function useSchedulePriority(calendar: string[]) {
  const calendarWithPriority = calendar.reduce(
    (
      acc: {
        [key: string]: Array<boolean>;
      },
      cur
    ) => {
      acc[getISODateString(cur)] = new Array(CALENDAR.MAX_SCHEDULE_COUNT).fill(false);

      return acc;
    },
    {}
  );

  const getLongTermSchedulesWithPriority = (longTerms: Array<ScheduleType>) =>
    longTerms.map((schedule) => {
      const startDate = getISODateString(schedule.startDateTime);
      if (!calendarWithPriority.hasOwnProperty(startDate)) {
        return {
          schedule,
          priority: null,
        };
      }

      const priority = calendarWithPriority[startDate].findIndex((el) => !el);

      if (priority === -1) {
        return {
          schedule,
          priority: null,
        };
      }

      const endDate = getISODateString(
        schedule.endDateTime.endsWith(DATE_TIME.END)
          ? getDayOffsetDateTime(schedule.endDateTime, -1)
          : schedule.endDateTime
      );
      const scheduleRange = calendar
        .filter((dateTime) => {
          const date = getISODateString(dateTime);

          return startDate <= date && date <= endDate;
        })
        .map((dateTime) => dateTime.split('T')[0]);

      scheduleRange.forEach((dateTime) => {
        if (calendarWithPriority.hasOwnProperty(dateTime)) {
          calendarWithPriority[dateTime][priority] = true;
        }
      });

      return {
        schedule,
        priority: priority + 1,
      };
    });

  const getSingleSchedulesWithPriority = (singleSchedules: Array<ScheduleType>) =>
    singleSchedules.map((schedule) => {
      const startDate = getISODateString(schedule.startDateTime);

      if (!calendarWithPriority.hasOwnProperty(startDate)) {
        return {
          schedule,
          priority: null,
        };
      }

      const priority = calendarWithPriority[startDate].findIndex((el) => !el);

      if (priority === -1) {
        return {
          schedule,
          priority: null,
        };
      }

      calendarWithPriority[startDate][priority] = true;

      return {
        schedule,
        priority: priority + 1,
      };
    });

  return {
    calendarWithPriority,
    getLongTermSchedulesWithPriority,
    getSingleSchedulesWithPriority,
  };
}

export default useSchedulePriority;

import { ScheduleResponseType, ScheduleType } from '@/@types/schedule';

import dallogApi from './';

const scheduleApi = {
  endpoint: {
    get: '/api/members/me/schedules',
    post: (categoryId: number) => `/api/categories/${categoryId}/schedules`,
  },
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },

  get: async (accessToken: string | null, startDate: string, endDate: string) => {
    const response = await dallogApi.get<ScheduleResponseType>(
      `${scheduleApi.endpoint.get}?startDate=${startDate}&endDate=${endDate}`,
      {
        headers: { ...scheduleApi.headers, Authorization: `Bearer ${accessToken}` },
      }
    );

    return response;
  },

  post: async (categoryId: number, body: Omit<ScheduleType, 'id'>) => {
    console.log(categoryId, body);
    const response = await dallogApi.post(scheduleApi.endpoint.post(categoryId), body, {
      headers: scheduleApi.headers,
    });

    return response;
  },
};

export default scheduleApi;

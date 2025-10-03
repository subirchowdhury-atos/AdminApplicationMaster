import api from './axios';

const API_BASE_URL = '/api/v1/dashboard';

export const dashboardApi = {
  // Get dashboard data (recent loan applications and statistics)
  getData: async () => {
    const response = await api.get(API_BASE_URL);
    return response.data;
  },
};

export default dashboardApi;
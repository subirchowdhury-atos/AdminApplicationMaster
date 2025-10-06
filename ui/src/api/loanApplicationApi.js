import api from './axios';

const API_BASE_URL = '/api/v1/application_services';

export const loanApplicationApi = {
  getAll: async (params = {}) => {
    const { status, page = 0, size = 20 } = params;
    
    const queryParams = new URLSearchParams({
      page: page.toString(),
      size: size.toString()
    });
    
    if (status) {
      queryParams.append('status', status);
    }
    
    const response = await api.get(`${API_BASE_URL}?${queryParams.toString()}`);
    return response;
  },

  getById: async (id) => {
    const response = await api.get(`${API_BASE_URL}/${id}`);
    return response;
  },

  create: async (loanApplicationData) => {
    const response = await api.post(API_BASE_URL, loanApplicationData);
    return response;
  },

  update: async (id, loanApplicationData) => {
    const response = await api.put(`${API_BASE_URL}/${id}`, loanApplicationData);
    return response;
  },

  decisionCheck: async (id) => {
    const response = await api.get(`${API_BASE_URL}/${id}/decision_check`);
    return response;
  },
};

export default loanApplicationApi;
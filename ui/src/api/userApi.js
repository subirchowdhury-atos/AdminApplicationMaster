import api from './axios';

const API_BASE_URL = '/api/v1/users';

export const userApi = {
  // Get all users
  getAll: async () => {
    const response = await api.get(API_BASE_URL);
    return response.data;
  },

  // Get a single user by ID
  getById: async (id) => {
    const response = await api.get(`${API_BASE_URL}/${id}`);
    return response.data;
  },

  // Create a new user
  create: async (userData) => {
    const response = await api.post(API_BASE_URL, userData);
    return response.data;
  },

  // Update an existing user
  update: async (id, userData) => {
    const response = await api.put(`${API_BASE_URL}/${id}`, userData);
    return response.data;
  },

  // Delete a user
  delete: async (id) => {
    const response = await api.delete(`${API_BASE_URL}/${id}`);
    return response.data;
  },
};

export default userApi;
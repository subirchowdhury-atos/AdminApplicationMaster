import api from './axios';

const API_BASE_URL = '/users';

export const authApi = {
  // Sign in
  signIn: async (email, password) => {
    const response = await api.post(`${API_BASE_URL}/sign_in`, {
      email,
      password,
    });
    
    // Store token in localStorage
    if (response.data.access_token) {
      localStorage.setItem('access_token', response.data.access_token);
    }
    
    return response.data;
  },

  // Sign out
  signOut: () => {
    localStorage.removeItem('access_token');
  },

  // Check if user is authenticated
  isAuthenticated: () => {
    return !!localStorage.getItem('access_token');
  },
};

export default authApi;
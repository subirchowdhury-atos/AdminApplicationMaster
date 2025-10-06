import api from './axios';

const API_BASE_URL = '/api/v1/location_services';

export const locationApi = {
  // Check if an address is eligible and create it if valid
  checkAddress: async (address) => {
    try {
      const response = await api.post(API_BASE_URL, { address });
      return response.data;
    } catch (error) {
      // Handle 404 - address not found or not eligible
      if (error.response?.status === 404) {
        const message = error.response.data?.message || 'Address not found';
        throw new Error(message);
      }
      
      // Handle other errors
      if (error.response) {
        const message = error.response.data?.message || 'Location service error';
        throw new Error(message);
      }
      
      // Network or other errors
      throw new Error('Unable to connect to location service');
    }
  },
};

export default locationApi;
import api from './axios';

const API_BASE_URL = '/api/v1/application_services';

export const loanApplicationApi = {
  // Get all loan applications with optional status filter
  getAll: async (statusSearch = null, page = 0) => {
  // For now, just get all without filters since the backend doesn't support filtering yet
  const response = await api.get(API_BASE_URL);
  return response;
},

  // Get a single loan application by ID
  getById: async (id) => {
    const response = await api.get(`${API_BASE_URL}/${id}`);
    return response.data;
  },

  // Create a new loan application
  create: async (loanApplicationData, addressId = null) => {
    const formData = new FormData();
    
    // Add loan application fields
    Object.keys(loanApplicationData).forEach(key => {
      if (loanApplicationData[key] !== null && loanApplicationData[key] !== undefined) {
        formData.append(key, loanApplicationData[key]);
      }
    });

    // Add address_id if provided
    if (addressId) {
      formData.append('address_id', addressId);
    }

    const response = await api.post(API_BASE_URL, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data;
  },

  // Update an existing loan application
  update: async (id, loanApplicationData) => {
    const formData = new FormData();
    
    Object.keys(loanApplicationData).forEach(key => {
      if (loanApplicationData[key] !== null && loanApplicationData[key] !== undefined) {
        formData.append(key, loanApplicationData[key]);
      }
    });

    const response = await api.post(`${API_BASE_URL}/${id}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data;
  },

  // Trigger decision check for a loan application
  decisionCheck: async (id) => {
    const response = await api.get(`${API_BASE_URL}/${id}/decision_check`);
    return response.data;
  },

  // Check if an address is eligible
  addressCheck: async (address) => {
    const formData = new FormData();
    formData.append('address', address);

    const response = await api.post(`${API_BASE_URL}/address_check`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data;
  },
};

export default loanApplicationApi;
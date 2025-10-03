const API_BASE_URL = '/api/v1/application_services';

export const applicationServiceApi = {
  // Get all loan applications
  getAll: async () => {
    const response = await fetch(API_BASE_URL);
    if (!response.ok) throw new Error('Failed to fetch loan applications');
    return response.json();
  },

  // Get a single loan application by ID
  getById: async (id) => {
    const response = await fetch(`${API_BASE_URL}/${id}`);
    if (!response.ok) throw new Error('Failed to fetch loan application');
    return response.json();
  },

  // Create a new loan application
  create: async (loanApplicationData) => {
    const response = await fetch(API_BASE_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(loanApplicationData),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.errors || 'Failed to create loan application');
    }
    return response.json();
  },

  // Update an existing loan application
  update: async (id, loanApplicationData) => {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(loanApplicationData),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.errors || 'Failed to update loan application');
    }
    return response.json();
  },

  // Partial update (PATCH)
  patch: async (id, loanApplicationData) => {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(loanApplicationData),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.errors || 'Failed to update loan application');
    }
    return response.json();
  },

  // Trigger decision check
  decisionCheck: async (id) => {
    const response = await fetch(`${API_BASE_URL}/${id}/decision_check`);
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to perform decision check');
    }
    return response.json();
  },
};

export default applicationServiceApi;
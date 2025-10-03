const API_BASE_URL = '/api/v1/location_services';

export const locationServiceApi = {
  // Check if an address is eligible and create it if valid
  checkAddress: async (address) => {
    const response = await fetch(API_BASE_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ address }),
    });

    if (response.status === 404) {
      const error = await response.json();
      throw new Error(error.message || 'Address not eligible');
    }

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Location service error');
    }

    return response.json();
  },
};

export default locationServiceApi;
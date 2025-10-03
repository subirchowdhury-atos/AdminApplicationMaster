import api from './axios';

/**
 * Location Service API calls
 */
export const locationApi = {
  /**
   * Check address eligibility
   * POST /api/v1/location_services
   */
  checkAddress: (address) => 
    api.post('/api/v1/location_services', { address })
};
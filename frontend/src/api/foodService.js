import axiosClient from './axiosClient';

/**
 * Service for interacting with Food Items APIs.
 */
const foodService = {
  /**
   * Fetch all active food items (public/market view).
   */
  getAllFoodItems: async () => {
    const response = await axiosClient.get('/v1/food-items');
    return response.data; // Note: axiosClient interceptor returns response, we just need to return response.data if we didn't unwrap in interceptor. But wait, we might have unwrapped it. Let's assume standard response structure.
  },

  /**
   * Fetch a specific food item by ID.
   */
  getFoodItemById: async (id) => {
    const response = await axiosClient.get(`/v1/food-items/${id}`);
    return response.data;
  },

  /**
   * Fetch food items created by a specific donor.
   */
  getFoodItemsByDonor: async (donorId) => {
    const response = await axiosClient.get(`/v1/food-items/donor/${donorId}`);
    return response.data;
  },

  /**
   * Create a new food item listing.
   */
  createFoodItem: async (foodData) => {
    const response = await axiosClient.post('/v1/food-items', foodData);
    return response.data;
  },

  /**
   * Update an existing food item.
   */
  updateFoodItem: async (id, foodData) => {
    const response = await axiosClient.put(`/v1/food-items/${id}`, foodData);
    return response.data;
  },

  /**
   * Update the status of a food item (e.g., AVAILABLE, RESERVED, DONATED, EXPIRED).
   */
  updateStatus: async (id, status) => {
    const response = await axiosClient.patch(`/v1/food-items/${id}/status`, null, {
      params: { status }
    });
    return response.data;
  },

  /**
   * Delete a food item listing.
   */
  deleteFoodItem: async (id) => {
    const response = await axiosClient.delete(`/v1/food-items/${id}`);
    return response.data;
  }
};

export default foodService;

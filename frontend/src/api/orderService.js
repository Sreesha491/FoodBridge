import axiosClient from './axiosClient';

/**
 * Service for interacting with Order APIs.
 */
const orderService = {
  /**
   * Fetch all orders in the system.
   */
  getAllOrders: async () => {
    const response = await axiosClient.get('/v1/orders');
    return response.data;
  },

  /**
   * Fetch a specific order by ID.
   */
  getOrderById: async (id) => {
    const response = await axiosClient.get(`/v1/orders/${id}`);
    return response.data;
  },

  /**
   * Fetch orders placed by a specific recipient (NGO/User).
   */
  getOrdersByRecipient: async (recipientId) => {
    const response = await axiosClient.get(`/v1/orders/recipient/${recipientId}`);
    return response.data;
  },

  /**
   * Fetch orders belonging to a specific donor/restaurant.
   */
  getOrdersByDonor: async (donorId) => {
    const response = await axiosClient.get(`/v1/orders/donor/${donorId}`);
    return response.data;
  },

  /**
   * Place a new order for a food item.
   */
  createOrder: async (orderData) => {
    const response = await axiosClient.post('/v1/orders', orderData);
    return response.data;
  },

  /**
   * Update order status (used by Delivery Partners / Donors).
   * @param {string} id - Order ID
   * @param {string} status - New status (e.g., CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED)
   * @param {string} deliveryPartnerId - Optional ID of the delivery partner picking it up
   */
  updateStatus: async (id, status, deliveryPartnerId = null) => {
    const params = { status };
    if (deliveryPartnerId) {
      params.deliveryPartnerId = deliveryPartnerId;
    }
    const response = await axiosClient.patch(`/v1/orders/${id}/status`, null, { params });
    return response.data;
  }
};

export default orderService;

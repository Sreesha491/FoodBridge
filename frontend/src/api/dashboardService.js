import foodService from './foodService';
import orderService from './orderService';

/**
 * Service for fetching aggregated dashboard statistics.
 *
 * In a real production environment with high scale, this should be a dedicated
 * backend endpoint (e.g., /v1/dashboard/stats) that runs optimized SQL COUNT() queries.
 * For this phase, we are aggregating the data on the client side using our existing APIs.
 */
const dashboardService = {
  /**
   * Fetch all stats needed for the dashboard overview.
   */
  getStats: async () => {
    try {
      // Run queries in parallel
      const [foodItemsRes, ordersRes] = await Promise.all([
        foodService.getAllFoodItems(),
        orderService.getAllOrders()
      ]);

      const foodItems = foodItemsRes.data || [];
      const orders = ordersRes.data || [];

      // Calculate aggregated metrics
      const totalFoodItems = foodItems.length;
      
      // Active donations could be food items that are AVAILABLE
      const activeDonations = foodItems.filter(f => f.status === 'AVAILABLE').length;

      const totalOrders = orders.length;

      // Deliveries done are orders marked as DELIVERED
      const deliveriesDone = orders.filter(o => o.status === 'DELIVERED').length;

      return {
        totalFoodItems,
        activeDonations,
        totalOrders,
        deliveriesDone
      };
    } catch (error) {
      console.error('Failed to fetch dashboard stats', error);
      throw error;
    }
  }
};

export default dashboardService;

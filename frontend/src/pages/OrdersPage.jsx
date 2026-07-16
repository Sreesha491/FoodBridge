import { useState, useEffect } from 'react';
import { useAuth } from '@/context/AuthContext';
import orderService from '@/api/orderService';

/**
 * Orders Page
 * - NGOs see their requested orders and status.
 * - Delivery Partners see orders they can pick up / have picked up.
 * - Donors see orders for their food.
 */
function OrdersPage() {
  const { user } = useAuth();
  const [orders, setOrders] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchOrders = async () => {
    setIsLoading(true);
    try {
      let res;
      if (user.role === 'NGO') {
        res = await orderService.getOrdersByRecipient(user.id); // Usually the backend uses recipientId
      } else if (user.role === 'DONOR' || user.role === 'RESTAURANT') {
        res = await orderService.getOrdersByDonor(user.id);
      } else if (user.role === 'DELIVERY_PARTNER') {
        // Delivery partners might want to see all pending/ready orders, or their own
        // For simplicity, fetch all orders, but in a real app, they'd have a specific queue
        res = await orderService.getAllOrders();
      } else {
        res = await orderService.getAllOrders();
      }
      setOrders(res.data || []);
      setError('');
    } catch (err) {
      console.error(err);
      setError('Failed to load orders.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, [user.id, user.role]);

  const handleUpdateStatus = async (orderId, newStatus) => {
    try {
      await orderService.updateStatus(orderId, newStatus, user.id);
      fetchOrders();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to update order status.');
    }
  };

  const renderActions = (order) => {
    if (user.role === 'DELIVERY_PARTNER') {
      if (order.status === 'CONFIRMED' || order.status === 'PREPARING') {
        return <button className="btn btn-sm btn-primary" onClick={() => handleUpdateStatus(order.id, 'OUT_FOR_DELIVERY')}>Pick Up for Delivery</button>;
      }
      if (order.status === 'OUT_FOR_DELIVERY' && order.deliveryPartnerId === user.id) {
        return <button className="btn btn-sm btn-success" onClick={() => handleUpdateStatus(order.id, 'DELIVERED')}>Mark Delivered</button>;
      }
    }
    
    if (user.role === 'DONOR' || user.role === 'RESTAURANT') {
      if (order.status === 'PENDING') {
        return <button className="btn btn-sm btn-primary" onClick={() => handleUpdateStatus(order.id, 'CONFIRMED')}>Confirm Order</button>;
      }
    }
    
    return null;
  };

  return (
    <div className="page-container">
      <header className="page-header">
        <div>
          <h1 className="page-title">Orders & Deliveries</h1>
          <p className="page-subtitle">Track the status of food donations.</p>
        </div>
      </header>

      {error && (
        <div className="alert alert-error" style={{ margin: '0 2rem' }}>
          <span className="alert-icon">⚠️</span> {error}
        </div>
      )}

      <main className="orders-content">
        {isLoading ? (
          <div className="loading-state">Loading orders...</div>
        ) : orders.length === 0 ? (
          <div className="empty-state">No orders found.</div>
        ) : (
          <div className="table-container">
            <table className="orders-table">
              <thead>
                <tr>
                  <th>Order ID</th>
                  <th>Date</th>
                  <th>Destination</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {orders.map(order => (
                  <tr key={order.id}>
                    <td><span className="mono">{order.id.split('-')[0]}...</span></td>
                    <td>{new Date(order.createdAt).toLocaleDateString()}</td>
                    <td>{order.deliveryAddress || 'N/A'}</td>
                    <td>
                      <span className={`status-badge status-${order.status.toLowerCase()}`}>
                        {order.status}
                      </span>
                    </td>
                    <td>
                      {renderActions(order)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </main>

      <style>{`
        .page-container {
          min-height: 100vh;
          background: #0d0d1a;
          color: #fff;
          font-family: 'Inter', sans-serif;
          padding-bottom: 3rem;
        }
        .page-header {
          display: flex;
          align-items: flex-end;
          justify-content: space-between;
          padding: 2rem 2rem 1.5rem;
          border-bottom: 1px solid rgba(255,255,255,0.08);
          margin-bottom: 2rem;
        }
        .page-title {
          font-size: 2rem; margin: 0 0 0.25rem; font-weight: 800;
          background: linear-gradient(90deg, #fff, rgba(255,255,255,0.7));
          -webkit-background-clip: text; -webkit-text-fill-color: transparent;
        }
        .page-subtitle { color: rgba(255,255,255,0.5); margin: 0; font-size: 0.95rem; }
        
        .orders-content {
          padding: 0 2rem;
        }
        .table-container {
          background: rgba(255,255,255,0.04);
          border: 1px solid rgba(255,255,255,0.08);
          border-radius: 1rem;
          overflow: hidden;
        }
        .orders-table {
          width: 100%;
          border-collapse: collapse;
          text-align: left;
        }
        .orders-table th {
          background: rgba(0,0,0,0.2);
          padding: 1rem 1.5rem;
          font-weight: 600;
          color: rgba(255,255,255,0.7);
          font-size: 0.85rem;
          text-transform: uppercase;
          letter-spacing: 0.05em;
          border-bottom: 1px solid rgba(255,255,255,0.08);
        }
        .orders-table td {
          padding: 1rem 1.5rem;
          border-bottom: 1px solid rgba(255,255,255,0.04);
          font-size: 0.95rem;
          vertical-align: middle;
        }
        .orders-table tr:last-child td { border-bottom: none; }
        .orders-table tr:hover { background: rgba(255,255,255,0.02); }
        
        .mono { font-family: monospace; color: rgba(255,255,255,0.5); }
        
        .status-badge {
          display: inline-block;
          padding: 0.25rem 0.75rem;
          border-radius: 1rem;
          font-size: 0.75rem;
          font-weight: 600;
          letter-spacing: 0.05em;
        }
        .status-pending { background: rgba(251,191,36,0.15); color: #fbbf24; border: 1px solid rgba(251,191,36,0.3); }
        .status-confirmed, .status-preparing { background: rgba(96,165,250,0.15); color: #60a5fa; border: 1px solid rgba(96,165,250,0.3); }
        .status-out_for_delivery { background: rgba(167,139,250,0.15); color: #a78bfa; border: 1px solid rgba(167,139,250,0.3); }
        .status-delivered { background: rgba(74,222,128,0.15); color: #4ade80; border: 1px solid rgba(74,222,128,0.3); }
        .status-cancelled { background: rgba(248,113,113,0.15); color: #f87171; border: 1px solid rgba(248,113,113,0.3); }

        .btn-sm { padding: 0.4rem 0.75rem; font-size: 0.8rem; border-radius: 0.5rem; }
        .btn-success { background: #16a34a; color: white; border: none; cursor: pointer; }
        .btn-success:hover { background: #15803d; }
        
        .empty-state, .loading-state {
          text-align: center; padding: 4rem;
          color: rgba(255,255,255,0.4); font-size: 1.1rem;
        }
      `}</style>
    </div>
  );
}

export default OrdersPage;

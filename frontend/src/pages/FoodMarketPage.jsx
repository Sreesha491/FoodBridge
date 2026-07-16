import { useState, useEffect } from 'react';
import { useAuth } from '@/context/AuthContext';
import foodService from '@/api/foodService';
import orderService from '@/api/orderService';
import CreateFoodModal from '@/components/CreateFoodModal';

/**
 * Food Market Page
 * - Donors/Restaurants can add surplus food.
 * - NGOs can request food (creates an order).
 */
function FoodMarketPage() {
  const { user } = useAuth();
  const [foodItems, setFoodItems] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [error, setError] = useState('');
  
  // Only NGOs can request. Donors/Restaurants can list.
  const canListFood = user?.role === 'DONOR' || user?.role === 'RESTAURANT';
  const canRequestFood = user?.role === 'NGO';

  const fetchFood = async () => {
    setIsLoading(true);
    try {
      // For now, fetch all active food items in the market
      const res = await foodService.getAllFoodItems();
      // Filter out items that are not available if needed, but the backend might return all
      // Let's just show AVAILABLE ones, unless the user owns them.
      let items = res.data || [];
      if (canRequestFood) {
        items = items.filter(f => f.status === 'AVAILABLE');
      }
      setFoodItems(items);
      setError('');
    } catch (err) {
      console.error(err);
      setError('Failed to load food market data.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchFood();
  }, []);

  const handleRequestFood = async (foodItem) => {
    if (!window.confirm(`Request ${foodItem.name}?`)) return;
    
    try {
      await orderService.createOrder({
        donorId: foodItem.donorId,
        items: [{
          foodItemId: foodItem.id,
          quantity: foodItem.quantity,
          unitPrice: 0 // Free donation
        }],
        totalAmount: 0,
        deliveryAddress: user.address || 'Default NGO Address'
      });
      alert('Food requested successfully! Check your Orders tab.');
      fetchFood();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to request food.');
    }
  };

  return (
    <div className="page-container">
      <header className="page-header">
        <div>
          <h1 className="page-title">Surplus Food Market</h1>
          <p className="page-subtitle">Connect surplus food with those in need.</p>
        </div>
        {canListFood && (
          <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>
            + List Food
          </button>
        )}
      </header>

      {error && (
        <div className="alert alert-error" style={{ margin: '0 2rem' }}>
          <span className="alert-icon">⚠️</span> {error}
        </div>
      )}

      <main className="market-grid">
        {isLoading ? (
          <div className="loading-state">Loading food market...</div>
        ) : foodItems.length === 0 ? (
          <div className="empty-state">No food items available at the moment.</div>
        ) : (
          foodItems.map(item => (
            <div key={item.id} className="food-card">
              <div className="food-badge">{item.category}</div>
              <h3 className="food-name">{item.name}</h3>
              <p className="food-desc">{item.description || 'No description provided.'}</p>
              
              <div className="food-meta">
                <span className="meta-item">
                  <strong>Qty:</strong> {item.quantity} {item.quantityUnit}
                </span>
                <span className="meta-item">
                  <strong>Expires:</strong> {new Date(item.expiryTime).toLocaleString()}
                </span>
                <span className="meta-item">
                  <strong>Status:</strong> <span className={`status-${item.status.toLowerCase()}`}>{item.status}</span>
                </span>
              </div>

              {canRequestFood && item.status === 'AVAILABLE' && (
                <button 
                  className="btn btn-primary btn-full mt-auto"
                  onClick={() => handleRequestFood(item)}
                >
                  Request Donation
                </button>
              )}
            </div>
          ))
        )}
      </main>

      <CreateFoodModal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        onSuccess={() => {
          setIsModalOpen(false);
          fetchFood();
        }}
      />

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
        
        .market-grid {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
          gap: 1.5rem;
          padding: 0 2rem;
        }
        .food-card {
          background: rgba(255,255,255,0.04);
          border: 1px solid rgba(255,255,255,0.08);
          border-radius: 1rem;
          padding: 1.5rem;
          display: flex; flex-direction: column; gap: 0.75rem;
          transition: transform 0.2s, border-color 0.2s;
          position: relative;
        }
        .food-card:hover {
          transform: translateY(-3px);
          border-color: rgba(249,115,22,0.3);
          background: rgba(255,255,255,0.06);
        }
        .food-badge {
          position: absolute; top: 1rem; right: 1rem;
          background: rgba(249,115,22,0.15); color: #fb923c;
          padding: 0.2rem 0.6rem; border-radius: 1rem;
          font-size: 0.7rem; font-weight: 600; letter-spacing: 0.05em;
        }
        .food-name { margin: 0; font-size: 1.2rem; font-weight: 700; padding-right: 4rem; }
        .food-desc { margin: 0; color: rgba(255,255,255,0.6); font-size: 0.85rem; line-height: 1.4; }
        
        .food-meta {
          background: rgba(0,0,0,0.2);
          padding: 0.75rem; border-radius: 0.5rem;
          display: flex; flex-direction: column; gap: 0.4rem;
          font-size: 0.8rem; color: rgba(255,255,255,0.8);
          margin: 0.5rem 0;
        }
        .meta-item strong { color: rgba(255,255,255,0.5); font-weight: 500; width: 60px; display: inline-block; }
        
        .status-available { color: #4ade80; font-weight: 600; }
        .status-reserved { color: #fbbf24; font-weight: 600; }
        .status-donated { color: #60a5fa; font-weight: 600; }
        .status-expired { color: #f87171; font-weight: 600; }
        
        .mt-auto { margin-top: auto; }
        .btn-full { width: 100%; }
        .empty-state, .loading-state {
          grid-column: 1 / -1; text-align: center; padding: 4rem;
          color: rgba(255,255,255,0.4); font-size: 1.1rem;
        }
      `}</style>
    </div>
  );
}

export default FoodMarketPage;

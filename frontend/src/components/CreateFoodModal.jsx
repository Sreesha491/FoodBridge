import { useState } from 'react';
import foodService from '@/api/foodService';

/**
 * Modal component for creating a new food item.
 * @param {boolean} isOpen - Whether the modal is visible
 * @param {function} onClose - Callback to close the modal
 * @param {function} onSuccess - Callback when food is successfully created
 */
function CreateFoodModal({ isOpen, onClose, onSuccess }) {
  const [form, setForm] = useState({
    name: '',
    description: '',
    quantity: '',
    unit: 'KG', // KG, PORTIONS, LITERS, BOXES
    category: 'COOKED_FOOD', // COOKED_FOOD, RAW_INGREDIENTS, PACKAGED_GOODS, BAKERY, PRODUCE
    expiryDate: '' // datetime-local value e.g. '2023-12-01T15:30'
  });
  
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  if (!isOpen) return null;

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    // Basic validation
    if (!form.name || !form.quantity || !form.expiryTime) {
      setError('Please fill in all required fields.');
      return;
    }
    
    // Build payload matching FoodItemRequest DTO exactly
    // - unit:       maps to 'unit' field
    // - expiryDate: must be ISO Instant (append Z for UTC)
    const expiryISO = form.expiryDate
      ? new Date(form.expiryDate).toISOString()
      : null;

    const payload = {
      name: form.name,
      description: form.description,
      category: form.category,
      quantity: parseFloat(form.quantity),
      unit: form.unit,
      expiryDate: expiryISO,
    };

    setIsLoading(true);
    try {
      await foodService.createFoodItem(payload);
      onSuccess(); // Triggers a reload of food items in parent component
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to create food listing. Please try again.';
      setError(msg);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-container" role="dialog" aria-modal="true" aria-labelledby="modal-title">
        
        <div className="modal-header">
          <h2 id="modal-title">List Surplus Food</h2>
          <button className="btn-close" onClick={onClose} aria-label="Close modal">✕</button>
        </div>

        <div className="modal-body">
          {error && (
            <div className="alert alert-error">
              <span className="alert-icon">⚠️</span>
              {error}
            </div>
          )}

          <form id="create-food-form" onSubmit={handleSubmit} className="food-form">
            <div className="form-group">
              <label htmlFor="food-name" className="form-label">Food Name *</label>
              <input
                id="food-name" name="name" type="text" required
                value={form.name} onChange={handleChange} className="form-input"
                placeholder="e.g. 50 Servings of Fried Rice"
              />
            </div>

            <div className="form-group">
              <label htmlFor="food-desc" className="form-label">Description</label>
              <textarea
                id="food-desc" name="description" rows="3"
                value={form.description} onChange={handleChange} className="form-input"
                placeholder="Any specific details about the food..."
              />
            </div>

            <div className="form-row">
              <div className="form-group" style={{ flex: 2 }}>
                <label htmlFor="food-qty" className="form-label">Quantity *</label>
                <input
                  id="food-qty" name="quantity" type="number" step="0.1" min="0.1" required
                  value={form.quantity} onChange={handleChange} className="form-input"
                  placeholder="e.g. 5.5"
                />
              </div>
              <div className="form-group" style={{ flex: 1 }}>
                <label htmlFor="food-unit" className="form-label">Unit *</label>
                              <select id="food-unit" name="unit" value={form.unit} onChange={handleChange} className="form-input select-input">
                  <option value="KG">KG</option>
                  <option value="PORTIONS">Portions</option>
                  <option value="LITERS">Liters</option>
                  <option value="BOXES">Boxes</option>
                </select>
              </div>
            </div>

            <div className="form-row">
              <div className="form-group" style={{ flex: 1 }}>
                <label htmlFor="food-cat" className="form-label">Category *</label>
                <select id="food-cat" name="category" value={form.category} onChange={handleChange} className="form-input select-input">
                  <option value="COOKED_FOOD">Cooked Food</option>
                  <option value="RAW_INGREDIENTS">Raw Ingredients</option>
                  <option value="PACKAGED_GOODS">Packaged Goods</option>
                  <option value="BAKERY">Bakery</option>
                  <option value="PRODUCE">Fresh Produce</option>
                </select>
              </div>
              <div className="form-group" style={{ flex: 1 }}>
                <label htmlFor="food-expiry" className="form-label">Expiry Time *</label>
                                <input
                  id="food-expiry" name="expiryDate" type="datetime-local" required
                  value={form.expiryDate} onChange={handleChange} className="form-input"
                />
              </div>
            </div>
          </form>
        </div>

        <div className="modal-footer">
          <button type="button" className="btn btn-outline" onClick={onClose} disabled={isLoading}>
            Cancel
          </button>
          <button type="submit" form="create-food-form" className="btn btn-primary" disabled={isLoading}>
            {isLoading ? <span className="spinner" /> : 'Publish Listing'}
          </button>
        </div>
      </div>

      <style>{`
        .modal-overlay {
          position: fixed;
          top: 0; left: 0; right: 0; bottom: 0;
          background: rgba(0,0,0,0.6);
          backdrop-filter: blur(8px);
          -webkit-backdrop-filter: blur(8px);
          display: flex;
          align-items: center;
          justify-content: center;
          z-index: 9999;
          padding: 1rem;
        }
        .modal-container {
          background: #1e1e2d;
          border: 1px solid rgba(255,255,255,0.1);
          border-radius: 1.25rem;
          width: 100%;
          max-width: 500px;
          box-shadow: 0 25px 50px rgba(0,0,0,0.5);
          display: flex;
          flex-direction: column;
          max-height: 90vh;
          overflow-y: auto;
        }
        .modal-header {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 1.5rem;
          border-bottom: 1px solid rgba(255,255,255,0.08);
        }
        .modal-header h2 {
          margin: 0;
          font-size: 1.25rem;
          color: #fff;
          font-weight: 700;
        }
        .btn-close {
          background: none; border: none;
          color: rgba(255,255,255,0.5);
          font-size: 1.25rem;
          cursor: pointer;
          transition: color 0.2s;
        }
        .btn-close:hover { color: #fff; }
        .modal-body {
          padding: 1.5rem;
        }
        .food-form {
          display: flex; flex-direction: column; gap: 1rem;
        }
        .form-row {
          display: flex; gap: 1rem;
        }
        .form-group {
          display: flex; flex-direction: column; gap: 0.4rem;
        }
        .form-label {
          color: rgba(255,255,255,0.7);
          font-size: 0.85rem;
          font-weight: 500;
        }
        .form-input {
          width: 100%;
          padding: 0.75rem 1rem;
          background: rgba(0,0,0,0.2);
          border: 1px solid rgba(255,255,255,0.1);
          border-radius: 0.625rem;
          color: #fff;
          font-size: 0.95rem;
          transition: all 0.2s;
          box-sizing: border-box;
          font-family: inherit;
        }
        .form-input:focus {
          outline: none;
          border-color: #f97316;
          box-shadow: 0 0 0 3px rgba(249,115,22,0.15);
        }
        .select-input {
          appearance: none;
          background-image: url("data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%23ffffff%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E");
          background-repeat: no-repeat;
          background-position: right 0.75rem top 50%;
          background-size: 0.65rem auto;
          padding-right: 2rem;
        }
        .select-input option {
          background: #1e1e2d;
          color: #fff;
        }
        .modal-footer {
          padding: 1.25rem 1.5rem;
          border-top: 1px solid rgba(255,255,255,0.08);
          display: flex;
          justify-content: flex-end;
          gap: 1rem;
        }
        .alert-error {
          background: rgba(239,68,68,0.15);
          border: 1px solid rgba(239,68,68,0.3);
          color: #fca5a5;
          padding: 0.75rem 1rem;
          border-radius: 0.625rem;
          font-size: 0.875rem;
          margin-bottom: 1rem;
          display: flex; align-items: center; gap: 0.5rem;
        }
      `}</style>
    </div>
  );
}

export default CreateFoodModal;

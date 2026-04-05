import { useState, useEffect } from 'react'
import { useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { getRestaurant, addToCart } from '../api/client'

export default function RestaurantDetail() {
  const { id } = useParams()
  const { token } = useAuth()
  const [restaurant, setRestaurant] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [added, setAdded] = useState(null)

  useEffect(() => {
    getRestaurant(id)
      .then(setRestaurant)
      .catch(err => setError(err.message))
      .finally(() => setLoading(false))
  }, [id])

  const handleAddToCart = async (menuItemId, qty = 1) => {
    if (!token) return
    try {
      await addToCart(menuItemId, qty)
      setAdded(menuItemId)
      setTimeout(() => setAdded(null), 2000)
    } catch (err) {
      alert(err.message)
    }
  }

  if (loading) return <div className="text-center py-5">Loading...</div>
  if (error) return <div className="alert alert-danger">{error}</div>
  if (!restaurant) return null

  const menuItems = restaurant.menuItems || []

  return (
    <div>
      <div className="card mb-4">
        <div className="card-body">
          <h2>{restaurant.name}</h2>
          <p className="text-muted">{restaurant.description}</p>
          <p>{restaurant.address}, {restaurant.city}</p>
          {restaurant.status !== 'APPROVED' && (
            <span className="badge bg-warning">{restaurant.status}</span>
          )}
        </div>
      </div>
      <h4 className="mb-3">Menu</h4>
      <div className="row g-3">
        {menuItems.filter(m => m.available).map(item => (
          <div key={item.id} className="col-md-6">
            <div className="card h-100">
              <div className="card-body d-flex justify-content-between align-items-center">
                <div>
                  <h6 className="card-title">{item.name}</h6>
                  <p className="card-text small text-muted mb-0">{item.description}</p>
                  <span className="fw-bold">₹{item.price}</span>
                </div>
                {token && (
                  <button
                    className="btn btn-primary btn-sm"
                    onClick={() => handleAddToCart(item.id)}
                    disabled={added === item.id}
                  >
                    {added === item.id ? 'Added' : 'Add to cart'}
                  </button>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>
      {menuItems.length === 0 && <p className="text-muted">No menu items.</p>}
    </div>
  )
}

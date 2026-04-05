import { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import { getRestaurants, getRestaurantOrders, updateOrderStatus, addMenuItem, updateMenuItem, deleteMenuItem } from '../api/client'

const ORDER_STATUSES = ['CONFIRMED', 'PREPARING', 'READY', 'OUT_FOR_DELIVERY', 'DELIVERED']

export default function RestaurantDashboard() {
  const { user } = useAuth()
  const [restaurants, setRestaurants] = useState([])
  const [selectedRestaurant, setSelectedRestaurant] = useState(null)
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    getRestaurants({ ownerId: user?.userId })
      .then(data => {
        const list = Array.isArray(data) ? data : []
        setRestaurants(list)
        if (list.length && !selectedRestaurant) setSelectedRestaurant(list[0])
      })
      .catch(err => setError(err.message))
      .finally(() => setLoading(false))
  }, [user?.userId])

  useEffect(() => {
    if (!selectedRestaurant?.id) return
    getRestaurantOrders(selectedRestaurant.id)
      .then(data => setOrders(Array.isArray(data) ? data : []))
      .catch(() => setOrders([]))
  }, [selectedRestaurant?.id])

  const refreshOrders = () => {
    if (selectedRestaurant?.id) {
      getRestaurantOrders(selectedRestaurant.id).then(data => setOrders(Array.isArray(data) ? data : []))
    }
  }

  const handleStatusChange = async (orderId, status) => {
    try {
      await updateOrderStatus(orderId, status, selectedRestaurant.id)
      refreshOrders()
    } catch (err) {
      alert(err.message)
    }
  }

  if (loading) return <div className="text-center py-5">Loading...</div>
  if (error) return <div className="alert alert-danger">{error}</div>
  if (restaurants.length === 0) {
    return (
      <div className="alert alert-info">
        You have no restaurant. Register one from the API or add a flow to create restaurant.
      </div>
    )
  }

  return (
    <div>
      <h2 className="mb-4">My Restaurant</h2>
      <div className="mb-3">
        <label className="form-label">Restaurant</label>
        <select
          className="form-select w-auto"
          value={selectedRestaurant?.id || ''}
          onChange={e => setSelectedRestaurant(restaurants.find(r => r.id === Number(e.target.value)) || null)}
        >
          {restaurants.map(r => (
            <option key={r.id} value={r.id}>{r.name} ({r.status})</option>
          ))}
        </select>
      </div>
      <h5 className="mt-4">Orders</h5>
      {orders.length === 0 ? (
        <p className="text-muted">No orders.</p>
      ) : (
        <div className="list-group">
          {orders.map(o => (
            <div key={o.id} className="list-group-item">
              <div className="d-flex justify-content-between">
                <strong>{o.orderNumber}</strong>
                <span className="badge bg-secondary">{o.status}</span>
              </div>
              <p className="mb-1 small">₹{o.totalAmount} · {o.deliveryAddress}</p>
              {ORDER_STATUSES.includes(o.status) && o.status !== 'DELIVERED' && (
                <div className="mt-2">
                  <span className="me-2">Update status:</span>
                  {ORDER_STATUSES.map(s => (
                    <button key={s} className="btn btn-outline-primary btn-sm me-1" onClick={() => handleStatusChange(o.id, s)} disabled={s === o.status}>
                      {s}
                    </button>
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

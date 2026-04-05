import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { getMyOrders } from '../api/client'

const STATUS_LABELS = {
  PENDING: 'Pending',
  CONFIRMED: 'Confirmed',
  PREPARING: 'Preparing',
  READY: 'Ready',
  OUT_FOR_DELIVERY: 'Out for delivery',
  DELIVERED: 'Delivered',
  CANCELLED: 'Cancelled',
}

export default function MyOrders() {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    getMyOrders()
      .then(data => setOrders(Array.isArray(data) ? data : []))
      .catch(err => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="text-center py-5">Loading...</div>
  if (error) return <div className="alert alert-danger">{error}</div>

  return (
    <div>
      <h2 className="mb-4">My Orders</h2>
      {orders.length === 0 ? (
        <p className="text-muted">No orders yet.</p>
      ) : (
        <div className="list-group">
          {orders.map(o => (
            <Link key={o.id} to={`/orders/${o.id}`} className="list-group-item list-group-item-action">
              <div className="d-flex w-100 justify-content-between">
                <h6 className="mb-1">{o.orderNumber}</h6>
                <span className="badge bg-secondary">{STATUS_LABELS[o.status] || o.status}</span>
              </div>
              <p className="mb-1 small text-muted">₹{o.totalAmount} · {o.deliveryAddress}</p>
              <small>{new Date(o.createdAt).toLocaleString()}</small>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}

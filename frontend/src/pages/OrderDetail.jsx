import { useState, useEffect } from 'react'
import { useParams } from 'react-router-dom'
import { getOrder } from '../api/client'

const STATUS_LABELS = {
  PENDING: 'Pending',
  CONFIRMED: 'Confirmed',
  PREPARING: 'Preparing',
  READY: 'Ready',
  OUT_FOR_DELIVERY: 'Out for delivery',
  DELIVERED: 'Delivered',
  CANCELLED: 'Cancelled',
}

export default function OrderDetail() {
  const { id } = useParams()
  const [order, setOrder] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    getOrder(id)
      .then(setOrder)
      .catch(err => setError(err.message))
      .finally(() => setLoading(false))
  }, [id])

  if (loading) return <div className="text-center py-5">Loading...</div>
  if (error) return <div className="alert alert-danger">{error}</div>
  if (!order) return null

  return (
    <div>
      <h2 className="mb-4">Order {order.orderNumber}</h2>
      <div className="card mb-3">
        <div className="card-body">
          <p><strong>Status:</strong> <span className="badge bg-primary">{STATUS_LABELS[order.status] || order.status}</span></p>
          <p><strong>Total:</strong> ₹{order.totalAmount}</p>
          <p><strong>Delivery address:</strong> {order.deliveryAddress}</p>
          <p className="text-muted small">{new Date(order.createdAt).toLocaleString()}</p>
        </div>
      </div>
      <h5>Items</h5>
      <ul className="list-group">
        {order.items?.map((item, i) => (
          <li key={i} className="list-group-item d-flex justify-content-between">
            <span>{item.itemName} × {item.quantity}</span>
            <span>₹{item.subtotal}</span>
          </li>
        ))}
      </ul>
    </div>
  )
}

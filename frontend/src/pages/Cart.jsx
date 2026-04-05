import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { getCart, updateCartItem, clearCart, placeOrder, processPayment } from '../api/client'

export default function Cart() {
  const navigate = useNavigate()
  const [cart, setCart] = useState(null)
  const [address, setAddress] = useState('')
  const [loading, setLoading] = useState(true)
  const [placing, setPlacing] = useState(false)
  const [error, setError] = useState('')

  const loadCart = () => {
    getCart()
      .then(setCart)
      .catch(err => setError(err.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadCart()
  }, [])

  const handleUpdateQty = async (cartItemId, quantity) => {
    try {
      const updated = await updateCartItem(cartItemId, quantity)
      setCart(updated)
    } catch (err) {
      alert(err.message)
    }
  }

  const handleClear = async () => {
    if (!confirm('Clear cart?')) return
    try {
      await clearCart()
      setCart(null)
    } catch (err) {
      alert(err.message)
    }
  }

  const handlePlaceOrder = async () => {
    if (!address.trim()) {
      setError('Enter delivery address')
      return
    }
    setError('')
    setPlacing(true)
    try {
      const order = await placeOrder(address.trim())
      await processPayment(order.id, order.totalAmount)
      await clearCart()
      navigate(`/orders/${order.id}`)
    } catch (err) {
      setError(err.message || 'Order failed')
    } finally {
      setPlacing(false)
    }
  }

  if (loading) return <div className="text-center py-5">Loading...</div>
  if (error && !placing) return <div className="alert alert-danger">{error}</div>

  if (!cart || !cart.items?.length) {
    return (
      <div className="text-center py-5">
        <p className="text-muted">Your cart is empty.</p>
        <a href="/restaurants" className="btn btn-primary">Browse restaurants</a>
      </div>
    )
  }

  return (
    <div>
      <h2 className="mb-4">Cart</h2>
      <div className="row">
        <div className="col-lg-8">
          {cart.items.map(item => (
            <div key={item.cartItemId} className="card mb-2">
              <div className="card-body d-flex justify-content-between align-items-center">
                <div>
                  <h6>{item.itemName}</h6>
                  <span className="text-muted">₹{item.unitPrice} × {item.quantity}</span>
                </div>
                <div className="d-flex align-items-center gap-2">
                  <button className="btn btn-outline-secondary btn-sm" onClick={() => handleUpdateQty(item.cartItemId, item.quantity - 1)} disabled={item.quantity <= 1}>−</button>
                  <span>{item.quantity}</span>
                  <button className="btn btn-outline-secondary btn-sm" onClick={() => handleUpdateQty(item.cartItemId, item.quantity + 1)}>+</button>
                </div>
              </div>
            </div>
          ))}
          <button className="btn btn-outline-danger btn-sm mt-2" onClick={handleClear}>Clear cart</button>
        </div>
        <div className="col-lg-4">
          <div className="card">
            <div className="card-body">
              <h5>Total: ₹{cart.totalAmount}</h5>
              <div className="mb-3">
                <label className="form-label">Delivery address</label>
                <input type="text" className="form-control" value={address} onChange={e => setAddress(e.target.value)} placeholder="Enter address" />
              </div>
              {error && placing && <div className="alert alert-danger small">{error}</div>}
              <button className="btn btn-primary w-100" onClick={handlePlaceOrder} disabled={placing}>
                {placing ? 'Placing order...' : 'Place order & Pay'}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

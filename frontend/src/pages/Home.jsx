import { Link } from 'react-router-dom'

export default function Home() {
  return (
    <div className="text-center py-5">
      <h1 className="display-4 fw-bold mb-3" style={{ color: 'var(--eatease-primary)' }}>EatEase</h1>
      <p className="lead text-muted mb-4">Order food from your favourite restaurants. Delivered to your door.</p>
      <Link to="/restaurants" className="btn btn-primary btn-lg px-4">Browse Restaurants</Link>
    </div>
  )
}

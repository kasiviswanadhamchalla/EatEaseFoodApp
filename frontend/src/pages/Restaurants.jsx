import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { getRestaurants } from '../api/client'

export default function Restaurants() {
  const [restaurants, setRestaurants] = useState([])
  const [city, setCity] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let cancelled = false
    const params = city ? { city } : {}
    getRestaurants(params)
      .then(data => { if (!cancelled) setRestaurants(Array.isArray(data) ? data : []) })
      .catch(err => { if (!cancelled) setError(err.message) })
      .finally(() => { if (!cancelled) setLoading(false) })
    return () => { cancelled = true }
  }, [city])

  if (loading) return <div className="text-center py-5">Loading...</div>
  if (error) return <div className="alert alert-danger">{error}</div>

  return (
    <div>
      <h2 className="mb-4">Restaurants</h2>
      <div className="mb-3">
        <input
          type="text"
          className="form-control w-50"
          placeholder="Filter by city"
          value={city}
          onChange={e => setCity(e.target.value)}
        />
      </div>
      <div className="row g-4">
        {restaurants.map(r => (
          <div key={r.id} className="col-md-6 col-lg-4">
            <Link to={`/restaurants/${r.id}`} className="text-decoration-none text-dark">
              <div className="card h-100">
                {r.imageUrl && <img src={r.imageUrl} className="card-img-top" alt={r.name} />}
                <div className="card-body">
                  <h5 className="card-title">{r.name}</h5>
                  <p className="card-text text-muted small">{r.description || r.address}</p>
                  {r.city && <span className="badge bg-light text-dark">{r.city}</span>}
                  {r.status === 'APPROVED' && <span className="badge bg-success ms-1">Open</span>}
                </div>
              </div>
            </Link>
          </div>
        ))}
      </div>
      {restaurants.length === 0 && <p className="text-muted">No restaurants found.</p>}
    </div>
  )
}

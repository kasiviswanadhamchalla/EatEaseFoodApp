import { useState, useEffect } from 'react'
import { getUsers, getRestaurants, approveRestaurant } from '../api/client'

export default function Admin() {
  const [users, setUsers] = useState([])
  const [restaurants, setRestaurants] = useState([])
  const [tab, setTab] = useState('users')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = () => {
    setLoading(true)
    Promise.all([getUsers(), getRestaurants()])
      .then(([u, r]) => {
        setUsers(Array.isArray(u) ? u : [])
        setRestaurants(Array.isArray(r) ? r : [])
      })
      .catch(err => setError(err.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    load()
  }, [])

  const handleApprove = async (id, status) => {
    try {
      await approveRestaurant(id, status)
      load()
    } catch (err) {
      alert(err.message)
    }
  }

  if (loading) return <div className="text-center py-5">Loading...</div>
  if (error) return <div className="alert alert-danger">{error}</div>

  const pendingRestaurants = restaurants.filter(r => r.status === 'PENDING_APPROVAL')

  return (
    <div>
      <h2 className="mb-4">Admin</h2>
      <ul className="nav nav-tabs mb-3">
        <li className="nav-item">
          <button className={`nav-link ${tab === 'users' ? 'active' : ''}`} onClick={() => setTab('users')}>Users</button>
        </li>
        <li className="nav-item">
          <button className={`nav-link ${tab === 'restaurants' ? 'active' : ''}`} onClick={() => setTab('restaurants')}>Restaurants</button>
        </li>
      </ul>
      {tab === 'users' && (
        <div className="table-responsive">
          <table className="table">
            <thead>
              <tr><th>ID</th><th>Name</th><th>Email</th><th>Roles</th><th>Enabled</th></tr>
            </thead>
            <tbody>
              {users.map(u => (
                <tr key={u.id}>
                  <td>{u.id}</td>
                  <td>{u.name}</td>
                  <td>{u.email}</td>
                  <td>{u.roles?.join(', ')}</td>
                  <td>{u.enabled ? 'Yes' : 'No'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      {tab === 'restaurants' && (
        <div>
          <p className="text-muted">Restaurant registrations (approve/reject):</p>
          <div className="list-group">
            {restaurants.map(r => (
              <div key={r.id} className="list-group-item d-flex justify-content-between align-items-center">
                <div>
                  <strong>{r.name}</strong> — {r.address}, {r.city} <span className="badge bg-secondary">{r.status}</span>
                </div>
                {r.status === 'PENDING_APPROVAL' && (
                  <div>
                    <button className="btn btn-success btn-sm me-1" onClick={() => handleApprove(r.id, 'APPROVED')}>Approve</button>
                    <button className="btn btn-danger btn-sm" onClick={() => handleApprove(r.id, 'REJECTED')}>Reject</button>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

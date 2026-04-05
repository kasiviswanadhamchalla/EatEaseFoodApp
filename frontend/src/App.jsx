import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import Layout from './components/Layout'
import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import Restaurants from './pages/Restaurants'
import RestaurantDetail from './pages/RestaurantDetail'
import Cart from './pages/Cart'
import MyOrders from './pages/MyOrders'
import OrderDetail from './pages/OrderDetail'
import Admin from './pages/Admin'
import RestaurantDashboard from './pages/RestaurantDashboard'

function PrivateRoute({ children, roles }) {
  const { user, token } = useAuth()
  if (!token) return <Navigate to="/login" replace />
  if (roles && user?.roles && !roles.some(r => user.roles.includes(r))) {
    return <Navigate to="/" replace />
  }
  return children
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route index element={<Home />} />
        <Route path="login" element={<Login />} />
        <Route path="register" element={<Register />} />
        <Route path="restaurants" element={<Restaurants />} />
        <Route path="restaurants/:id" element={<RestaurantDetail />} />
        <Route path="cart" element={<PrivateRoute><Cart /></PrivateRoute>} />
        <Route path="orders" element={<PrivateRoute><MyOrders /></PrivateRoute>} />
        <Route path="orders/:id" element={<PrivateRoute><OrderDetail /></PrivateRoute>} />
        <Route path="admin" element={<PrivateRoute roles={['ADMIN']}><Admin /></PrivateRoute>} />
        <Route path="restaurant-dashboard" element={<PrivateRoute roles={['RESTAURANT_OWNER']}><RestaurantDashboard /></PrivateRoute>} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

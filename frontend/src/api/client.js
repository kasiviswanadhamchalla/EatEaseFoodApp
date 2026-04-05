const API_BASE = '/api'

function getToken() {
  return localStorage.getItem('eatease_token')
}

function getUserId() {
  try {
    const u = localStorage.getItem('eatease_user')
    return u ? JSON.parse(u).userId : null
  } catch {
    return null
  }
}

export async function request(path, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  }
  const token = getToken()
  if (token) headers['Authorization'] = `Bearer ${token}`
  const userId = getUserId()
  if (userId) headers['X-User-Id'] = String(userId)
  const res = await fetch(`${API_BASE}${path}`, { ...options, headers })
  if (res.status === 401) {
    localStorage.removeItem('eatease_token')
    localStorage.removeItem('eatease_user')
    window.location.href = '/login'
    throw new Error('Unauthorized')
  }
  const text = await res.text()
  if (!res.ok) {
    let msg = text
    try {
      const j = JSON.parse(text)
      msg = j.message || text
    } catch {}
    throw new Error(msg)
  }
  return text ? JSON.parse(text) : null
}

export function authLogin(email, password) {
  return request('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  })
}

export function authRegister(body) {
  return request('/auth/register', {
    method: 'POST',
    body: JSON.stringify(body),
  })
}

export function getRestaurants(params = {}) {
  const q = new URLSearchParams(params).toString()
  return request(`/restaurants${q ? `?${q}` : ''}`)
}

export function getRestaurant(id) {
  return request(`/restaurants/${id}`)
}

export function getMenu(restaurantId) {
  return request(`/restaurants/${restaurantId}/menus`)
}

export function getCart() {
  return request('/cart')
}

export function addToCart(menuItemId, quantity = 1) {
  return request('/cart/items', {
    method: 'POST',
    body: JSON.stringify({ menuItemId, quantity }),
  })
}

export function updateCartItem(cartItemId, quantity) {
  return request(`/cart/items/${cartItemId}?quantity=${quantity}`, {
    method: 'PUT',
  })
}

export function clearCart() {
  return request('/cart', { method: 'DELETE' })
}

export function placeOrder(deliveryAddress) {
  return request('/orders', {
    method: 'POST',
    body: JSON.stringify({ deliveryAddress }),
  })
}

export function getMyOrders() {
  return request('/orders/my')
}

export function getOrder(id) {
  return request(`/orders/${id}`)
}

export function getRestaurantOrders(restaurantId) {
  return request('/orders/restaurant', {
    headers: { 'X-Restaurant-Id': String(restaurantId) },
  })
}

export function updateOrderStatus(orderId, status, restaurantId) {
  return request(`/orders/${orderId}/status?status=${status}`, {
    method: 'PATCH',
    headers: restaurantId ? { 'X-Restaurant-Id': String(restaurantId) } : {},
  })
}

export function processPayment(orderId, amount, paymentMethod = 'UPI') {
  return request('/payments', {
    method: 'POST',
    body: JSON.stringify({ orderId, amount, paymentMethod }),
  })
}

export function getNotifications(page = 0, size = 20) {
  return request(`/notifications?page=${page}&size=${size}`)
}

// Admin / restaurant owner - pass extra headers when needed
export function getUsers() {
  return request('/users')
}

export function createRestaurant(body) {
  return request('/restaurants', {
    method: 'POST',
    body: JSON.stringify(body),
  })
}

export function updateRestaurant(id, body) {
  return request(`/restaurants/${id}`, {
    method: 'PUT',
    body: JSON.stringify(body),
  })
}

export function approveRestaurant(id, status) {
  return request(`/restaurants/${id}/status?status=${status}`, {
    method: 'PATCH',
  })
}

export function addMenuItem(restaurantId, body) {
  return request(`/restaurants/${restaurantId}/menus`, {
    method: 'POST',
    body: JSON.stringify(body),
  })
}

export function updateMenuItem(menuItemId, body) {
  return request(`/restaurants/menus/${menuItemId}`, {
    method: 'PUT',
    body: JSON.stringify(body),
  })
}

export function deleteMenuItem(menuItemId) {
  return request(`/restaurants/menus/${menuItemId}`, {
    method: 'DELETE',
  })
}

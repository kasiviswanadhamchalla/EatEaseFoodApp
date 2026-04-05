# EatEase – REST API Reference

Base URL: **http://localhost:8080**

For protected endpoints send:
- **Authorization:** `Bearer <token>` (from login/register)
- **X-User-Id:** `<userId>` (from login/register)
- **X-Restaurant-Id:** `<restaurantId>` (only where noted)

Use your **own userId** in paths like `/api/users/{id}` (or login as admin for any user).

---

## 1. Auth

### 1.1 Register

- **Method:** POST  
- **URL:** `/api/auth/register`  
- **Headers:** none  
- **Input (Body, JSON):**
```json
{
  "email": "postman@test.com",
  "password": "test123",
  "name": "Postman User",
  "phone": "+1234567890",
  "roles": ["CUSTOMER"]
}
```
- **Output (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "email": "postman@test.com",
  "userId": 2,
  "roles": ["CUSTOMER"],
  "name": "Postman User",
  "approved": true
}
```

---

### 1.2 Login

- **Method:** POST  
- **URL:** `/api/auth/login`  
- **Headers:** none  
- **Input (Body, JSON):**
```json
{
  "email": "postman@test.com",
  "password": "test123"
}
```
- **Output (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "email": "postman@test.com",
  "userId": 2,
  "roles": ["CUSTOMER"],
  "name": "Postman User",
  "approved": true
}
```

---

## 2. Users

Use **Authorization** and **X-User-Id**. For `/api/users/{id}` use your own `userId` or be ADMIN. Admin: `admin@eatease.com` / `Admin@123`.

### 2.1 Get user by ID

- **Method:** GET  
- **URL:** `/api/users/{id}`  
- **Headers:** Authorization, X-User-Id  
- **Input:** none (use your userId in path, e.g. `/api/users/2`)  
- **Output (200):**
```json
{
  "id": 2,
  "email": "postman@test.com",
  "name": "Postman User",
  "phone": "+1234567890",
  "roles": ["CUSTOMER"],
  "enabled": true,
  "approved": true,
  "createdAt": "2026-02-04T16:00:00Z"
}
```

---

### 2.2 Get all users (ADMIN)

- **Method:** GET  
- **URL:** `/api/users`  
- **Headers:** Authorization, X-User-Id  
- **Input:** none  
- **Output (200):** Array of user objects (same shape as 2.1).

---

### 2.3 Get users by role (ADMIN)

- **Method:** GET  
- **URL:** `/api/users/role/{role}`  
- **Headers:** Authorization, X-User-Id  
- **Input:** path `role` = `ADMIN` | `CUSTOMER` | `RESTAURANT_OWNER` | `DELIVERY_PARTNER`  
- **Output (200):** Array of user objects.

---

### 2.4 Update user

- **Method:** PUT  
- **URL:** `/api/users/{id}`  
- **Headers:** Authorization, X-User-Id, Content-Type: application/json  
- **Input (Body, JSON):**
```json
{
  "email": "updated@test.com",
  "password": "newpass123",
  "name": "Updated Name",
  "phone": "+9876543210",
  "roles": ["CUSTOMER"],
  "enabled": true,
  "approved": true
}
```
All fields optional.  
- **Output (200):** Single user object (updated).

---

### 2.5 Delete user (ADMIN)

- **Method:** DELETE  
- **URL:** `/api/users/{id}`  
- **Headers:** Authorization, X-User-Id  
- **Input:** none  
- **Output (204):** No body.

---

### 2.6 Get users pending approval (ADMIN)

- **Method:** GET  
- **URL:** `/api/users/pending-approval`  
- **Headers:** Authorization, X-User-Id  
- **Input:** none  
- **Output (200):** Array of user objects.

---

### 2.7 Approve user (ADMIN)

- **Method:** PATCH  
- **URL:** `/api/users/{id}/approve`  
- **Headers:** Authorization, X-User-Id  
- **Input:** none  
- **Output (200):** Single user object with `approved: true`.

---

### 2.8 Revoke approval (ADMIN)

- **Method:** PATCH  
- **URL:** `/api/users/{id}/revoke-approval`  
- **Headers:** Authorization, X-User-Id  
- **Input:** none  
- **Output (200):** Single user object.

---

## 3. Restaurants

### 3.1 Create restaurant

- **Method:** POST  
- **URL:** `/api/restaurants`  
- **Headers:** Authorization, X-User-Id, Content-Type: application/json  
- **Input (Body, JSON):**
```json
{
  "name": "My Restaurant",
  "description": "Best food",
  "ownerId": 2,
  "address": "123 Main St",
  "city": "New York",
  "phone": "+1555123456",
  "imageUrl": "https://example.com/image.jpg"
}
```
- **Output (201):**
```json
{
  "id": 1,
  "name": "My Restaurant",
  "description": "Best food",
  "ownerId": 2,
  "address": "123 Main St",
  "city": "New York",
  "phone": "+1555123456",
  "imageUrl": "https://example.com/image.jpg",
  "status": "PENDING_APPROVAL",
  "rating": null,
  "createdAt": "2026-02-04T16:00:00Z",
  "menuItems": null
}
```
`status`: PENDING_APPROVAL | APPROVED | REJECTED | SUSPENDED

---

### 3.2 Get restaurant by ID

- **Method:** GET  
- **URL:** `/api/restaurants/{id}`  
- **Headers:** none  
- **Input:** none  
- **Output (200):** Single restaurant object (same shape as 3.1, may include menuItems).

---

### 3.3 Get all restaurants

- **Method:** GET  
- **URL:** `/api/restaurants`  
- **Headers:** none  
- **Input (Query, optional):** `status`, `city`, `ownerId`, `q`  
- **Output (200):** Array of restaurant objects.

---

### 3.4 Search restaurants

- **Method:** GET  
- **URL:** `/api/restaurants/search?q=pizza&city=New York`  
- **Headers:** none  
- **Input:** query params `q`, `city` (optional)  
- **Output (200):** Array of restaurant objects.

---

### 3.5 Update restaurant

- **Method:** PUT  
- **URL:** `/api/restaurants/{id}`  
- **Headers:** Authorization, X-User-Id, Content-Type: application/json  
- **Input (Body, JSON):** same fields as 3.1  
- **Output (200):** Single restaurant object (updated).

---

### 3.6 Approve/reject restaurant status

- **Method:** PATCH  
- **URL:** `/api/restaurants/{id}/status?status=APPROVED`  
- **Headers:** Authorization, X-User-Id  
- **Input:** query `status` = PENDING_APPROVAL | APPROVED | REJECTED | SUSPENDED  
- **Output (200):** Single restaurant object.

---

### 3.7 Get restaurant menu

- **Method:** GET  
- **URL:** `/api/restaurants/{restaurantId}/menus`  
- **Headers:** none  
- **Input:** none  
- **Output (200):**
```json
[
  {
    "id": 1,
    "restaurantId": 1,
    "name": "Pizza",
    "description": "Margherita",
    "price": 12.50,
    "imageUrl": null,
    "available": true,
    "createdAt": "2026-02-04T16:00:00Z"
  }
]
```

---

### 3.8 Add menu item

- **Method:** POST  
- **URL:** `/api/restaurants/{restaurantId}/menus`  
- **Headers:** Authorization, X-User-Id, Content-Type: application/json  
- **Input (Body, JSON):**
```json
{
  "name": "Pizza",
  "description": "Margherita",
  "price": 12.50,
  "imageUrl": null,
  "available": true
}
```
- **Output (201):** Single menu item object (id, restaurantId, name, description, price, imageUrl, available, createdAt).

---

### 3.9 Get menu item by ID

- **Method:** GET  
- **URL:** `/api/restaurants/menus/{menuItemId}`  
- **Headers:** none  
- **Input:** none  
- **Output (200):** Single menu item object.

---

### 3.10 Update menu item

- **Method:** PUT  
- **URL:** `/api/restaurants/menus/{menuItemId}`  
- **Headers:** Authorization, X-User-Id, Content-Type: application/json  
- **Input (Body, JSON):** same as 3.8 (name, description, price, imageUrl, available)  
- **Output (200):** Single menu item object.

---

### 3.11 Delete menu item

- **Method:** DELETE  
- **URL:** `/api/restaurants/menus/{menuItemId}`  
- **Headers:** Authorization, X-User-Id  
- **Input:** none  
- **Output (204):** No body.

---

### 3.12 Add review

- **Method:** POST  
- **URL:** `/api/restaurants/reviews`  
- **Headers:** Authorization, X-User-Id, Content-Type: application/json  
- **Input (Body, JSON):**
```json
{
  "restaurantId": 1,
  "rating": 5,
  "comment": "Great food!"
}
```
`rating` 1–5.  
- **Output (201):**
```json
{
  "id": 1,
  "restaurantId": 1,
  "customerId": 2,
  "rating": 5,
  "comment": "Great food!",
  "createdAt": "2026-02-04T16:00:00Z"
}
```

---

### 3.13 Get restaurant reviews

- **Method:** GET  
- **URL:** `/api/restaurants/{restaurantId}/reviews`  
- **Headers:** none  
- **Input:** none  
- **Output (200):** Array of review objects (id, restaurantId, customerId, rating, comment, createdAt).

---

### 3.14 Delete review

- **Method:** DELETE  
- **URL:** `/api/restaurants/reviews/{reviewId}`  
- **Headers:** Authorization, X-User-Id  
- **Input:** none  
- **Output (204):** No body.

---

### 3.15 Get all reviews (ADMIN)

- **Method:** GET  
- **URL:** `/api/restaurants/reviews`  
- **Headers:** Authorization, X-User-Id, X-User-Roles: ADMIN  
- **Input:** none  
- **Output (200):** Array of all review objects. Without ADMIN: 403.

---

## 4. Cart

All require **Authorization** and **X-User-Id**.

### 4.1 Get cart

- **Method:** GET  
- **URL:** `/api/cart`  
- **Headers:** Authorization, X-User-Id  
- **Input:** none  
- **Output (200):**
```json
{
  "cartId": 1,
  "customerId": 2,
  "restaurantId": 1,
  "items": [
    {
      "cartItemId": 1,
      "menuItemId": 1,
      "name": "Pizza",
      "quantity": 2,
      "unitPrice": 12.50,
      "subtotal": 25.00
    }
  ],
  "totalAmount": 25.00
}
```

---

### 4.2 Add to cart

- **Method:** POST  
- **URL:** `/api/cart/items`  
- **Headers:** Authorization, X-User-Id, Content-Type: application/json  
- **Input (Body, JSON):**
```json
{
  "menuItemId": 1,
  "quantity": 2
}
```
- **Output (200):** Cart object (same shape as 4.1) with updated items and totalAmount.

---

### 4.3 Update cart item quantity

- **Method:** PUT  
- **URL:** `/api/cart/items/{cartItemId}?quantity=3`  
- **Headers:** Authorization, X-User-Id  
- **Input:** query `quantity`  
- **Output (200):** Cart object (updated).

---

### 4.4 Clear cart

- **Method:** DELETE  
- **URL:** `/api/cart`  
- **Headers:** Authorization, X-User-Id  
- **Input:** none  
- **Output (204):** No body.

---

## 5. Orders

Use **Authorization**, **X-User-Id**; for restaurant endpoints also **X-Restaurant-Id**.

### 5.1 Place order

- **Method:** POST  
- **URL:** `/api/orders`  
- **Headers:** Authorization, X-User-Id, Content-Type: application/json  
- **Input (Body, JSON):**
```json
{
  "deliveryAddress": "456 Oak Ave, Apt 2"
}
```
- **Output (201):**
```json
{
  "id": 1,
  "orderNumber": "ORD-20260204-001",
  "customerId": 2,
  "restaurantId": 1,
  "status": "PENDING",
  "deliveryAddress": "456 Oak Ave, Apt 2",
  "totalAmount": 25.00,
  "deliveryPartnerId": null,
  "createdAt": "2026-02-04T16:00:00Z",
  "updatedAt": "2026-02-04T16:00:00Z",
  "items": [
    {
      "menuItemId": 1,
      "name": "Pizza",
      "quantity": 2,
      "unitPrice": 12.50,
      "subtotal": 25.00
    }
  ]
}
```
`status`: PENDING | CONFIRMED | PREPARING | READY | OUT_FOR_DELIVERY | DELIVERED

---

### 5.2 Get order by ID

- **Method:** GET  
- **URL:** `/api/orders/{id}`  
- **Headers:** Authorization, X-User-Id (customer) or X-User-Id + X-Restaurant-Id (restaurant)  
- **Input:** none  
- **Output (200):** Single order object (same shape as 5.1).

---

### 5.3 Get my orders

- **Method:** GET  
- **URL:** `/api/orders/my`  
- **Headers:** Authorization, X-User-Id  
- **Input:** none  
- **Output (200):** Array of order objects.

---

### 5.4 Get restaurant orders

- **Method:** GET  
- **URL:** `/api/orders/restaurant`  
- **Headers:** Authorization, X-User-Id, X-Restaurant-Id  
- **Input:** none  
- **Output (200):** Array of order objects.

---

### 5.5 Update order status

- **Method:** PATCH  
- **URL:** `/api/orders/{id}/status?status=CONFIRMED`  
- **Headers:** Authorization, X-User-Id, X-Restaurant-Id  
- **Input:** query `status` = PENDING | CONFIRMED | PREPARING | READY | OUT_FOR_DELIVERY | DELIVERED  
- **Output (200):** Single order object (updated).

---

### 5.6 Get all orders (ADMIN)

- **Method:** GET  
- **URL:** `/api/orders/admin/all`  
- **Headers:** Authorization, X-User-Id, X-User-Roles: ADMIN  
- **Input:** none  
- **Output (200):** Array of all order objects. Without ADMIN: 403.

---

### 5.7 Get orders by status

- **Method:** GET  
- **URL:** `/api/orders/status/{status}`  
- **Headers:** Authorization, X-User-Id (optional X-User-Roles: ADMIN for all)  
- **Input:** path `status` e.g. PENDING, DELIVERED  
- **Output (200):** Array of order objects.

---

## 6. Payments

### 6.1 Process payment

- **Method:** POST  
- **URL:** `/api/payments`  
- **Headers:** Authorization, X-User-Id, Content-Type: application/json  
- **Input (Body, JSON):**
```json
{
  "orderId": 1,
  "amount": 25.00,
  "paymentMethod": "CARD"
}
```
- **Output (201):**
```json
{
  "id": 1,
  "orderId": 1,
  "customerId": 2,
  "amount": 25.00,
  "status": "SUCCESS",
  "transactionId": "TXN-xxx",
  "paymentMethod": "CARD",
  "createdAt": "2026-02-04T16:00:00Z",
  "failureReason": null
}
```
`status`: PENDING | SUCCESS | FAILED | REFUNDED

---

### 6.2 Get payment by ID

- **Method:** GET  
- **URL:** `/api/payments/{id}`  
- **Headers:** none  
- **Input:** none  
- **Output (200):** Single payment object (same shape as 6.1).

---

### 6.3 Get payment by order ID

- **Method:** GET  
- **URL:** `/api/payments/order/{orderId}`  
- **Headers:** none  
- **Input:** none  
- **Output (200):** Single payment object.

---

### 6.4 Get my payments

- **Method:** GET  
- **URL:** `/api/payments/my`  
- **Headers:** Authorization, X-User-Id  
- **Input:** none  
- **Output (200):** Array of payment objects.

---

### 6.5 Get payments by status

- **Method:** GET  
- **URL:** `/api/payments/status/{status}`  
- **Headers:** none  
- **Input:** path `status` = PENDING | SUCCESS | FAILED | REFUNDED  
- **Output (200):** Array of payment objects.

---

### 6.6 Get all payments (ADMIN)

- **Method:** GET  
- **URL:** `/api/payments/admin/all`  
- **Headers:** Authorization, X-User-Id, X-User-Roles: ADMIN  
- **Input:** none  
- **Output (200):** Array of all payment objects. Without ADMIN: 403.

---

## 7. Notifications

### 7.1 Get my notifications

- **Method:** GET  
- **URL:** `/api/notifications?page=0&size=20`  
- **Headers:** Authorization, X-User-Id  
- **Input:** query `page` (default 0), `size` (default 20)  
- **Output (200):**
```json
[
  {
    "id": "notif-id-1",
    "userId": 2,
    "type": "ORDER_STATUS",
    "title": "Order Update",
    "body": "Your order is confirmed.",
    "referenceId": "1",
    "read": false,
    "createdAt": "2026-02-04T16:00:00Z"
  }
]
```

---

### 7.2 Get unread count

- **Method:** GET  
- **URL:** `/api/notifications/unread-count`  
- **Headers:** Authorization, X-User-Id  
- **Input:** none  
- **Output (200):**
```json
{
  "count": 3
}
```

---

## Summary

| # | Method | Path | Auth | Output |
|---|--------|------|------|--------|
| 1.1 | POST | /api/auth/register | no | 200, AuthResponse |
| 1.2 | POST | /api/auth/login | no | 200, AuthResponse |
| 2.1 | GET | /api/users/{id} | yes (own id or ADMIN) | 200, UserResponse |
| 2.2 | GET | /api/users | ADMIN | 200, UserResponse[] |
| 2.3 | GET | /api/users/role/{role} | ADMIN | 200, UserResponse[] |
| 2.4 | PUT | /api/users/{id} | yes | 200, UserResponse |
| 2.5 | DELETE | /api/users/{id} | ADMIN | 204 |
| 2.6 | GET | /api/users/pending-approval | ADMIN | 200, UserResponse[] |
| 2.7 | PATCH | /api/users/{id}/approve | ADMIN | 200, UserResponse |
| 2.8 | PATCH | /api/users/{id}/revoke-approval | ADMIN | 200, UserResponse |
| 3.1 | POST | /api/restaurants | yes | 201, RestaurantResponse |
| 3.2 | GET | /api/restaurants/{id} | no | 200, RestaurantResponse |
| 3.3 | GET | /api/restaurants | no | 200, RestaurantResponse[] |
| 3.4 | GET | /api/restaurants/search | no | 200, RestaurantResponse[] |
| 3.5 | PUT | /api/restaurants/{id} | yes | 200, RestaurantResponse |
| 3.6 | PATCH | /api/restaurants/{id}/status | yes | 200, RestaurantResponse |
| 3.7 | GET | /api/restaurants/{id}/menus | no | 200, MenuItemResponse[] |
| 3.8 | POST | /api/restaurants/{id}/menus | yes | 201, MenuItemResponse |
| 3.9 | GET | /api/restaurants/menus/{menuItemId} | no | 200, MenuItemResponse |
| 3.10 | PUT | /api/restaurants/menus/{menuItemId} | yes | 200, MenuItemResponse |
| 3.11 | DELETE | /api/restaurants/menus/{menuItemId} | yes | 204 |
| 3.12 | POST | /api/restaurants/reviews | yes | 201, ReviewResponse |
| 3.13 | GET | /api/restaurants/{id}/reviews | no | 200, ReviewResponse[] |
| 3.14 | DELETE | /api/restaurants/reviews/{reviewId} | yes | 204 |
| 3.15 | GET | /api/restaurants/reviews | ADMIN | 200, ReviewResponse[] |
| 4.1 | GET | /api/cart | yes | 200, CartResponse |
| 4.2 | POST | /api/cart/items | yes | 200, CartResponse |
| 4.3 | PUT | /api/cart/items/{cartItemId} | yes | 200, CartResponse |
| 4.4 | DELETE | /api/cart | yes | 204 |
| 5.1 | POST | /api/orders | yes | 201, OrderResponse |
| 5.2 | GET | /api/orders/{id} | yes | 200, OrderResponse |
| 5.3 | GET | /api/orders/my | yes | 200, OrderResponse[] |
| 5.4 | GET | /api/orders/restaurant | yes + X-Restaurant-Id | 200, OrderResponse[] |
| 5.5 | PATCH | /api/orders/{id}/status | yes + X-Restaurant-Id | 200, OrderResponse |
| 5.6 | GET | /api/orders/admin/all | ADMIN | 200, OrderResponse[] |
| 5.7 | GET | /api/orders/status/{status} | yes | 200, OrderResponse[] |
| 6.1 | POST | /api/payments | yes | 201, PaymentResponse |
| 6.2 | GET | /api/payments/{id} | no | 200, PaymentResponse |
| 6.3 | GET | /api/payments/order/{orderId} | no | 200, PaymentResponse |
| 6.4 | GET | /api/payments/my | yes | 200, PaymentResponse[] |
| 6.5 | GET | /api/payments/status/{status} | no | 200, PaymentResponse[] |
| 6.6 | GET | /api/payments/admin/all | ADMIN | 200, PaymentResponse[] |
| 7.1 | GET | /api/notifications | yes | 200, NotificationResponse[] |
| 7.2 | GET | /api/notifications/unread-count | yes | 200, { "count": n } |

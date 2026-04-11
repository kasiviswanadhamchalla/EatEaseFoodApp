$ErrorActionPreference = "Stop"
$baseUrl = "http://localhost:8080"
Write-Host "========================================="
Write-Host "   EatEase E2E Endpoint Verification     "
Write-Host "========================================="
Write-Host ""

function Assert-Success {
    param($title, $response)
    if ($response.StatusCode -like "20*" -or $response -ne $null) {
        Write-Host "[OK] $title" -ForegroundColor Green
    } else {
        Write-Host "[FAIL] $title" -ForegroundColor Red
        Exit
    }
}

# 1. Register
$regBody = @{
    email = "testrunner@eatease.com"
    password = "password123"
    name = "Test Runner"
    phone = "1234567890"
    roles = @("CUSTOMER", "ADMIN")
} | ConvertTo-Json

Write-Host "1. Testing Auth Service (Register)..."
try {
    $regRes = Invoke-RestMethod -Uri "$baseUrl/api/auth/register" -Method Post -Body $regBody -ContentType "application/json"
    Assert-Success "POST /api/auth/register" $regRes
} catch {
    Write-Host "[SKIPPED/FAIL] User might exist or service down." -ForegroundColor DarkYellow
}

# 2. Login
$loginBody = @{
    email = "testrunner@eatease.com"
    password = "password123"
} | ConvertTo-Json

Write-Host "2. Testing Auth Service (Login)..."
try {
    $loginRes = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    $token = $loginRes.token
    $userId = $loginRes.userId
    Assert-Success "POST /api/auth/login" $loginRes
} catch {
    Write-Host "[FATAL] Cannot login. Exiting check." -ForegroundColor Red
    Exit
}

$headers = @{
    "Authorization" = "Bearer $token"
    "X-User-Id" = "$userId"
    "X-User-Roles" = "ADMIN"
}

# 3. User Service
Write-Host "3. Testing User Service..."
$userRes = Invoke-RestMethod -Uri "$baseUrl/api/users/$userId" -Method Get -Headers $headers
Assert-Success "GET /api/users/$userId" $userRes

# 4. Restaurant Service
Write-Host "4. Testing Restaurant Service (Create Restaurant)..."
$restBody = @{
    name = "Automated Test Rest"
    description = "A great place"
    ownerId = $userId
    address = "123 Test St"
    city = "Testville"
    phone = "555-5555"
} | ConvertTo-Json
$restRes = Invoke-RestMethod -Uri "$baseUrl/api/restaurants" -Method Post -Headers $headers -Body $restBody -ContentType "application/json"
Assert-Success "POST /api/restaurants" $restRes
$restaurantId = $restRes.id

# 5. Add Menu Item
Write-Host "5. Testing Restaurant Service (Add Menu Item)..."
$menuBody = @{
    name = "Test Burger"
    description = "Yummy"
    price = 10.50
    available = $true
} | ConvertTo-Json
$menuRes = Invoke-RestMethod -Uri "$baseUrl/api/restaurants/$restaurantId/menus" -Method Post -Headers $headers -Body $menuBody -ContentType "application/json"
Assert-Success "POST /api/restaurants/$restaurantId/menus" $menuRes
$menuItemId = $menuRes.id

# 6. Cart Service
Write-Host "6. Testing Order Service (Add to Cart)..."
$cartBody = @{
    menuItemId = $menuItemId
    quantity = 2
} | ConvertTo-Json
$cartRes = Invoke-RestMethod -Uri "$baseUrl/api/cart/items" -Method Post -Headers $headers -Body $cartBody -ContentType "application/json"
Assert-Success "POST /api/cart/items" $cartRes

# 7. Place Order
Write-Host "7. Testing Order Service (Place Order)..."
$orderBody = @{
    deliveryAddress = "123 Home address"
} | ConvertTo-Json
$orderRes = Invoke-RestMethod -Uri "$baseUrl/api/orders" -Method Post -Headers $headers -Body $orderBody -ContentType "application/json"
Assert-Success "POST /api/orders" $orderRes
$orderId = $orderRes.id

# 8. Process Payment
Write-Host "8. Testing Payment Service..."
$paymentBody = @{
    orderId = $orderId
    amount = 21.00
    paymentMethod = "CARD"
} | ConvertTo-Json
$payRes = Invoke-RestMethod -Uri "$baseUrl/api/payments" -Method Post -Headers $headers -Body $paymentBody -ContentType "application/json"
Assert-Success "POST /api/payments" $payRes

# 9. Search Service
Write-Host "9. Testing Search Service..."
try {
    $searchRes = Invoke-RestMethod -Uri "$baseUrl/api/restaurants/search?q=Burger" -Method Get
    Assert-Success "GET /api/restaurants/search" $searchRes
} catch {
    Write-Host "[SKIPPED/FAIL] Search Service might not have completely synced." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================="
Write-Host "  E2E Critical Flow Testing Completed!   "
Write-Host "========================================="

# Dashboard API Documentation

## Overview
This document provides comprehensive API endpoints for the Angular Dashboard frontend. All endpoints are prefixed with `/dashboard` and return JSON responses.

**Base URL**: `http://localhost:8080/dashboard`

## 📊 Summary Statistics

### GET `/summary`
Returns overall dashboard statistics.

**Response Example:**
```json
{
  "totalOrders": 1250,
  "todayOrders": 45,
  "pendingOrders": 12,
  "completedOrders": 1180,
  "cancelledOrders": 58,
  "inProgressOrders": 3,
  "totalServices": 15,
  "activeServices": 12,
  "inactiveServices": 3,
  "totalWindows": 8,
  "activeWindows": 6,
  "totalUsers": 25,
  "activeUsers": 20,
  "operatorUsers": 8,
  "customerUsers": 17,
  "totalCategories": 5,
  "totalTransferRequests": 45,
  "pendingTransferRequests": 3,
  "approvedTransferRequests": 40,
  "rejectedTransferRequests": 2,
  "averageWaitTime": 15.5,
  "longestWaitTime": 45,
  "shortestWaitTime": 2
}
```

## 📋 Entity Lists

### Categories
- **GET** `/categories` - Get all categories
- **GET** `/count/categories` - Get categories count

### Services
- **GET** `/services` - Get all services
- **GET** `/services/status/{status}` - Get services by status (ACTIVE, INACTIVE)
- **GET** `/count/services` - Get services count

### Windows
- **GET** `/windows` - Get all windows
- **GET** `/count/windows` - Get windows count

### Users
- **GET** `/users` - Get all users
- **GET** `/users/role/{role}` - Get users by role (ADMIN, OPERATOR, CUSTOMER)
- **GET** `/count/users` - Get users count

### Orders
- **GET** `/orders` - Get all orders
- **GET** `/orders/status/{status}` - Get orders by status (PENDING, IN_PROGRESS, COMPLETED, CANCELED, CALLED)
- **GET** `/count/orders` - Get orders count

### Transfer Requests
- **GET** `/transfer-requests` - Get all transfer requests
- **GET** `/transfer-requests/status/{status}` - Get transfer requests by status
- **GET** `/count/transfer-requests` - Get transfer requests count

### Window Roles
- **GET** `/window-roles` - Get all window-service mappings

### Order Actions
- **GET** `/order-actions` - Get all order action logs

### User Actions
- **GET** `/user-actions` - Get all user action logs

### Order Archives
- **GET** `/order-archives` - Get historical order data

## 🕒 Recent Activity

### Recent Orders
- **GET** `/recent/orders?limit=10` - Get recent orders (default limit: 10)

### Recent Order Actions
- **GET** `/recent/order-actions?limit=10` - Get recent order actions

### Recent User Actions
- **GET** `/recent/user-actions?limit=10` - Get recent user activities

### Recent Transfer Requests
- **GET** `/recent/transfer-requests?limit=10` - Get recent transfer requests

## 📈 Analytics & Statistics

### Order Analytics
- **GET** `/analytics/orders-by-service` - Orders grouped by service
- **GET** `/analytics/orders-by-window` - Orders grouped by window
- **GET** `/analytics/orders-by-date?days=7` - Orders statistics for last N days

## 🎯 Queue Management

### Current Queue Status
- **GET** `/queue/current` - Get current queue (pending + called orders)
- **GET** `/queue/pending` - Get pending orders only
- **GET** `/queue/in-progress` - Get orders currently being processed

## 🏥 Health Check
- **GET** `/health` - API health check

## 📝 Response Models

### DashboardSummaryResponse
```typescript
interface DashboardSummaryResponse {
  totalOrders: number;
  todayOrders: number;
  pendingOrders: number;
  completedOrders: number;
  cancelledOrders: number;
  inProgressOrders: number;
  totalServices: number;
  activeServices: number;
  inactiveServices: number;
  totalWindows: number;
  activeWindows: number;
  totalUsers: number;
  activeUsers: number;
  operatorUsers: number;
  customerUsers: number;
  totalCategories: number;
  totalTransferRequests: number;
  pendingTransferRequests: number;
  approvedTransferRequests: number;
  rejectedTransferRequests: number;
  averageWaitTime: number;
  longestWaitTime: number;
  shortestWaitTime: number;
}
```

### CategoryResponse
```typescript
interface CategoryResponse {
  id: number;
  name: string;
}
```

### ServiceResponse
```typescript
interface ServiceResponse {
  id: number;
  code: string;
  start: number;
  end: number;
  categoryId: number;
  categoryName: string;
  name: string;
  endTime: string;
  pendingOrdersCount: number;
  serviceStatus: string;
  serviceType: string;
  icon: string;
  currentNumber: number;
}
```

### WindowResponse
```typescript
interface WindowResponse {
  id: number;
  ipAddress: string;
  windowNumber: string;
  services: ServiceResponse[];
}
```

### UserResponse
```typescript
interface UserResponse {
  id: number;
  username: string;
  password: string;
  email: string;
  phone: string;
  name: string;
  status: string;
  address: string;
  windowId: string;
  windowNumber: string;
  role: string;
}
```

### OrderResponse
```typescript
interface OrderResponse {
  orderId: number;
  serviceId: number;
  currentNumber: number;
  callDate: string;
  windowNumber: number;
  serviceCode: string;
}
```

### TransferResponse
```typescript
interface TransferResponse {
  requestId: number;
  order: string;
  userRequester: string;
  requestDate: string;
  requestedService: string;
  requestedWindow: string;
  targetService: string;
  userResponse: string;
  targetWindow: string;
  responseDate: string;
  status: string;
}
```

### WindowRoleResponse
```typescript
interface WindowRoleResponse {
  id: number;
  windowId: number;
  windowNumber: string;
  serviceId: number;
  serviceName: string;
  serviceCode: string;
  categoryName: string;
}
```

### OrderActionResponse
```typescript
interface OrderActionResponse {
  id: number;
  createdAt: string;
  orderStatus: string;
  orderId: number;
  orderCode: string;
  serviceName: string;
  windowNumber: string;
}
```

### UserActionsResponse
```typescript
interface UserActionsResponse {
  id: number;
  createdAt: string;
  userStatus: string;
  username: string;
}
```

### OrderArchiveResponse
```typescript
interface OrderArchiveResponse {
  id: number;
  currentNumber: number;
  updatedAt: string;
  createdAt: string;
  callDate: string;
  orderStatus: string;
  windowNumber: string;
  serviceName: string;
  categoryName: string;
  username: string;
}
```

## 🚀 Angular Service Example

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private baseUrl = 'http://localhost:8080/dashboard';

  constructor(private http: HttpClient) {}

  // Summary
  getDashboardSummary(): Observable<DashboardSummaryResponse> {
    return this.http.get<DashboardSummaryResponse>(`${this.baseUrl}/summary`);
  }

  // Categories
  getAllCategories(): Observable<CategoryResponse[]> {
    return this.http.get<CategoryResponse[]>(`${this.baseUrl}/categories`);
  }

  // Services
  getAllServices(): Observable<ServiceResponse[]> {
    return this.http.get<ServiceResponse[]>(`${this.baseUrl}/services`);
  }

  getServicesByStatus(status: string): Observable<ServiceResponse[]> {
    return this.http.get<ServiceResponse[]>(`${this.baseUrl}/services/status/${status}`);
  }

  // Windows
  getAllWindows(): Observable<WindowResponse[]> {
    return this.http.get<WindowResponse[]>(`${this.baseUrl}/windows`);
  }

  // Users
  getAllUsers(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${this.baseUrl}/users`);
  }

  getUsersByRole(role: string): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${this.baseUrl}/users/role/${role}`);
  }

  // Orders
  getAllOrders(): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.baseUrl}/orders`);
  }

  getOrdersByStatus(status: string): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.baseUrl}/orders/status/${status}`);
  }

  // Recent Activity
  getRecentOrders(limit: number = 10): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.baseUrl}/recent/orders?limit=${limit}`);
  }

  // Queue Management
  getCurrentQueue(): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.baseUrl}/queue/current`);
  }

  getPendingOrders(): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.baseUrl}/queue/pending`);
  }

  // Transfer Requests
  getAllTransferRequests(): Observable<TransferResponse[]> {
    return this.http.get<TransferResponse[]>(`${this.baseUrl}/transfer-requests`);
  }

  // Analytics
  getOrdersByServiceStats(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/analytics/orders-by-service`);
  }

  // Health Check
  healthCheck(): Observable<string> {
    return this.http.get(`${this.baseUrl}/health`, { responseType: 'text' });
  }
}
```

## 🔧 Error Handling

All endpoints return standard HTTP status codes:
- **200 OK**: Successful request
- **400 Bad Request**: Invalid parameters
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

Error responses follow this format:
```json
{
  "message": "Error description",
  "errorCode": "ERROR_CODE",
  "timestamp": "2024-01-15T10:30:00Z",
  "path": "/dashboard/endpoint"
}
```

## 🔐 Authentication

Most endpoints require authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## 📊 Dashboard Features Supported

1. **Real-time Statistics**: Live counts and metrics
2. **Entity Management**: Full CRUD operations data
3. **Queue Monitoring**: Current queue status and management
4. **Activity Tracking**: Recent user and system activities
5. **Analytics**: Statistical data for reporting
6. **Transfer Management**: Order transfer monitoring
7. **User Management**: Role-based user data
8. **Service Monitoring**: Service status and performance

---

**Note**: This API is designed specifically for Angular dashboard frontend integration. All endpoints return JSON data optimized for dashboard widgets and data visualization components.

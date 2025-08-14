# Queue Management Dashboard API - Frontend Documentation

## 🎯 Overview
This API provides comprehensive endpoints for building a complete queue management dashboard in Angular. All endpoints return JSON data optimized for dashboard widgets and data visualization.

**Base URL**: `http://localhost:8080/dashboard`
**Content-Type**: `application/json`
**CORS**: Enabled for all origins

---

## 🔐 Authentication
Include JWT token in all requests using either header format:

**Option 1: Standard Authorization Header (Recommended)**
```typescript
headers: {
  'Authorization': 'Bearer <your-jwt-token>',
  'Content-Type': 'application/json'
}
```

**Option 2: Custom x-access-token Header (If your frontend uses this)**
```typescript
headers: {
  'x-access-token': '<your-jwt-token>',
  'Content-Type': 'application/json'
}
```

---

## 📊 Dashboard Summary & KPIs

### GET `/summary`
**Description**: Get overall dashboard statistics and KPIs
**Response**: Complete metrics for dashboard widgets

```typescript
interface DashboardSummaryResponse {
  // Order Statistics
  totalOrders: number;           // Total orders in system
  todayOrders: number;           // Orders created today
  pendingOrders: number;         // Orders waiting to be called
  completedOrders: number;       // Successfully completed orders
  cancelledOrders: number;       // Cancelled orders
  inProgressOrders: number;      // Orders currently being processed
  
  // Service Statistics
  totalServices: number;         // Total services available
  activeServices: number;        // Currently active services
  inactiveServices: number;      // Inactive services
  
  // Infrastructure Statistics
  totalWindows: number;          // Total service windows
  activeWindows: number;         // Currently active windows
  
  // User Statistics
  totalUsers: number;            // Total users in system
  activeUsers: number;           // Active users
  operatorUsers: number;         // Users with operator role
  customerUsers: number;         // Customer users
  
  // Category Statistics
  totalCategories: number;       // Service categories
  
  // Transfer Statistics
  totalTransferRequests: number;     // All transfer requests
  pendingTransferRequests: number;   // Pending transfers
  approvedTransferRequests: number;  // Approved transfers
  rejectedTransferRequests: number;  // Rejected transfers
  
  // Performance Metrics
  averageWaitTime: number;       // Average wait time in minutes
  longestWaitTime: number;       // Longest wait time
  shortestWaitTime: number;      // Shortest wait time
}
```

**Example Response**:
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
  "totalUsers": 25,
  "totalCategories": 5,
  "averageWaitTime": 15.5
}
```

---

## 🏢 Entity Management APIs

### Categories

#### GET `/categories`
**Description**: Get all service categories
```typescript
interface CategoryResponse {
  id: number;
  name: string;
}
```

#### GET `/count/categories`
**Description**: Get total categories count
**Response**: `number`

---

### Services

#### GET `/services`
**Description**: Get all services with details
```typescript
interface ServiceResponse {
  id: number;
  code: string;                 // Service code (e.g., "PASS")
  start: number;                // Service start number
  end: number;                  // Service end number
  categoryId: number;           // Parent category ID
  categoryName: string;         // Parent category name
  name: string;                 // Service name
  endTime: string;              // Service end time
  pendingOrdersCount: number;   // Current pending orders
  serviceStatus: string;        // "ACTIVE" | "INACTIVE"
  serviceType: string;          // Service type
  icon: string;                 // Service icon URL/name
  currentNumber: number;        // Current serving number
}
```

#### GET `/services/status/{status}`
**Description**: Get services by status
**Parameters**: `status` - "ACTIVE" | "INACTIVE"

#### GET `/count/services`
**Description**: Get total services count

---

### Windows

#### GET `/windows`
**Description**: Get all service windows
```typescript
interface WindowResponse {
  id: number;
  ipAddress: string;            // Window IP address
  windowNumber: string;         // Display window number
  services: ServiceResponse[];  // Services available at this window
}
```

#### GET `/count/windows`
**Description**: Get total windows count

---

### Users

#### GET `/users`
**Description**: Get all users
```typescript
interface UserResponse {
  id: number;
  username: string;
  email: string;
  phone: string;
  name: string;
  status: string;
  address: string;
  windowId: string;             // Assigned window ID
  windowNumber: string;         // Assigned window number
  role: string;                 // "ADMIN" | "OPERATOR" | "CUSTOMER"
}
```

#### GET `/users/role/{role}`
**Description**: Get users by role
**Parameters**: `role` - "ADMIN" | "OPERATOR" | "CUSTOMER"

#### GET `/count/users`
**Description**: Get total users count

---

### Orders

#### GET `/orders`
**Description**: Get all orders
```typescript
interface OrderResponse {
  orderId: number;
  serviceId: number;
  currentNumber: number;        // Queue number
  callDate: string;            // When order was called (ISO date)
  windowNumber: number;        // Window where order is processed
  serviceCode: string;         // Service code
}
```

#### GET `/orders/status/{status}`
**Description**: Get orders by status
**Parameters**: `status` - "PENDING" | "BOOKED" | "CALLED" | "CANCELLED" | "TRANSFER"

#### GET `/count/orders`
**Description**: Get total orders count

---

## 🔄 Queue Management APIs

### GET `/queue/current`
**Description**: Get current active queue (pending + called orders)
**Response**: `OrderResponse[]`

### GET `/queue/pending`
**Description**: Get only pending orders waiting to be called
**Response**: `OrderResponse[]`

### GET `/queue/in-progress`
**Description**: Get orders currently being processed
**Response**: `OrderResponse[]`

---

## ⏰ Recent Activity APIs

### GET `/recent/orders?limit={limit}`
**Description**: Get recent orders
**Parameters**: `limit` (optional, default: 10)
**Response**: `OrderResponse[]`

### GET `/recent/order-actions?limit={limit}`
**Description**: Get recent order actions/status changes
```typescript
interface OrderActionResponse {
  id: number;
  createdAt: string;           // ISO date string
  orderStatus: string;         // Status that was set
  orderId: number;
  orderCode: string;
  serviceName: string;
  windowNumber: string;
}
```

### GET `/recent/user-actions?limit={limit}`
**Description**: Get recent user activities
```typescript
interface UserActionsResponse {
  id: number;
  createdAt: string;           // ISO date string
  userStatus: string;          // User status/action
  username: string;
}
```

### GET `/recent/transfer-requests?limit={limit}`
**Description**: Get recent transfer requests
```typescript
interface TransferResponse {
  requestId: number;
  order: string;               // Order identifier
  userRequester: string;       // User who requested transfer
  requestDate: string;         // Request date
  requestedService: string;    // Source service
  requestedWindow: string;     // Source window
  targetService: string;       // Target service
  userResponse: string;        // User who approved/rejected
  targetWindow: string;        // Target window
  responseDate: string;        // Response date
  status: string;              // Transfer status
}
```

---

## 📈 Analytics APIs

### GET `/analytics/orders-by-service`
**Description**: Get order statistics grouped by service
**Response**: `Object[]` - Raw statistical data

### GET `/analytics/orders-by-window`
**Description**: Get order statistics grouped by window
**Response**: `Object[]` - Raw statistical data

### GET `/analytics/orders-by-date?days={days}`
**Description**: Get order trends for last N days
**Parameters**: `days` (optional, default: 7)
**Response**: `Object[]` - Time series data

---

## 🔧 Additional Entity APIs

### Transfer Requests

#### GET `/transfer-requests`
**Description**: Get all transfer requests
**Response**: `TransferResponse[]`

#### GET `/transfer-requests/status/{status}`
**Description**: Get transfer requests by status
**Response**: `TransferResponse[]`

#### GET `/count/transfer-requests`
**Description**: Get total transfer requests count

### Window Roles

#### GET `/window-roles`
**Description**: Get window-service mappings
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

### Order Actions

#### GET `/order-actions`
**Description**: Get all order action logs
**Response**: `OrderActionResponse[]`

### User Actions

#### GET `/user-actions`
**Description**: Get all user action logs
**Response**: `UserActionsResponse[]`

### Order Archives

#### GET `/order-archives`
**Description**: Get historical order data
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

---

## 🚀 Angular Service Implementation

```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DashboardApiService {
  private baseUrl = 'http://localhost:8080/dashboard';
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      // Option 1: Standard Authorization header (recommended)
      'Authorization': `Bearer ${this.getAuthToken()}`,
      // Option 2: If your app uses x-access-token, use this instead:
      // 'x-access-token': this.getAuthToken()
    })
  };

  constructor(private http: HttpClient) {}

  private getAuthToken(): string {
    // Implement your token retrieval logic
    return localStorage.getItem('authToken') || '';
  }

  // Dashboard Summary
  getDashboardSummary(): Observable<DashboardSummaryResponse> {
    return this.http.get<DashboardSummaryResponse>(`${this.baseUrl}/summary`, this.httpOptions);
  }

  // Categories
  getCategories(): Observable<CategoryResponse[]> {
    return this.http.get<CategoryResponse[]>(`${this.baseUrl}/categories`, this.httpOptions);
  }

  getCategoriesCount(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/count/categories`, this.httpOptions);
  }

  // Services
  getServices(): Observable<ServiceResponse[]> {
    return this.http.get<ServiceResponse[]>(`${this.baseUrl}/services`, this.httpOptions);
  }

  getServicesByStatus(status: string): Observable<ServiceResponse[]> {
    return this.http.get<ServiceResponse[]>(`${this.baseUrl}/services/status/${status}`, this.httpOptions);
  }

  getServicesCount(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/count/services`, this.httpOptions);
  }

  // Windows
  getWindows(): Observable<WindowResponse[]> {
    return this.http.get<WindowResponse[]>(`${this.baseUrl}/windows`, this.httpOptions);
  }

  getWindowsCount(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/count/windows`, this.httpOptions);
  }

  // Users
  getUsers(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${this.baseUrl}/users`, this.httpOptions);
  }

  getUsersByRole(role: string): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${this.baseUrl}/users/role/${role}`, this.httpOptions);
  }

  getUsersCount(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/count/users`, this.httpOptions);
  }

  // Orders
  getOrders(): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.baseUrl}/orders`, this.httpOptions);
  }

  getOrdersByStatus(status: string): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.baseUrl}/orders/status/${status}`, this.httpOptions);
  }

  getOrdersCount(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/count/orders`, this.httpOptions);
  }

  // Queue Management
  getCurrentQueue(): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.baseUrl}/queue/current`, this.httpOptions);
  }

  getPendingOrders(): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.baseUrl}/queue/pending`, this.httpOptions);
  }

  getOrdersInProgress(): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.baseUrl}/queue/in-progress`, this.httpOptions);
  }

  // Recent Activity
  getRecentOrders(limit: number = 10): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.baseUrl}/recent/orders?limit=${limit}`, this.httpOptions);
  }

  getRecentOrderActions(limit: number = 10): Observable<OrderActionResponse[]> {
    return this.http.get<OrderActionResponse[]>(`${this.baseUrl}/recent/order-actions?limit=${limit}`, this.httpOptions);
  }

  getRecentUserActions(limit: number = 10): Observable<UserActionsResponse[]> {
    return this.http.get<UserActionsResponse[]>(`${this.baseUrl}/recent/user-actions?limit=${limit}`, this.httpOptions);
  }

  getRecentTransferRequests(limit: number = 10): Observable<TransferResponse[]> {
    return this.http.get<TransferResponse[]>(`${this.baseUrl}/recent/transfer-requests?limit=${limit}`, this.httpOptions);
  }

  // Transfer Requests
  getTransferRequests(): Observable<TransferResponse[]> {
    return this.http.get<TransferResponse[]>(`${this.baseUrl}/transfer-requests`, this.httpOptions);
  }

  getTransferRequestsByStatus(status: string): Observable<TransferResponse[]> {
    return this.http.get<TransferResponse[]>(`${this.baseUrl}/transfer-requests/status/${status}`, this.httpOptions);
  }

  // Analytics
  getOrdersByServiceStats(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/analytics/orders-by-service`, this.httpOptions);
  }

  getOrdersByWindowStats(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/analytics/orders-by-window`, this.httpOptions);
  }

  getOrdersByDateStats(days: number = 7): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/analytics/orders-by-date?days=${days}`, this.httpOptions);
  }

  // Window Roles
  getWindowRoles(): Observable<WindowRoleResponse[]> {
    return this.http.get<WindowRoleResponse[]>(`${this.baseUrl}/window-roles`, this.httpOptions);
  }

  // Health Check
  healthCheck(): Observable<string> {
    return this.http.get(`${this.baseUrl}/health`, { 
      ...this.httpOptions, 
      responseType: 'text' as 'json' 
    }) as Observable<string>;
  }
}
```

---

## 🎨 Dashboard Layout Suggestions

### 1. **Dashboard Overview Page**
```typescript
// Use these APIs for main dashboard
- getDashboardSummary() // KPI cards
- getCurrentQueue() // Queue status widget
- getRecentOrders(5) // Recent activity feed
- getRecentOrderActions(5) // Action logs
```

### 2. **Queue Management Page**
```typescript
// Real-time queue monitoring
- getCurrentQueue() // Current queue table
- getPendingOrders() // Pending orders list
- getOrdersInProgress() // Processing orders
- getOrdersByStatus('CALLED') // Called orders
```

### 3. **Analytics Page**
```typescript
// Charts and statistics
- getOrdersByServiceStats() // Service performance chart
- getOrdersByWindowStats() // Window efficiency chart
- getOrdersByDateStats(30) // Trend charts
- getDashboardSummary() // Overall metrics
```

### 4. **Entity Management Pages**
```typescript
// Services Management
- getServices() // Services table
- getServicesByStatus('ACTIVE') // Active services
- getWindowRoles() // Service-window mappings

// Users Management
- getUsers() // Users table
- getUsersByRole('OPERATOR') // Operators list
- getUsersCount() // Total count

// Windows Management
- getWindows() // Windows table with services
- getWindowsCount() // Count
```

### 5. **Reports & History**
```typescript
// Historical data
- getOrderArchives() // Historical orders
- getRecentTransferRequests(50) // Transfer history
- getRecentUserActions(100) // User activity log
```

---

## 🔍 Error Handling

### HTTP Status Codes
- **200 OK**: Successful request
- **400 Bad Request**: Invalid parameters
- **401 Unauthorized**: Invalid/missing authentication
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

### Error Response Format
```typescript
interface ErrorResponse {
  message: string;
  errorCode: string;
  timestamp: string;
  path: string;
}
```

### Angular Error Handling Example
```typescript
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

getDashboardSummary(): Observable<DashboardSummaryResponse> {
  return this.http.get<DashboardSummaryResponse>(`${this.baseUrl}/summary`, this.httpOptions)
    .pipe(
      catchError(error => {
        console.error('Dashboard API Error:', error);
        // Handle error appropriately
        return throwError(error);
      })
    );
}
```

---

## 🚦 Rate Limiting & Performance

### Recommendations
- **Cache data** for 30-60 seconds for summary statistics
- **Implement polling** for real-time queue updates (every 5-10 seconds)
- **Use pagination** for large data sets
- **Debounce** filter operations

### Example Caching
```typescript
import { shareReplay } from 'rxjs/operators';

private dashboardSummary$ = this.getDashboardSummary().pipe(
  shareReplay({ bufferSize: 1, refCount: true })
);

// Use cached observable
getDashboardSummaryObservable(): Observable<DashboardSummaryResponse> {
  return this.dashboardSummary$;
}
```

---

## 🔧 Development Tips

### 1. **Environment Configuration**
```typescript
// environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};

// Use in service
private baseUrl = `${environment.apiUrl}/dashboard`;
```

### 2. **Type Safety**
- Use provided TypeScript interfaces
- Enable strict mode in TypeScript
- Create custom types for complex data

### 3. **Testing**
```typescript
// Example test
describe('DashboardApiService', () => {
  let service: DashboardApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [DashboardApiService]
    });
    service = TestBed.inject(DashboardApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should fetch dashboard summary', () => {
    const mockSummary: DashboardSummaryResponse = {
      totalOrders: 100,
      todayOrders: 10,
      // ... other properties
    };

    service.getDashboardSummary().subscribe(summary => {
      expect(summary).toEqual(mockSummary);
    });

    const req = httpMock.expectOne(`${service.baseUrl}/summary`);
    expect(req.request.method).toBe('GET');
    req.flush(mockSummary);
  });
});
```

---

## 📱 Real-time Updates

### WebSocket Alternative
Since this is REST API, implement polling for real-time feel:

```typescript
import { interval } from 'rxjs';
import { switchMap } from 'rxjs/operators';

// Poll queue status every 5 seconds
const queueUpdates$ = interval(5000).pipe(
  switchMap(() => this.dashboardService.getCurrentQueue())
);

queueUpdates$.subscribe(queue => {
  // Update UI with latest queue data
});
```

---

This documentation provides everything needed to build a comprehensive queue management dashboard with Angular and Cursor AI. All endpoints are production-ready and optimized for dashboard usage patterns.

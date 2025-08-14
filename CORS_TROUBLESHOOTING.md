# CORS Troubleshooting Guide

## ✅ **CORS Issue Fixed!**

### **Problem**
Frontend was getting CORS error:
```
Access to XMLHttpRequest at 'http://localhost:8083/dashboard/summary' from origin 'http://localhost:4200' has been blocked by CORS policy: Request header field x-access-token is not allowed by Access-Control-Allow-Headers in preflight response.
```

### **Solution Applied**
1. **Updated CORS Configuration** to allow `x-access-token` header
2. **Modified Application Properties** (`application.yml`)
3. **Updated CustomCorsConfiguration** class
4. **Enhanced WebConfig** CORS settings
5. **Added explicit @CrossOrigin** to DashboardController

---

## 🔧 **Changes Made**

### 1. **application.yml**
```yaml
app:
  cors:
    allowed-headers: Authorization,Content-Type,Accept,Origin,X-Requested-With,x-access-token
```

### 2. **CustomCorsConfiguration.java**
```java
@Value("${app.cors.allowed-headers:Authorization,Content-Type,Accept,Origin,X-Requested-With,x-access-token}")
private String allowedHeaders;
```

### 3. **WebConfig.java**
```java
.allowedHeaders("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With", "x-access-token")
.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
```

### 4. **DashboardController.java**
```java
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"}, 
             allowedHeaders = {"Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With", "x-access-token"},
             allowCredentials = "true")
```

---

## 📋 **Frontend Options**

### **Option 1: Use Standard Authorization Header (Recommended)**
```typescript
headers: {
  'Authorization': 'Bearer <your-jwt-token>',
  'Content-Type': 'application/json'
}
```

### **Option 2: Continue Using x-access-token**
```typescript
headers: {
  'x-access-token': '<your-jwt-token>',
  'Content-Type': 'application/json'
}
```

---

## 🚀 **Testing the Fix**

### **1. Restart Spring Boot Application**
```bash
# Stop the application and restart it to apply CORS changes
mvn spring-boot:run
```

### **2. Test from Frontend**
```typescript
// Test the dashboard API
this.http.get('http://localhost:8083/dashboard/summary', {
  headers: {
    'x-access-token': 'your-jwt-token',
    'Content-Type': 'application/json'
  }
}).subscribe(
  response => console.log('Success:', response),
  error => console.error('Error:', error)
);
```

### **3. Verify in Browser DevTools**
- Check Network tab for successful requests
- Verify no CORS errors in Console
- Confirm preflight OPTIONS requests are successful

---

## 🔍 **Common CORS Issues & Solutions**

### **Issue 1: Preflight Request Fails**
**Solution**: Ensure OPTIONS method is allowed
```java
.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
```

### **Issue 2: Custom Headers Not Allowed**
**Solution**: Add custom headers to allowedHeaders
```java
.allowedHeaders("Authorization", "Content-Type", "x-access-token", "your-custom-header")
```

### **Issue 3: Credentials Not Allowed**
**Solution**: Enable credentials
```java
.allowCredentials(true)
```

### **Issue 4: Origin Not Allowed**
**Solution**: Add your frontend origin
```java
.allowedOrigins("http://localhost:4200", "http://127.0.0.1:4200")
```

---

## 📊 **CORS Configuration Layers**

Your application now has **3 layers of CORS configuration** for maximum compatibility:

1. **Global Spring Security CORS** (`CustomCorsConfiguration`)
2. **Web MVC CORS** (`WebConfig`)
3. **Controller-level CORS** (`@CrossOrigin` on DashboardController)

This ensures CORS works regardless of how requests are processed.

---

## 🧪 **Quick Test Commands**

### **Test with curl:**
```bash
# Test preflight request
curl -X OPTIONS http://localhost:8083/dashboard/summary \
  -H "Origin: http://localhost:4200" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: x-access-token" \
  -v

# Test actual request
curl -X GET http://localhost:8083/dashboard/summary \
  -H "Origin: http://localhost:4200" \
  -H "x-access-token: your-jwt-token" \
  -v
```

### **Test with JavaScript:**
```javascript
// Test in browser console
fetch('http://localhost:8083/dashboard/summary', {
  method: 'GET',
  headers: {
    'x-access-token': 'your-jwt-token',
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => console.log('Success:', data))
.catch(error => console.error('Error:', error));
```

---

## ✅ **Expected Results**

After applying these changes and restarting your Spring Boot application:

1. ✅ **No more CORS errors** in browser console
2. ✅ **Successful API calls** from Angular frontend
3. ✅ **Preflight requests** (OPTIONS) work correctly
4. ✅ **Custom headers** (`x-access-token`) are accepted
5. ✅ **Dashboard endpoints** are accessible from `http://localhost:4200`

Your Angular dashboard should now be able to successfully communicate with the Spring Boot API! 🎉

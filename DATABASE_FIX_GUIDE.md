# Database Token Column Fix Guide

## Problem
The application was throwing the following error:
```
org.springframework.dao.DataIntegrityViolationException: could not execute statement [Data truncation: Data too long for column 'token' at row 1]
```

This occurs because the enhanced JWT tokens are now longer than the original database column can accommodate.

## Solution

### 1. Immediate Fix - Run SQL Script
Execute the following SQL commands on your database:

```sql
USE queue_test; -- Replace with your actual database name

-- Update token column to TEXT type
ALTER TABLE token MODIFY COLUMN token TEXT;

-- Update refresh_token column to TEXT type  
ALTER TABLE token MODIFY COLUMN refresh_token TEXT;

-- Optional: Add performance indexes
CREATE INDEX IF NOT EXISTS idx_token_user_id ON token(user_id);
CREATE INDEX IF NOT EXISTS idx_token_is_active ON token(is_active);
CREATE INDEX IF NOT EXISTS idx_token_user_active ON token(user_id, is_active);
CREATE INDEX IF NOT EXISTS idx_token_expires_at ON token(expires_at);

-- Verify the changes
DESCRIBE token;
```

### 2. Alternative - Use Provided SQL File
You can also run the provided SQL file:
```bash
mysql -u root -p queue_test < src/main/resources/sql/update_token_table.sql
```

### 3. Code Changes Made

The following improvements were implemented:

#### A. Updated Token Entity
- Added `@Column(columnDefinition = "TEXT")` annotations for token fields
- Added proper JPA annotations for better database mapping
- Added lazy loading for user relationship

#### B. Enhanced Token Repository
- Added cleanup methods for expired and inactive tokens
- Added query methods for token maintenance

#### C. Token Cleanup Service
- Automatic scheduled cleanup of expired tokens
- Manual cleanup endpoints for administration
- Performance monitoring and logging

### 4. Verification Steps

1. **Check Table Structure:**
   ```sql
   DESCRIBE token;
   ```
   You should see `token` and `refresh_token` columns as `TEXT` type.

2. **Test Authentication:**
   - Try signing in through the application
   - Verify tokens are being created successfully

3. **Monitor Logs:**
   - Check application logs for any remaining database errors
   - Look for successful token creation messages

### 5. Prevention Features Added

1. **Scheduled Cleanup:** Automatic daily cleanup at 2 AM
2. **Manual Cleanup Endpoints:** Admin endpoints for manual token cleanup
3. **Database Indexes:** Improved query performance
4. **Enhanced Logging:** Better monitoring of token operations

### 6. Admin Endpoints for Token Management

The following admin endpoints are now available:

- `POST /admin/tokens/cleanup/expired` - Clean expired tokens
- `POST /admin/tokens/cleanup/inactive?daysOld=7` - Clean inactive tokens
- `POST /admin/tokens/cleanup/user/{userId}` - Clean user's inactive tokens
- `POST /admin/tokens/cleanup/all` - Clean all expired and inactive tokens

### 7. Configuration Updates

Updated `application.yml` with:
- Better token expiration settings
- Environment variable support for signing keys
- Enhanced security configurations

## Post-Fix Verification

After applying the fix:

1. Restart the application
2. Test user authentication
3. Check database for proper token storage
4. Monitor application logs for any issues

## Future Maintenance

- Expired tokens are automatically cleaned up daily
- Use admin endpoints for manual cleanup when needed
- Monitor database size and token table growth
- Consider implementing token blacklisting for additional security

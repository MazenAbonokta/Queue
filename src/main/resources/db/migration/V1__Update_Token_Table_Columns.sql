-- Migration to update token table columns to support larger JWT tokens
-- This addresses the "Data too long for column 'token'" error

-- Update token column to TEXT type to support larger JWT tokens
ALTER TABLE token MODIFY COLUMN token TEXT;

-- Update refresh_token column to TEXT type to support larger refresh tokens
ALTER TABLE token MODIFY COLUMN refresh_token TEXT;

-- Add index on user_id for better performance
CREATE INDEX IF NOT EXISTS idx_token_user_id ON token(user_id);

-- Add index on is_active for better performance when querying active tokens
CREATE INDEX IF NOT EXISTS idx_token_is_active ON token(is_active);

-- Add composite index for user_id and is_active (most common query pattern)
CREATE INDEX IF NOT EXISTS idx_token_user_active ON token(user_id, is_active);

-- Add index on expires_at for cleanup operations
CREATE INDEX IF NOT EXISTS idx_token_expires_at ON token(expires_at);

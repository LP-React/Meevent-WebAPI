-- =====================================================
-- Replace the boolean email_verified column with a
-- more expressive verification status using CHECK
-- constraint to allow multiple verification states.
--
-- STATUS MEANINGS:
-- NOT_VERIFIED : User registered but never started verification
-- PENDING     : Verification process started but not completed
-- VERIFIED    : User successfully verified
-- =====================================================

-- Add new verification_status column
ALTER TABLE users
ADD COLUMN verification_status VARCHAR(20) NOT NULL
DEFAULT 'NOT_VERIFIED';

-- Add CHECK constraint to control allowed values
ALTER TABLE users
ADD CONSTRAINT chk_users_verification_status
CHECK (verification_status IN ('NOT_VERIFIED', 'PENDING', 'VERIFIED'));

-- Migrate existing data from legacy column
-- Users with email_verified = TRUE become VERIFIED
UPDATE users
SET verification_status = 'VERIFIED'
WHERE email_verified = TRUE;

-- Remove legacy boolean column
ALTER TABLE users
DROP COLUMN email_verified;
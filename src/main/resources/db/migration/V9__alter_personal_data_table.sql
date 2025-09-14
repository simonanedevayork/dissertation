ALTER TABLE personal_data
    ALTER COLUMN pd_participant_id TYPE VARCHAR(36);

ALTER TABLE personal_data
    ALTER COLUMN pd_email TYPE VARCHAR(320);

ALTER TABLE personal_data
    ALTER COLUMN pd_password TYPE VARCHAR(256);

-- Add role column with default value
ALTER TABLE personal_data
    ADD COLUMN pd_role VARCHAR(50) NOT NULL DEFAULT 'USER';

-- Optional: add comments for new/modified columns
COMMENT ON COLUMN personal_data.pd_participant_id IS 'The unique identifier assigned to a user.';
COMMENT ON COLUMN personal_data.pd_email IS 'The unique user email.';
COMMENT ON COLUMN personal_data.pd_password IS 'The user password.';
COMMENT ON COLUMN personal_data.pd_role IS 'The user role for Spring Security.';
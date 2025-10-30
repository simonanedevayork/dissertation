CREATE TABLE PERSONAL_DATA
(
    PD_PARTICIPANT_ID         VARCHAR(36)
        CONSTRAINT NN_PD_PARTICIPANT_ID NOT NULL,
    PD_EMAIL                  VARCHAR(320)
        CONSTRAINT UQ_PD_EMAIL UNIQUE,
    PD_PASSWORD               VARCHAR(256)
        CONSTRAINT NN_PD_PASSWORD NOT NULL,
    PD_ROLE                   VARCHAR(50)
        CONSTRAINT NN_PD_ROLE NOT NULL DEFAULT 'USER',
    PD_RESET_TOKEN            VARCHAR(255),
    PD_RESET_TOKEN_EXPIRATION TIMESTAMP(6),
    PD_ONBOARDING_COMPLETED   BOOLEAN,
    PD_CONSENT_GRANTED        BOOLEAN,
    PD_CONSENT_TIMESTAMP      TIMESTAMP(6),
    PD_CREATION_TS            TIMESTAMP(6)
);

ALTER TABLE PERSONAL_DATA
    ADD CONSTRAINT PK_PD_ID
        PRIMARY KEY (PD_PARTICIPANT_ID);

COMMENT ON TABLE PERSONAL_DATA IS 'This table provides the personal information about a user needed inside the Dog Health Tracker.';

COMMENT ON COLUMN PERSONAL_DATA.PD_PARTICIPANT_ID IS 'The unique identified assigned to a user.';
COMMENT ON COLUMN PERSONAL_DATA.PD_EMAIL IS 'The unique user email.';
COMMENT ON COLUMN PERSONAL_DATA.PD_PASSWORD IS 'The user password.';
COMMENT ON COLUMN PERSONAL_DATA.PD_ROLE IS 'The user role for Spring Security.';
COMMENT ON COLUMN PERSONAL_DATA.PD_RESET_TOKEN IS 'Temporary token used for password reset functionality.';
COMMENT ON COLUMN PERSONAL_DATA.PD_RESET_TOKEN_EXPIRATION IS 'Expiration timestamp for the password reset token.';
COMMENT ON COLUMN PERSONAL_DATA.PD_ONBOARDING_COMPLETED IS 'Indicates whether the user has completed the system onboarding process.';
COMMENT ON COLUMN PERSONAL_DATA.PD_CONSENT_GRANTED IS 'Indicates whether the user has granted consent for data processing and system usage.';
COMMENT ON COLUMN PERSONAL_DATA.PD_CONSENT_TIMESTAMP IS 'Timestamp when the user provided data processing consent.';
COMMENT ON COLUMN PERSONAL_DATA.PD_CREATION_TS IS 'Creation timestamp for user.';
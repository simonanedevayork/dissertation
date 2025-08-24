CREATE TABLE DOG
(
    DOG_ID VARCHAR(16) CONSTRAINT NN_DOG_ID NOT NULL,
    DOG_OWNER VARCHAR(16) CONSTRAINT NN_DOG_OWNER NOT NULL,
    DOG_NAME VARCHAR(100) CONSTRAINT NN_DOG_NAME NOT NULL,
    DOG_AGE INTEGER,
    DOG_GENDER VARCHAR(10),
    DOG_BREED VARCHAR(100),
    DOG_BIRTH_DATE DATE,
    DOG_HEIGHT NUMERIC(5,2),
    DOG_WIDTH NUMERIC(5,2),
    DOG_PHOTO VARCHAR(255),
    DOG_NURTURED BOOLEAN,
    DOG_QUIZ_COMPLETED BOOLEAN
);

-- Primary Key
ALTER TABLE DOG
    ADD CONSTRAINT PK_DOG_ID
        PRIMARY KEY (DOG_ID);

-- Foreign Key to PERSONAL_DATA
ALTER TABLE DOG
    ADD CONSTRAINT FK_DOG_OWNER
        FOREIGN KEY (DOG_OWNER)
            REFERENCES PERSONAL_DATA (PD_PARTICIPANT_ID);

-- Table level comment
COMMENT ON TABLE DOG IS 'This table stores information about the dog, owned by a user in the Dog Health Tracker.';

-- Column level comments
COMMENT ON COLUMN DOG.DOG_ID IS 'The unique identifier assigned to a dog.';
COMMENT ON COLUMN DOG.DOG_OWNER IS 'The unique ID of the user who owns the dog.';
COMMENT ON COLUMN DOG.DOG_NAME IS 'The name of the dog.';
COMMENT ON COLUMN DOG.DOG_AGE IS 'The age of the dog in years.';
COMMENT ON COLUMN DOG.DOG_GENDER IS 'The gender of the dog (e.g., Male, Female).';
COMMENT ON COLUMN DOG.DOG_BREED IS 'The breed of the dog.';
COMMENT ON COLUMN DOG.DOG_BIRTH_DATE IS 'The birth date of the dog.';
COMMENT ON COLUMN DOG.DOG_HEIGHT IS 'The height of the dog in centimeters.';
COMMENT ON COLUMN DOG.DOG_WIDTH IS 'The width of the dog in centimeters.';
COMMENT ON COLUMN DOG.DOG_PHOTO IS 'The URL or file path of the dog''s photo.';
COMMENT ON COLUMN DOG.DOG_NURTURED IS 'Indicates if the dog is neutered.';
COMMENT ON COLUMN DOG.DOG_QUIZ_COMPLETED IS 'Indicates if the health quiz for the dog has been completed.';
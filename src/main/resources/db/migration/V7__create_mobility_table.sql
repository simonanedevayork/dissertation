CREATE TABLE MOBILITY
(
    MB_ID VARCHAR(16) CONSTRAINT NN_MOBILITY_ID NOT NULL,
    MB_DOG_ID VARCHAR(16) CONSTRAINT NN_MOBILITY_DOG_ID NOT NULL,
    MB_LIMPING BOOLEAN,
    MB_RISES_LEG BOOLEAN,
    MB_LACK_OF_ACTIVITY BOOLEAN,
    MB_PLAYFULNESS BOOLEAN,
    MB_STANDING_DIFFICULTY BOOLEAN,
    MB_CLIMBING_STAIRS BOOLEAN,
    MB_JUMPING BOOLEAN,
    MB_CREATED_TS TIMESTAMP(6) CONSTRAINT NN_MOBILITY_CREATED_TS NOT NULL
);

ALTER TABLE MOBILITY
    ADD CONSTRAINT PK_MOBILITY_ID
        PRIMARY KEY (MB_ID);

ALTER TABLE MOBILITY
    ADD CONSTRAINT FK_MOBILITY_DOG
        FOREIGN KEY (MB_DOG_ID)
            REFERENCES DOG (DOG_ID);

COMMENT ON TABLE MOBILITY IS 'This table stores mobility health records for a dog.';

COMMENT ON COLUMN MOBILITY.MB_ID IS 'The unique identifier for this mobility record.';
COMMENT ON COLUMN MOBILITY.MB_DOG_ID IS 'The ID of the dog this mobility entry belongs to.';
COMMENT ON COLUMN MOBILITY.MB_LIMPING IS 'Boolean flag indicating if the dog is limping.';
COMMENT ON COLUMN MOBILITY.MB_RISES_LEG IS 'Boolean flag indicating if the dog can rise its leg normally.';
COMMENT ON COLUMN MOBILITY.MB_LACK_OF_ACTIVITY IS 'Boolean flag indicating lack of interest in activity or exercise.';
COMMENT ON COLUMN MOBILITY.MB_PLAYFULNESS IS 'Boolean flag indicating the level of playfulness of the dog.';
COMMENT ON COLUMN MOBILITY.MB_STANDING_DIFFICULTY IS 'Boolean flag indicating difficulty standing up from lying down.';
COMMENT ON COLUMN MOBILITY.MB_CLIMBING_STAIRS IS 'Boolean flag indicating difficulty climbing stairs.';
COMMENT ON COLUMN MOBILITY.MB_JUMPING IS 'Boolean flag indicating difficulty jumping.';
COMMENT ON COLUMN MOBILITY.MB_CREATED_TS IS 'The timestamp when this mobility record was created.';
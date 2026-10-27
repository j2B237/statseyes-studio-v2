ALTER TABLE athletes MODIFY COLUMN uuid CHAR(36) NOT NULL;
CREATE UNIQUE INDEX ux_athletes_uuid ON athletes(uuid);


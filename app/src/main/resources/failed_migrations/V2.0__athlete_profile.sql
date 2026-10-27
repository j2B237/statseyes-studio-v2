ALTER TABLE athletes ADD COLUMN dominant_foot VARCHAR(5) NOT NULL DEFAULT 'RIGHT';
ALTER TABLE athletes ADD COLUMN availability_status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE';
ALTER TABLE athletes ADD COLUMN availability_reason VARCHAR(255);
ALTER TABLE athletes ADD COLUMN availability_since DATE;
ALTER TABLE athletes ADD COLUMN availability_expected_return DATE;

ALTER TABLE athletes ADD CONSTRAINT ck_athletes_dominant_foot
    CHECK (athletes.dominant_foot in ('LEFT', 'RIGHT', 'BOTH'));

ALTER TABLE athletes ADD CONSTRAINT ck_athletes_availability_status
    CHECK (athletes.availability_status in ('AVAILABLE', 'INJURED', 'UNAVAILABLE'));

ALTER TABLE athletes ADD CONSTRAINT ck_athlete_availability_dates
    CHECK (athletes.availability_expected_return IS NULL OR athletes.availability_since IS NULL
        OR athletes.availability_expected_return >= athletes.availability_since);

CREATE TABLE athlete_secondary_position (
    athlete_id INTEGER NOT NULL,
    position_id INTEGER NOT NULL,
    PRIMARY KEY (athlete_id, position_id),
    CONSTRAINT fk_asp_athletes FOREIGN KEY (athlete_id) REFERENCES athletes(id) ON DELETE CASCADE,
    CONSTRAINT fk_asp_positions FOREIGN KEY (position_id) REFERENCES positions(id) ON DELETE CASCADE
);


CREATE INDEX idx_asp_positions ON athlete_secondary_position(position_id);

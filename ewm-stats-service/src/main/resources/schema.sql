CREATE TABLE IF NOT EXISTS HIT (
                                    hit_id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                    app VARCHAR(255) NOT NULL,
                                    uri VARCHAR(512) NOT NULL,
                                    ip VARCHAR(512) NOT NULL,
                                    time VARCHAR(512)
);
CREATE TABLE IF NOT EXISTS USERS (
                                    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                    user_name VARCHAR(255) NOT NULL,
                                    email VARCHAR(512) NOT NULL,
                                    CONSTRAINT pk_user PRIMARY KEY (user_id),
                                    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS CATEGORIES (
                                    category_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                    category_name VARCHAR,
                                    CONSTRAINT pk_category PRIMARY KEY (category_id),
                                    CONSTRAINT UQ_CATEGORY UNIQUE (category_name)
);

CREATE TABLE IF NOT EXISTS LOCATIONS (
                                    location_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                    lat FLOAT NOT NULL,
                                    lon FLOAT NOT NULL,
                                    name VARCHAR,
                                    CONSTRAINT pk_location PRIMARY KEY (location_id),
                                    CONSTRAINT UQ_COORDINATES UNIQUE (lat, lon)
);

CREATE TABLE IF NOT EXISTS EVENTS (
                                    event_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                    title VARCHAR NOT NULL,
                                    annotation VARCHAR NOT NULL,
                                    description VARCHAR NOT NULL,
                                    category_id BIGINT REFERENCES CATEGORIES(category_id),
                                    event_date TIMESTAMP,
                                    paid BOOLEAN,
                                    state VARCHAR,
                                    initiator BIGINT REFERENCES USERS(user_id) ON DELETE CASCADE,
                                    location BIGINT REFERENCES LOCATIONS(location_id),
                                    created_on TIMESTAMP,
                                    confirmed_requests INT,
                                    participant_limit INT,
                                    published_on TIMESTAMP,
                                    request_moderation BOOLEAN,
                                    CONSTRAINT pk_event PRIMARY KEY (event_id)
);

CREATE TABLE IF NOT EXISTS PARTICIPATION_REQUESTS (
                                    request_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                    created TIMESTAMP,
                                    event_id BIGINT REFERENCES EVENTS(event_id) ON DELETE CASCADE,
                                    requester BIGINT REFERENCES USERS(user_id) ON DELETE CASCADE,
                                    status VARCHAR(100),
                                    CONSTRAINT pk_request PRIMARY KEY (request_id),
                                    CONSTRAINT UQ_EVENT_REQUESTER UNIQUE (event_id, requester)
);

CREATE TABLE IF NOT EXISTS COMPILATIONS (
                                    compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                    pinned BOOLEAN,
                                    title VARCHAR,
                                    CONSTRAINT pk_compilation PRIMARY KEY (compilation_id)
);

CREATE TABLE IF NOT EXISTS COMPILATION_EVENTS (
                                            compilation_id BIGINT REFERENCES COMPILATIONS(compilation_id) ON DELETE CASCADE,
                                            event_id  BIGINT REFERENCES EVENTS(event_id) ON DELETE CASCADE,
                                            CONSTRAINT pk_compilation_event PRIMARY KEY (compilation_id, event_id)
);
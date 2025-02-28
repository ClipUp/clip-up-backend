CREATE TABLE clip_up.service_user
(
    id          VARCHAR(255) PRIMARY KEY,
    email       VARCHAR(255) UNIQUE NOT NULL,
    username    VARCHAR(100)        NOT NULL,
    create_time BIGINT              NOT NULL,
    update_time BIGINT              NOT NULL
);

CREATE TABLE clip_up.login_info
(
    id        VARCHAR(255) PRIMARY KEY,
    user_id   VARCHAR(255) NOT NULL,
    method    VARCHAR(50)  NOT NULL,
    login_key VARCHAR(255) NOT NULL,
    password  VARCHAR(255),
    CONSTRAINT unique_method_login_key UNIQUE (method, login_key)
);

CREATE TABLE clip_up.meeting
(
    id                  VARCHAR(255) PRIMARY KEY,
    owner_id            VARCHAR(255) NOT NULL,
    title               VARCHAR(255) NOT NULL,
    audio_file_url      TEXT,
    audio_file_duration INTEGER,
    script              TEXT,
    minutes             TEXT,
    create_time         BIGINT       NOT NULL,
    update_time         BIGINT       NOT NULL,
    is_deleted          BOOLEAN DEFAULT FALSE
);
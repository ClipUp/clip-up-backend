CREATE SCHEMA clip_up;

CREATE TABLE clip_up.service_user
(
    id          VARCHAR(20) PRIMARY KEY,
    email       VARCHAR(100) UNIQUE NOT NULL,
    username    VARCHAR(20)         NOT NULL,
    create_time BIGINT              NOT NULL,
    update_time BIGINT              NOT NULL
);

CREATE TABLE clip_up.login_info
(
    id        VARCHAR(20) PRIMARY KEY,
    user_id   VARCHAR(20)  NOT NULL,
    method    VARCHAR(10)  NOT NULL,
    login_key VARCHAR(255) NOT NULL,
    password  VARCHAR(255),
    CONSTRAINT unique_method_login_key UNIQUE (method, login_key)
);

CREATE TABLE clip_up.meeting
(
    id                  VARCHAR(20) PRIMARY KEY,
    owner_id            VARCHAR(20) NOT NULL,
    title               VARCHAR(40) NOT NULL,
    audio_file_url      TEXT,
    audio_file_duration INTEGER,
    script              TEXT,
    minutes             TEXT,
    create_time         BIGINT      NOT NULL,
    update_time         BIGINT      NOT NULL,
    is_deleted          BOOLEAN
);

CREATE TABLE clip_up.service_session
(
    id          VARCHAR(20) PRIMARY KEY,
    user_id     VARCHAR(20)         NOT NULL,
    token       VARCHAR(255) UNIQUE NOT NULL,
    is_blocked  BOOLEAN,
    create_time BIGINT              NOT NULL,
    update_time BIGINT              NOT NULL
);
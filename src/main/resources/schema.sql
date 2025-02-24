create table user (
    id          bigint       not null   primary key,
    email       varchar(100) not null,
    username    varchar(50) not null,
    create_time bigint       not null,
    update_time bigint       not null,
    unique (email)
);

create table login_info (
    id        bigint         not null   primary key,
    user_id   bigint         not null,
    method    enum ('EMAIL', 'NAVER') not null,
    login_key varchar(255)   not null,
    password  varchar(255)   null,
    unique (method, login_key)
);

create table note (
    id        bigint         not null   primary key,
    title     varchar(40),
    script    longtext,
    audio_file_url  varchar(255),
    content     longtext,
    is_finished tinyint(1)   not null,
    create_time bigint       not null,
    update_time bigint       not null,
    is_deleted  tinyint(1)   not null
);
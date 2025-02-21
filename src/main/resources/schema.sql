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
create table user
(
    id         bigint auto_increment
        primary key,
    full_name  varchar(255)                       not null,
    email      varchar(20)                        not null,
    created_at datetime default CURRENT_TIMESTAMP not null
);
create table wallet
(
    id         bigint auto_increment
        primary key,
    user_id    bigint                             not null,
    balance    decimal(18, 2)                     not null,
    currency   varchar(10)                        not null,
    created_at datetime default current_timestamp not null,
    constraint wallet_user_id_fk
        foreign key (user_id) references user (id)
);
create table refresh_token
(
    id         bigint auto_increment
        primary key,
    token      bigint                             not null,
    user_id    bigint                             not null,
    created_at datetime default CURRENT_TIMESTAMP not null,
    expired_at datetime default current_timestamp not null,
    constraint refresh_token_user_id_fk
        foreign key (user_id) references user (id)
);

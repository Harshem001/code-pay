create table payment_code
(
    id          bigint auto_increment
        primary key,
    code        varchar(10)                                       not null comment 'generated secure code',
    sender_id   bigint                                            not null comment 'link to user table',
    receiver_id bigint                                            null comment 'the redeemer user',
    amount      decimal(18, 2)                                    not null comment 'intended transfer amount',
    status      enum ('PENDING', 'REDEEMED', 'EXPIRED', 'FAILED')  default 'PENDING'  not null comment 'state of the code',
    created_at  datetime default CURRENT_TIMESTAMP                not null comment 'when code was created',
    expire_at   datetime default current_timestamp                not null comment 'when code will expire (after 24hrs)',
    redeemed_at datetime default current_timestamp                null comment 'when code was redeemed',
    constraint payment_code_user_id_fk
        foreign key (sender_id) references user (id)
);


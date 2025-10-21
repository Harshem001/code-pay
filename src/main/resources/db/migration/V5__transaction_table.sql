create table transactions
(
    id               bigint auto_increment
        primary key,
    wallet_id        bigint                                                             not null,
    reference        varchar(10)                                                        not null,
    amount           decimal(18, 2)                                                     not null,
    status           ENUM ('PENDING', 'SUCCESSFUL', 'FAILED') default 'PENDING'         not null,
    transaction_type ENUM ('CREDITED', 'DEBITED')             default 'CREDITED'        not null,
    date             datetime                                 default current_timestamp not null,
    constraint transactions_wallet_id_fk
        foreign key (wallet_id) references wallet (id)
);
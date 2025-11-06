alter table transactions
    change transaction_type transaction_entry ENUM ('CREDITED', 'DEBITED') default 'CREDITED' not null;

alter table transactions
    add transaction_type ENUM ('WITHDRAWAL', 'TRANSFER', 'DEPOSIT', 'CODE_TRANSFER') default 'DEPOSIT' not null comment 'withdrawal, deposit, transfer, code_transfer';

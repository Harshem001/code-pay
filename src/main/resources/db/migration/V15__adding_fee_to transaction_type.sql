alter table transactions
    modify transaction_type enum ('WITHDRAWAL', 'TRANSFER', 'DEPOSIT', 'CODE_TRANSFER', 'FEE') default 'DEPOSIT' not null comment 'withdrawal, deposit, transfer, code_transfer';

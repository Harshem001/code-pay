alter table user
    add user_status ENUM ('ACTIVE', 'INACTIVE') default 'INACTIVE' not null;

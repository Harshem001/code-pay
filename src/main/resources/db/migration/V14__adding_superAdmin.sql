alter table user
    modify roles ENUM ('USER', 'ADMIN', 'SUPERADMIN') default 'USER' not null;

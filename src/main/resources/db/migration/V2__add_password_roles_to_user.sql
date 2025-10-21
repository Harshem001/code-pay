alter table user
    add password varchar(255) not null;

alter table user
    add roles varchar(20) default 'USER' not null;
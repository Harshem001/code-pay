alter table user
    add password varchar(255) not null;

alter table user
    add roles varchar(20) default 'USER' not null;

alter table user
    add address varchar(255) not null;

alter table user
    add date_of_birth date not null;

alter table user
    add phone_Number varchar(255) not null;

alter table user
    add gender varchar(20) default 'MALE' not null comment 'MALE OR FEMALE';

alter table user
    add bvn varchar(11) not null comment 'must be 11 digits';
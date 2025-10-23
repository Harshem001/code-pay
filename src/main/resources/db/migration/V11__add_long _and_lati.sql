alter table payment_code
    add target_lat decimal(10, 7) not null;

alter table payment_code
    add target_lon decimal(10, 7) not null;

alter table payment_code
    add radius_meters bigint not null comment 'min of 1000, max of 3000';

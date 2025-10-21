alter table wallet
drop foreign key wallet_user_id_fk;

alter table wallet
drop column user_id;

alter table wallet
    add constraint wallet_user_id_fk
        foreign key (id) references user (id);
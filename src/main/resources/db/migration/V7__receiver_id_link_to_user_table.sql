alter table payment_code
    add constraint payment_code_user_id_fk_2
        foreign key (receiver_id) references user (id);

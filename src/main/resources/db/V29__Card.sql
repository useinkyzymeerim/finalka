create sequence card_seq start with 1 increment by 1;

create table card (
                      active boolean not null,
                      bank_account_id bigint,
                      id bigint not null,
                      user_id bigint,
                      card_number varchar(16),
                      card_holder_name varchar(255),
                      cvv varchar(255),
                      expiry_date varchar(255),
                      primary key (id)
);

alter table if exists card
    add constraint FK87o2vhvm08af0vd5y0sp9mccd
    foreign key (bank_account_id)
    references bank_account;

alter table if exists card
    add constraint FKq5apcc4ddrab8t48q2uqvyquq
    foreign key (user_id)
    references users;
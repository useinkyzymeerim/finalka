create sequence bank_account_seq start with 1 increment by 1;

create table bank_account (
                              balance numeric(38,2) not null,
                              id bigint not null,
                              account_number varchar(255) not null,
                              primary key (id)
);
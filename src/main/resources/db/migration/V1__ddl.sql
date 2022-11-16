create sequence public.hibernate_sequence;

create table users (
       id int8 not null,
        date_of_birth date,
        name varchar(255),
        primary key (id)
    );
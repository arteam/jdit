create table players(
    id bigint not null identity,
    first_name varchar(128) not null,
    last_name varchar(128) not null,
    birth_date date not null,
    weight int null,
    height int null
);
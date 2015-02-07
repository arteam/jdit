create table players(
    id  identity,
    first_name varchar(128) not null,
    last_name varchar(128) not null,
    birth_date date not null,
    weight int null,
    height int null
);

create table divisions(
  name varchar(32) primary key
);

insert into divisions values 'PACIFIC', 'CENTRAL', 'METROPOLITAN', 'ATLANTIC';

create table teams(
   id  identity,
   name varchar(128) not null,
   division varchar(32) not null,
   foreign key (division) references divisions(name)
);

create table roster(
  team_id bigint not null ,
  player_id bigint not null ,
  foreign key (team_id) references teams(id),
  foreign key (player_id) references players(id)
);
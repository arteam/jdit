create sequence players_id_seq as integer;
create table players (
  id         integer      not null default nextval('players_id_seq') primary key,
  first_name varchar(128) not null,
  last_name  varchar(128) not null,
  birth_date date         not null,
  weight     int,
  height     int
);

create table divisions (
  name varchar(32) primary key
);

create sequence teams_id_seq as integer;
create table teams (
  id       int          not null default nextval('teams_id_seq') primary key,
  name     varchar(128) not null,
  division varchar(32)  not null,
  foreign key (division) references divisions (name)
);

create table roster (
  team_id   int not null,
  player_id int not null,
  foreign key (team_id) references teams (id),
  foreign key (player_id) references players (id),
  primary key (team_id, player_id)
);

create table players (
  id         int          not null auto_increment,
  first_name varchar(128) not null,
  last_name  varchar(128) not null,
  birth_date date         not null,
  weight     int,
  height     int,
  primary key (id)
);

create table divisions (
  name varchar(32),
  primary key (name)
);

create table teams (
  id       int          not null auto_increment,
  name     varchar(128) not null,
  division varchar(32)  not null,
  primary key (id),
  foreign key (division) references divisions (name)
);

create table roster (
  team_id   int not null,
  player_id int not null,
  foreign key (team_id) references teams (id),
  foreign key (player_id) references players (id),
  primary key (team_id, player_id)
);

create table roster(
  team_id bigint not null ,
  player_id bigint not null ,
  foreign key (team_id) references teams(id),
  foreign key (player_id) references players(id)
);
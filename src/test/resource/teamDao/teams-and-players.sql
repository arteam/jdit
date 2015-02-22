insert into divisions values 'PACIFIC', 'CENTRAL', 'METROPOLITAN', 'ATLANTIC';

insert into players(id, first_name, last_name, birth_date, weight, height)
  values (18, 'Vladimir','Tarasenko', '1991-08-05', 99, 184);
insert into players(id, first_name, last_name, birth_date, weight, height)
  values (19, 'Jake','Allen', '1990-08-07', 92, 188);
insert into players(id, first_name, last_name, birth_date, weight, height)
  values (21, 'Jaden','Schwartz', '1992-06-25', 82, 177);

insert into teams(id, name, division) values (1, 'St. Louis Blues', 'CENTRAL');

insert into roster(team_id, player_id) values (1, 18), (1,19), (1, 21);
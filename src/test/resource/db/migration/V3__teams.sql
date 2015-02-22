create table teams(
  id  identity,
  name varchar(128) not null,
  division varchar(32) not null,
  foreign key (division) references divisions(name)
);
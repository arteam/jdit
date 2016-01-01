create table teams(
  id  serial primary key ,
  name varchar(128) not null,
  division varchar(32) not null,
  foreign key (division) references divisions(name)
);
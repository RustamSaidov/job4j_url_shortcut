create table person (
    id serial primary key not null,
    site VARCHAR(255) not null,
    login varchar (2000) not null unique,
    password varchar(2000)
);



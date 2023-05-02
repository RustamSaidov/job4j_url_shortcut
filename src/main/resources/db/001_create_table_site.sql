CREATE TABLE site (
    id serial primary key not null,
    site_line VARCHAR(255) not null unique,
    login varchar (100) not null unique,
    password varchar(100) not null
);



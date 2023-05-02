CREATE TABLE url_entity (
  id serial primary key,
  url_line varchar not null unique,
  converted_url varchar not null unique,
  total int not null,
  site_id int not null references site(id)
);

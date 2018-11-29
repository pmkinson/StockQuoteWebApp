CREATE TABLE searches (
  id             INT GENERATED ALWAYS AS IDENTITY,
  type_of_search int,
  user_id        int,
  stock_symbol   CHAR(6) NOT NULL,
  family         char(35),
  family_version char(35),
  os             CHAR(35),
  os_version     char(35),
  device         char(35),
  time_stamp     TIMESTAMP
);

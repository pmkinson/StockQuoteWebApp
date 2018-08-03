create table stockquote.stocks
(
  id             serial    not null primary key,
  date           timestamp not null,
  stock_id       integer   not null,
  system_id      integer   not null,
  browser_id     integer   not null,
  user_id        integer   not null,
  type_of_search integer   not null,
  stock_symbol   varchar(4)
);
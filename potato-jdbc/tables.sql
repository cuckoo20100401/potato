CREATE TABLE electronic_book (
  id character(32),
  name character varying(100),
  author_name character varying(100),
  size integer,
  price1 numeric(10,2),
  price2 numeric(10,2),
  create_date date,
  create_time timestamp without time zone,
  PRIMARY KEY (id)
);




# --- !Ups

create table "accumulations" (userID VARCHAR NOT NULL PRIMARY KEY,mantraId VARCHAR NOT NULL, count BIGINT NOT NULL, year INT NOT NULL, month INT NOT NULL, day INT NOT NULL);


# --- !Downs

drop table "accumulations";
# --- !Ups

create table "mantra" ("mantraID" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR UNIQUE,"description" VARCHAR,"image_url" VARCHAR);


# --- !Downs

drop table "mantra";
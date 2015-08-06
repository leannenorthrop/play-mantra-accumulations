# --- !Ups
drop table "mantra";
create table "mantra" ("mantraID" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR UNIQUE  NOT NULL,"description" VARCHAR  NOT NULL,"image_url" VARCHAR NOT NULL, "year" INT  NOT NULL, "month"  INT NOT NULL, "day" INT NOT NULL, "is_archived" INT  NOT NULL);
INSERT INTO "mantra" ("name","description","image_url", "year", "month", "day", "is_archived") VALUES ('Short Chenrezig','No description','http://www.buddhadordenma.org/images/chenrezig-thangka.jpg', 2015, 8, 6, 0);


# --- !Downs

drop table "mantra";
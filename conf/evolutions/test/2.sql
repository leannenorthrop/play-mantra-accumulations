# --- !Ups
create table "mantra" ("id" IDENTITY NOT NULL PRIMARY KEY,"name" VARCHAR UNIQUE  NOT NULL,"description" VARCHAR  NOT NULL,"image_url" VARCHAR NOT NULL, "y" INT  NOT NULL, "m"  INT NOT NULL, "d" INT NOT NULL, "is_archived" INT  NOT NULL);
INSERT INTO "mantra" ("name","description","image_url", "y", "m", "d", "is_archived") VALUES ('Short Chenrezig','No description','http://www.buddhadordenma.org/images/chenrezig-thangka.jpg', 2015, 8, 6, 0);

create table "accumulations" ("id" IDENTITY NOT NULL PRIMARY KEY, user_id VARCHAR NOT NULL, mantra_id BIGINT NOT NULL, gathering_id BIGINT NOT NULL, amount BIGINT NOT NULL, y INT NOT NULL, m INT NOT NULL, d INT NOT NULL);
CREATE INDEX acc_mantra_index ON accumulations (mantra_id);
CREATE INDEX acc_mantra_gathering_user_index ON accumulations (mantra_id, user_id, gathering_id);
CREATE INDEX acc_mantra_year_index ON accumulations (y);
CREATE INDEX acc_mantra_month_index ON accumulations (m);
CREATE INDEX acc_mantra_day_index ON accumulations (d);

# --- !Downs

drop table "accumulations";
drop table "mantra";
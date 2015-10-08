# --- !Ups
create table "mantra" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR UNIQUE  NOT NULL,"description" TEXT NOT NULL,"image_url" VARCHAR NOT NULL, "y" INT  NOT NULL, "m"  INT NOT NULL, "d" INT NOT NULL, "is_archived" INT  NOT NULL);
INSERT INTO "mantra" ("name","description","image_url", "y", "m", "d", "is_archived") VALUES ('Short Chenrezig','No description','http://www.buddhadordenma.org/images/chenrezig-thangka.jpg', 2015, 8, 6, 0);

create table "gatherings" ("id" BIGSERIAL NOT NULL PRIMARY KEY, user_id VARCHAR NOT NULL, "name" VARCHAR UNIQUE NOT NULL, "dedication" TEXT NOT NULL, is_achieved INT NOT NULL, is_private INT NOT NULL, is_archived INT NOT NULL, y INT NOT NULL, m INT NOT NULL, d INT NOT NULL);
CREATE INDEX gatherings_user_index ON "gatherings" (user_id);
INSERT INTO "gatherings" ("user_id", "name", "dedication", "is_achieved", "is_private", "is_archived", "y", "m", "d") VALUES ('1bf95b31-f034-4fdf-8345-22e6169a379b','Mani','Dedicated to all sentient beings', 0, 0, 0, 2015, 8, 6);

create table "accumulations" ("id" BIGSERIAL NOT NULL PRIMARY KEY, user_id VARCHAR NOT NULL, mantra_id BIGINT NOT NULL, gathering_id BIGINT NOT NULL, amount BIGINT NOT NULL, y INT NOT NULL, m INT NOT NULL, d INT NOT NULL, FOREIGN KEY (mantra_id) REFERENCES "mantra"("id"), FOREIGN KEY (gathering_id) REFERENCES "gatherings"("id"));
CREATE INDEX acc_mantra_index ON "accumulations" (mantra_id);
CREATE INDEX acc_mantra_gathering_user_index ON "accumulations" (mantra_id, user_id, gathering_id);
CREATE INDEX acc_mantra_year_index ON "accumulations" (y);
CREATE INDEX acc_mantra_month_index ON "accumulations" (m);
CREATE INDEX acc_mantra_day_index ON "accumulations" (d);

create table "goals" ("gathering_id" BIGINT NOT NULL, "mantra_id" BIGINT NOT NULL, "goal" INT NOT NULL, is_achieved INT NOT NULL, is_archived INT NOT NULL, PRIMARY KEY(gathering_id,mantra_id), FOREIGN KEY (mantra_id) REFERENCES "mantra"("id"), FOREIGN KEY (gathering_id) REFERENCES "gatherings"("id"));

# --- !Downs
drop table "goals";
drop table "accumulations";
drop table "gatherings";
drop table "mantra";


# --- !Ups

create table "accumulations" ("id" BIGSERIAL NOT NULL PRIMARY KEY, user_id VARCHAR NOT NULL, mantra_id BIGINT NOT NULL, gathering_id BIGINT NOT NULL, count BIGINT NOT NULL, year INT NOT NULL, month INT NOT NULL, day INT NOT NULL);
CREATE INDEX acc_mantra_index ON accumulations (mantra_id);
CREATE INDEX acc_mantra_gathering_user_index ON accumulations (mantra_id, user_id, gathering_id);
CREATE INDEX acc_mantra_year_index ON accumulations (year);
CREATE INDEX acc_mantra_month_index ON accumulations (month);
CREATE INDEX acc_mantra_day_index ON accumulations (day);

# --- !Downs

drop table "accumulations";
ALTER TABLE students ADD CONSTRAINT age_constraint CHECK (age >= 16);
ALTER TABLE students ADD CONSTRAINT name_is_unique_constraint UNIQUE (name);
ALTER TABLE students ALTER COLUMN name SET NOT NULL;
ALTER TABLE faculties ADD CONSTRAINT name_and_color_pair_is_unique_constraint UNIQUE (name, color);
ALTER TABLE students ALTER COLUMN age SET DEFAULT 20;
-- liquibase formatted sql

-- changeset alevin:1
CREATE INDEX student_name_index ON students (name);
-- changeset alevin:2
CREATE INDEX faculty_name_and_color_index ON faculties (name, color);
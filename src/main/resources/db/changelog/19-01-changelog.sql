-- liquibase formatted sql

-- changeset rklim:1760870061037-1
CREATE SEQUENCE IF NOT EXISTS attendance_id_seq START WITH 1 INCREMENT BY 1;

-- changeset rklim:1760870061037-2
CREATE SEQUENCE IF NOT EXISTS attendance_status_id_seq START WITH 1 INCREMENT BY 1;

-- changeset rklim:1760870061037-3
CREATE SEQUENCE IF NOT EXISTS class_type_id_seq START WITH 1 INCREMENT BY 1;

-- changeset rklim:1760870061037-4
CREATE SEQUENCE IF NOT EXISTS course_id_seq START WITH 1 INCREMENT BY 1;

-- changeset rklim:1760870061037-5
CREATE SEQUENCE IF NOT EXISTS group_id_seq START WITH 1 INCREMENT BY 1;

-- changeset rklim:1760870061037-6
CREATE SEQUENCE IF NOT EXISTS role_id_seq START WITH 1 INCREMENT BY 1;

-- changeset rklim:1760870061037-7
CREATE SEQUENCE IF NOT EXISTS schedule_id_seq START WITH 1 INCREMENT BY 1;

-- changeset rklim:1760870061037-8
CREATE SEQUENCE IF NOT EXISTS student_id_seq START WITH 1 INCREMENT BY 1;

-- changeset rklim:1760870061037-9
CREATE SEQUENCE IF NOT EXISTS teacher_id_seq START WITH 1 INCREMENT BY 1;

-- changeset rklim:1760870061037-10
CREATE SEQUENCE IF NOT EXISTS user_id_seq START WITH 1 INCREMENT BY 1;

-- changeset rklim:1760870061037-11
CREATE TABLE attendance
(
    id          BIGINT NOT NULL,
    schedule_id BIGINT,
    student_id  BIGINT,
    teacher_id  BIGINT,
    marked_time time WITHOUT TIME ZONE,
    CONSTRAINT pk_attendance PRIMARY KEY (id)
);

-- changeset rklim:1760870061037-12
CREATE TABLE attendance_status
(
    id                     BIGINT NOT NULL,
    attendance_status_name VARCHAR(255),
    CONSTRAINT pk_attendance_status PRIMARY KEY (id)
);

-- changeset rklim:1760870061037-13
CREATE TABLE class_types
(
    id   BIGINT NOT NULL,
    name VARCHAR(255),
    CONSTRAINT pk_class_types PRIMARY KEY (id)
);

-- changeset rklim:1760870061037-14
CREATE TABLE courses
(
    id            BIGINT NOT NULL,
    name          VARCHAR(255),
    lecture_count INTEGER,
    seminar_count INTEGER,
    lab_count     INTEGER,
    CONSTRAINT pk_courses PRIMARY KEY (id)
);

-- changeset rklim:1760870061037-15
CREATE TABLE groups
(
    id                 BIGINT NOT NULL,
    name               VARCHAR(255),
    year_created       INTEGER,
    count_students     INTEGER,
    max_count_students INTEGER,
    CONSTRAINT pk_groups PRIMARY KEY (id)
);

-- changeset rklim:1760870061037-16
CREATE TABLE roles
(
    id   BIGINT NOT NULL,
    name VARCHAR(255),
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

-- changeset rklim:1760870061037-17
CREATE TABLE schedule
(
    id            BIGINT NOT NULL,
    date          date,
    start_time    time WITHOUT TIME ZONE,
    end_time      time WITHOUT TIME ZONE,
    group_id      BIGINT,
    course_id     BIGINT,
    teacher_id    BIGINT,
    class_type_id BIGINT,
    CONSTRAINT pk_schedule PRIMARY KEY (id)
);

-- changeset rklim:1760870061037-18
CREATE TABLE students
(
    id          BIGINT NOT NULL,
    last_name   VARCHAR(255),
    name        VARCHAR(255),
    middle_name VARCHAR(255),
    email       VARCHAR(255),
    phone       VARCHAR(255),
    group_id    BIGINT,
    CONSTRAINT pk_students PRIMARY KEY (id)
);

-- changeset rklim:1760870061037-19
CREATE TABLE teachers
(
    id          BIGINT NOT NULL,
    last_name   VARCHAR(255),
    name        VARCHAR(255),
    middle_name VARCHAR(255),
    email       VARCHAR(255),
    phone       VARCHAR(255),
    CONSTRAINT pk_teachers PRIMARY KEY (id)
);

-- changeset rklim:1760870061037-20
CREATE TABLE users
(
    id         BIGINT NOT NULL,
    username   VARCHAR(255),
    password   VARCHAR(255),
    role_id    BIGINT,
    student_id BIGINT,
    teacher_id BIGINT,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

-- changeset rklim:1760870061037-21
ALTER TABLE users
    ADD CONSTRAINT uc_users_student UNIQUE (student_id);

-- changeset rklim:1760870061037-22
ALTER TABLE users
    ADD CONSTRAINT uc_users_teacher UNIQUE (teacher_id);

-- changeset rklim:1760870061037-23
ALTER TABLE attendance
    ADD CONSTRAINT FK_ATTENDANCE_ON_SCHEDULE FOREIGN KEY (schedule_id) REFERENCES schedule (id);

-- changeset rklim:1760870061037-24
ALTER TABLE attendance
    ADD CONSTRAINT FK_ATTENDANCE_ON_STUDENT FOREIGN KEY (student_id) REFERENCES students (id);

-- changeset rklim:1760870061037-25
ALTER TABLE attendance
    ADD CONSTRAINT FK_ATTENDANCE_ON_TEACHER FOREIGN KEY (teacher_id) REFERENCES attendance_status (id);

-- changeset rklim:1760870061037-26
ALTER TABLE schedule
    ADD CONSTRAINT FK_SCHEDULE_ON_CLASS_TYPE FOREIGN KEY (class_type_id) REFERENCES class_types (id);

-- changeset rklim:1760870061037-27
ALTER TABLE schedule
    ADD CONSTRAINT FK_SCHEDULE_ON_COURSE FOREIGN KEY (course_id) REFERENCES courses (id);

-- changeset rklim:1760870061037-28
ALTER TABLE schedule
    ADD CONSTRAINT FK_SCHEDULE_ON_GROUP FOREIGN KEY (group_id) REFERENCES groups (id);

-- changeset rklim:1760870061037-29
ALTER TABLE schedule
    ADD CONSTRAINT FK_SCHEDULE_ON_TEACHER FOREIGN KEY (teacher_id) REFERENCES teachers (id);

-- changeset rklim:1760870061037-30
ALTER TABLE students
    ADD CONSTRAINT FK_STUDENTS_ON_GROUP FOREIGN KEY (group_id) REFERENCES groups (id);

-- changeset rklim:1760870061037-31
ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ROLE FOREIGN KEY (role_id) REFERENCES roles (id);

-- changeset rklim:1760870061037-32
ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_STUDENT FOREIGN KEY (student_id) REFERENCES students (id);

-- changeset rklim:1760870061037-33
ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_TEACHER FOREIGN KEY (teacher_id) REFERENCES teachers (id);


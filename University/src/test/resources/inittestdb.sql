DROP TABLE IF EXISTS groups CASCADE;
CREATE TABLE groups(
group_id IDENTITY NOT NULL PRIMARY KEY,
group_name VARCHAR(5) NOT NULL);
DROP TABLE IF EXISTS students CASCADE;
CREATE TABLE students(
student_id IDENTITY NOT NULL PRIMARY KEY,
group_id INT,
first_name VARCHAR(30) NOT NULL,
last_name VARCHAR(50) NOT NULL,
CONSTRAINT FK_group_id FOREIGN KEY (group_id) REFERENCES groups (group_id) ON DELETE CASCADE);
DROP TABLE IF EXISTS courses CASCADE;
CREATE TABLE courses(
course_id IDENTITY NOT NULL PRIMARY KEY,
course_name VARCHAR(50) NOT NULL,
course_description VARCHAR(250) NOT NULL);
DROP TABLE IF EXISTS students_to_courses CASCADE;
CREATE TABLE students_to_courses(
student_id INT NOT NULL,
course_id INT NOT NULL,
CONSTRAINT FK_student_id FOREIGN KEY (student_id) REFERENCES students (student_id) ON DELETE CASCADE,
CONSTRAINT FK_course_id FOREIGN KEY (course_id) REFERENCES courses (course_id) ON DELETE CASCADE,
CONSTRAINT PK_student_id_course_id PRIMARY KEY(student_id, course_id));
INSERT INTO groups VALUES(1, 'AB-22');
INSERT INTO groups VALUES(2, 'FR-33');
INSERT INTO students VALUES(1, 1, 'Bob', 'Morris');
INSERT INTO students VALUES(2, 1, 'Rob', 'Brown');
INSERT INTO students VALUES(3, 1, 'Ann', 'Wood');
INSERT INTO students VALUES(4, 2, 'Jonh', 'Stark');
INSERT INTO students VALUES(5, 2, 'Tom', 'Lee');
INSERT INTO students VALUES(6, 2, 'Jinn', 'Grey');
INSERT INTO courses VALUES(1, 'Law', 'test-courses');
INSERT INTO courses VALUES(2, 'Math','test-courses');
INSERT INTO courses VALUES(3, 'Art','test-courses');
INSERT INTO students_to_courses (student_id, course_id) VALUES(1,1);
INSERT INTO students_to_courses (student_id, course_id) VALUES(1,2);
INSERT INTO students_to_courses (student_id, course_id) VALUES(1,3);
INSERT INTO students_to_courses (student_id, course_id) VALUES(2,1);
INSERT INTO students_to_courses (student_id, course_id) VALUES(3,1);
INSERT INTO students_to_courses (student_id, course_id) VALUES(4,1);
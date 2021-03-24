DROP TABLE IF EXISTS groups CASCADE;
CREATE TABLE groups(
group_id SERIAL PRIMARY KEY,
group_name VARCHAR(5) NOT NULL
);
DROP TABLE IF EXISTS students CASCADE;
CREATE TABLE students(
student_id SERIAL PRIMARY KEY,
group_id INT REFERENCES groups (group_id) ON DELETE CASCADE,
first_name VARCHAR(30) NOT NULL,
last_name VARCHAR(50) NOT NULL 
);
DROP TABLE IF EXISTS courses CASCADE;
CREATE TABLE courses(
course_id SERIAL PRIMARY KEY,
course_name VARCHAR(50) NOT NULL,
course_description TEXT NOT NULL
);
DROP TABLE IF EXISTS students_to_courses CASCADE;
CREATE TABLE students_to_courses(
student_id INT REFERENCES students(student_id) ON DELETE CASCADE,
course_id INT REFERENCES courses(course_id) ON DELETE CASCADE,
PRIMARY KEY(student_id,course_id)
);
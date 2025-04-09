-- Database initialization script for academic database

-- Delete tables if they exist

DROP TABLE IF EXISTS "major" CASCADE;
DROP TABLE IF EXISTS "course_group" CASCADE;
DROP TABLE IF EXISTS "course" CASCADE;
DROP TABLE IF EXISTS "student" CASCADE;
DROP TABLE IF EXISTS "course_taken" CASCADE;
DROP TABLE IF EXISTS "major_requirement" CASCADE;

-- Note: The CASCADE option is used to automatically drop objects that depend on the table being dropped.


-- Create tables in the correct order to handle foreign key dependencies

-- Create Major table
CREATE TABLE "major" (
    id SERIAL PRIMARY KEY,
    code VARCHAR NOT NULL,
    title VARCHAR NOT NULL,
    CONSTRAINT unique_major_code UNIQUE(code)
);

-- Create Course Group table
CREATE TABLE "course_group" (
    id SERIAL PRIMARY KEY,
    code VARCHAR NOT NULL,
    title VARCHAR NOT NULL,
    CONSTRAINT unique_course_group_code UNIQUE(code)
);

-- Create Course table
CREATE TABLE "course" (
    id SERIAL PRIMARY KEY,
    title VARCHAR NOT NULL,
    code VARCHAR NOT NULL,
    min_credits INTEGER NOT NULL,
    max_credits INTEGER NOT NULL,
    requisite_string VARCHAR NOT NULL,
    course_group_id INTEGER REFERENCES "course_group"(id),
    CONSTRAINT unique_course_code UNIQUE(code)
);

-- Create Student table
CREATE TABLE "student" (
    id SERIAL PRIMARY KEY,
    email VARCHAR NOT NULL,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL
);

CREATE TABLE "authentication" (
    id SERIAL PRIMARY KEY,
    code VARCHAR NOT NULL,
    created_at TIMESTAMP NOT NULL,
    student_id INTEGER REFERENCES "student"(id)
);

-- Create Course Taken table
CREATE TABLE "course_taken" (
    id SERIAL PRIMARY KEY,
    course_id INTEGER REFERENCES "course"(id),
    student_id INTEGER REFERENCES "student"(id)
);

-- Create Major Requirement table
CREATE TABLE "major_requirement" (
    id SERIAL PRIMARY KEY,
    number INTEGER NOT NULL,
    credits INTEGER NOT NULL,
    required_course JSONB NOT NULL,
    major_id INTEGER REFERENCES "major"(id),
    CONSTRAINT unique_requirement_number_per_major UNIQUE(major_id, number)
);

-- Add indexes for foreign keys to improve query performance
CREATE INDEX idx_course_course_group_id ON "course" (course_group_id);
CREATE INDEX idx_course_taken_course_id ON "course_taken" (course_id);
CREATE INDEX idx_course_taken_student_id ON "course_taken" (student_id);
CREATE INDEX idx_authentication_student_id ON "authentication" (student_id);
CREATE INDEX idx_major_requirement_major_id ON "major_requirement" (major_id);

-- Add comment to explain the JSON structure
COMMENT ON COLUMN "major_requirement"."required_course" IS 'Stores the required course information in JSON format';
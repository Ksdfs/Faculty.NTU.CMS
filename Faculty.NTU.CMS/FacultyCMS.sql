CREATE DATABASE FacultyCMS;

USE FacultyCMS;

CREATE TABLE page (
    id INT PRIMARY KEY IDENTITY(1,1),
    title NVARCHAR(255),
    url NVARCHAR(255),
    last_updated NVARCHAR(50),
    status NVARCHAR(50)
);

INSERT INTO page (title, url, last_updated, status) VALUES
('About Us', '/about', '2023-04-12', 'Published'),
('Contact Information', '/contact', '2023-04-10', 'Published'),
('Faculty Members', '/faculty', '2023-04-05', 'Published'),
('Research Areas', '/research', '2023-04-01', 'Published'),
('Admissions', '/admissions', '2023-03-28', 'Draft'),
('Student Resources', '/resources', '2023-03-25', 'Published'),
('Academic Programs', '/programs', '2023-03-20', 'Draft');


CREATE TABLE category_post (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL
);

INSERT INTO category_post (name) VALUES
(N'Research'),
(N'Events'),
(N'Admissions'),
(N'News');

CREATE TABLE post (
    id INT PRIMARY KEY IDENTITY(1,1),
    title NVARCHAR(255) NOT NULL,
    category_id INT NOT NULL,          -- Foreign key đến category_post
    author NVARCHAR(100),
    post_date DATE,
    status NVARCHAR(50),               -- Draft / Published / Pending Review
    
    CONSTRAINT FK_Post_Category FOREIGN KEY (category_id) REFERENCES category_post(id)
);


DROP TABLE post;


INSERT INTO post (title, category_id, author, post_date, status) VALUES
(N'New Research Grant Awarded to Faculty',      1, N'Dr. Smith',      '2023-04-15', N'Published'),
(N'Faculty Conference Announcement',            2, N'Dr. Johnson',    '2023-04-12', N'Published'),
(N'Student Admissions Process Update',          3, N'Jane Doe',       '2023-04-10', N'Pending Review'),
(N'New Course Offerings for Fall Semester',     4, N'Dr. Williams',   '2023-04-08', N'Draft'),
(N'Faculty Research Spotlight: AI in Education',1, N'Dr. Brown',      '2023-04-05', N'Published'),
(N'Campus Renovation Updates',                  4, N'Facilities Team','2023-04-03', N'Published');


CREATE TABLE notification (
    id INT PRIMARY KEY IDENTITY(1,1),
    title NVARCHAR(255) NOT NULL,
    content NVARCHAR(MAX),
    post_date DATE,
    status NVARCHAR(50)     
);





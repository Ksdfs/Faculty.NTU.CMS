CREATE DATABASE FacultyCMS;

USE FacultyCMS;

CREATE TABLE page (
    id INT PRIMARY KEY IDENTITY(1,1),
    title NVARCHAR(255),
    url NVARCHAR(255),
    last_updated NVARCHAR(50),
    status NVARCHAR(50),
	created_at DATETIME2 DEFAULT GETDATE(),
	updated_at DATETIME2 DEFAULT GETDATE()

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
    name NVARCHAR(100) NOT NULL,
	created_at DATETIME2 DEFAULT GETDATE(),
	updated_at DATETIME2 DEFAULT GETDATE()
);

INSERT INTO category_post (name) VALUES
(N'Research'),
(N'Events'),
(N'Admissions'),
(N'News');

CREATE TABLE post (
    id INT PRIMARY KEY IDENTITY(1,1),
    title NVARCHAR(255) NOT NULL,
    category_id INT NOT NULL,           -- Foreign key đến category_post
    author NVARCHAR(100),
    post_date DATE,
    status NVARCHAR(50),                -- Draft / Published / Pending Review
	created_at DATETIME2 DEFAULT GETDATE(),
	updated_at DATETIME2 DEFAULT GETDATE(),

    excerpt NVARCHAR(MAX),              -- đoạn mô tả ngắn
    content NVARCHAR(MAX),              -- nội dung bài viết
    tags NVARCHAR(255),                 -- danh sách thẻ (tags)
    image NVARCHAR(255),                -- ảnh chính
    thumbnail NVARCHAR(255),            -- ảnh phụ

    CONSTRAINT FK_Post_Category FOREIGN KEY (category_id) REFERENCES category_post(id)
);




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
    status NVARCHAR(50),
	created_at DATETIME2 DEFAULT GETDATE(),
	updated_at DATETIME2 DEFAULT GETDATE()

);

CREATE TABLE event (
    id BIGINT IDENTITY PRIMARY KEY,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    start_date DATE,
    end_date DATE,
    status NVARCHAR(50),
	updated_at DATETIME2 DEFAULT GETDATE(),
	created_at DATETIME2 DEFAULT GETDATE(),
	start_time TIME         NULL,
      end_time   TIME         NULL,
      location   NVARCHAR(255) NULL,
      organizer  NVARCHAR(255) NULL,
);
CREATE TABLE activity (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    action NVARCHAR(255)   NOT NULL,
    url    NVARCHAR(255)   NOT NULL,
    icon   NVARCHAR(100)   NULL,
    time   DATETIME2       NOT NULL
);

-- Bảng user tổng quát (dùng chung cho admin/editor)
CREATE TABLE Users (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    username NVARCHAR(255) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    email NVARCHAR(255) UNIQUE NULL,
    role NVARCHAR(50) CHECK (role IN ('admin', 'editor')) NOT NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);

-- Nếu bạn muốn tách riêng bảng admin và editor:
CREATE TABLE Admin (
    id BIGINT PRIMARY KEY,
    full_name NVARCHAR(255),
    department NVARCHAR(255),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (id) REFERENCES Users(id)
);

CREATE TABLE Editor (
    id BIGINT PRIMARY KEY,
    full_name NVARCHAR(255),
    specialization NVARCHAR(255),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (id) REFERENCES Users(id)
);





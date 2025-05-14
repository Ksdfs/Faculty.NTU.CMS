CREATE DATABASE FacultyCMS;

USE FacultyCMS;

CREATE TABLE page (
    id INT PRIMARY KEY IDENTITY(1,1),
    title NVARCHAR(255),
    url NVARCHAR(255),
    last_updated NVARCHAR(50),
    status NVARCHAR(50),
	created_at DATETIME2 DEFAULT GETDATE(),
	updated_at DATETIME2 DEFAULT GETDATE(),
	created_by_admin_id INT   NULL,
	created_by_qtv_id   INT   NULL

	CONSTRAINT FK_page_created_by_admin FOREIGN KEY(created_by_admin_id) REFERENCES Admin(id),
	CONSTRAINT FK_page_created_by_qtv   FOREIGN KEY(created_by_qtv_id)   REFERENCES QuanTriVien(id)
);


CREATE TABLE category_post (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL,
	created_at DATETIME2 DEFAULT GETDATE(),
	updated_at DATETIME2 DEFAULT GETDATE(),
	created_by_admin_id INT    NULL,
	created_by_qtv_id   INT    NULL

	CONSTRAINT FK_category_created_by_admin FOREIGN KEY(created_by_admin_id) REFERENCES Admin(id),
	CONSTRAINT FK_category_created_by_qtv   FOREIGN KEY(created_by_qtv_id)   REFERENCES QuanTriVien(id)
);


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
	created_by_admin_id INT   NULL,
	created_by_qtv_id   INT   NULL

    CONSTRAINT FK_Post_Category FOREIGN KEY (category_id) REFERENCES category_post(id),
	CONSTRAINT FK_post_created_by_admin FOREIGN KEY(created_by_admin_id) REFERENCES Admin(id),
	CONSTRAINT FK_post_created_by_qtv   FOREIGN KEY(created_by_qtv_id)   REFERENCES QuanTriVien(id)
);


CREATE TABLE notification (
    id INT PRIMARY KEY IDENTITY(1,1),
    title NVARCHAR(255) NOT NULL,
    content NVARCHAR(MAX),
    post_date DATE,
    status NVARCHAR(50),
	created_at DATETIME2 DEFAULT GETDATE(),
	updated_at DATETIME2 DEFAULT GETDATE(),
	created_by_admin_id INT   NULL,
	created_by_qtv_id   INT   NULL

	CONSTRAINT FK_notification_created_by_admin FOREIGN KEY(created_by_admin_id) REFERENCES Admin(id),
	CONSTRAINT FK_notification_created_by_qtv   FOREIGN KEY(created_by_qtv_id)   REFERENCES QuanTriVien(id)

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
	created_by_admin_id INT   NULL,
	created_by_qtv_id   INT   NULL

	CONSTRAINT FK_event_created_by_admin FOREIGN KEY(created_by_admin_id) REFERENCES Admin(id),
	CONSTRAINT FK_event_created_by_qtv   FOREIGN KEY(created_by_qtv_id)   REFERENCES QuanTriVien(id)
);

CREATE TABLE activity (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    action NVARCHAR(255)   NOT NULL,
    url    NVARCHAR(255)   NOT NULL,
    icon   NVARCHAR(100)   NULL,
    time   DATETIME2       NOT NULL
);

CREATE TABLE Users (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    username NVARCHAR(255) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    email NVARCHAR(255) UNIQUE NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);



CREATE TABLE Admin (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(255) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL
);

--    Tài khoản admin
INSERT INTO Admin (username, password)
VALUES ('admin123', 'admin123');

CREATE TABLE quan_tri_vien (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(255) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL
);

INSERT INTO quan_tri_vien (username, password)
VALUES ('quantrivien123', 'quantrivien123');








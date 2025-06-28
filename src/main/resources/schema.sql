-- 文件表
CREATE TABLE IF NOT EXISTS files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(255),
    data BLOB,
    create_time BIGINT,
    remark VARCHAR(255),
    password VARCHAR(255)
);

-- 文本表
CREATE TABLE IF NOT EXISTS texts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content CLOB,
    create_time BIGINT,
    remark VARCHAR(255),
    password VARCHAR(255)
);
-- 新增邮箱字段
ALTER TABLE sys_user
    ADD COLUMN email VARCHAR(128);

-- 添加唯一约束
ALTER TABLE sys_user
    ADD CONSTRAINT uk_sys_user_email UNIQUE (email);

-- 字段注释
COMMENT ON COLUMN sys_user.email IS '邮箱';
ALTER TABLE posts ADD COLUMN IF NOT EXISTS deleted_at timestamptz;
ALTER TABLE notices ADD COLUMN IF NOT EXISTS notice_type varchar(24) NOT NULL DEFAULT 'GENERAL';
ALTER TABLE notices ADD COLUMN IF NOT EXISTS academic_year int;
ALTER TABLE notices ADD COLUMN IF NOT EXISTS section_code varchar(1);
ALTER TABLE notices ADD COLUMN IF NOT EXISTS deleted_at timestamptz;
ALTER TABLE question_papers ADD COLUMN IF NOT EXISTS description varchar(2000);


ALTER TABLE question_papers
    ADD COLUMN status varchar(16) NOT NULL DEFAULT 'APPROVED',
    ADD COLUMN moderated_by uuid REFERENCES users(id),
    ADD COLUMN moderated_at timestamptz,
    ADD COLUMN rejection_reason varchar(500),
    ADD CONSTRAINT chk_question_paper_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'));

CREATE INDEX idx_question_papers_status_created
    ON question_papers(status, created_at DESC);

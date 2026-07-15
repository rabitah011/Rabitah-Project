ALTER TABLE comments
    ADD COLUMN parent_comment_id uuid REFERENCES comments(id) ON DELETE CASCADE;

CREATE INDEX idx_comments_parent ON comments(parent_comment_id, created_at);

CREATE TABLE comment_reactions (
    comment_id uuid NOT NULL REFERENCES comments(id) ON DELETE CASCADE,
    user_id uuid NOT NULL REFERENCES users(id),
    reaction varchar(8) NOT NULL CHECK (reaction IN ('LIKE', 'DISLIKE')),
    created_at timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (comment_id, user_id)
);

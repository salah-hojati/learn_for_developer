-- password و hash لازم نیست چون فقط read داریم
INSERT INTO users (username, email, full_name, status, created_at, version)
VALUES ('ali', 'ali@test.com', 'Ali Ahmadi', 'ACTIVE', CURRENT_TIMESTAMP, 0);

INSERT INTO users (username, email, full_name, status, created_at, version)
VALUES ('sara', 'sara@test.com', 'Sara Rezaei', 'ACTIVE', CURRENT_TIMESTAMP, 0);

INSERT INTO posts (title, slug, content, status, view_count, author_id, created_at, version)
VALUES ('Intro to JPA', 'intro-to-jpa', 'JPA is...', 'PUBLISHED', 100, 1, CURRENT_TIMESTAMP, 0);

INSERT INTO posts (title, slug, content, status, view_count, author_id, created_at, version)
VALUES ('Criteria API', 'criteria-api', 'Criteria is...', 'PUBLISHED', 50, 2, CURRENT_TIMESTAMP, 0);

INSERT INTO tags (name, created_at, version) VALUES ('java', CURRENT_TIMESTAMP, 0);
INSERT INTO tags (name, created_at, version) VALUES ('jpa', CURRENT_TIMESTAMP, 0);

INSERT INTO post_tags (post_id, tag_id) VALUES (1, 1), (1, 2), (2, 1);
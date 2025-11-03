-- Sample Data for Sentence Builder
-- CS4485 Capstone Project

USE sentence_builder;

-- Insert sample words
INSERT INTO words (word_text, total_count, sentence_start_count, sentence_end_count) VALUES
('the', 150, 20, 5),
('quick', 45, 5, 0),
('brown', 40, 0, 0),
('fox', 35, 0, 10),
('jumps', 30, 0, 0),
('over', 25, 0, 0),
('lazy', 20, 0, 0),
('dog', 18, 0, 8),
('cat', 15, 2, 5),
('runs', 12, 0, 0),
('fast', 10, 0, 2);

-- Insert sample word pairs (Markov chain transitions)
INSERT INTO word_pairs (first_word_id, second_word_id, transition_count, transition_probability) VALUES
-- "the" transitions
(1, 2, 20, 0.13333),  -- the -> quick
(1, 3, 15, 0.10000),  -- the -> brown
(1, 4, 10, 0.06667),  -- the -> fox
(1, 7, 8, 0.05333),   -- the -> lazy
(1, 8, 12, 0.08000),  -- the -> dog
(1, 9, 7, 0.04667),   -- the -> cat

-- "quick" transitions
(2, 3, 25, 0.55556),  -- quick -> brown
(2, 4, 10, 0.22222),  -- quick -> fox
(2, 9, 5, 0.11111),   -- quick -> cat

-- "brown" transitions
(3, 4, 30, 0.75000),  -- brown -> fox
(3, 8, 5, 0.12500),   -- brown -> dog
(3, 9, 5, 0.12500),   -- brown -> cat

-- "fox" transitions
(4, 5, 25, 0.71429),  -- fox -> jumps
(4, 10, 10, 0.28571), -- fox -> runs

-- "jumps" transitions
(5, 6, 30, 1.00000),  -- jumps -> over

-- "over" transitions
(6, 1, 25, 1.00000),  -- over -> the

-- "lazy" transitions
(7, 8, 15, 0.75000),  -- lazy -> dog
(7, 9, 5, 0.25000),   -- lazy -> cat

-- "dog" transitions
(8, 10, 10, 0.55556), -- dog -> runs
(8, 5, 8, 0.44444);   -- dog -> jumps

-- Insert sample imported files
INSERT INTO imported_files (filename, file_path, word_count, status) VALUES
('sample_text.txt', '/path/to/sample_text.txt', 250, 'completed'),
('test_document.txt', '/path/to/test_document.txt', 180, 'completed'),
('training_data.txt', '/path/to/training_data.txt', 500, 'completed');
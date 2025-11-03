-- Sentence Builder Database Schema
-- CS4485 Capstone Project

-- Create database
CREATE DATABASE IF NOT EXISTS sentence_builder;
USE sentence_builder;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS ngrams;
DROP TABLE IF EXISTS word_pairs;
DROP TABLE IF EXISTS imported_files;
DROP TABLE IF EXISTS words;

-- Words table: stores unique words and their statistics
CREATE TABLE words (
    word_id INT AUTO_INCREMENT PRIMARY KEY,
    word_text VARCHAR(255) NOT NULL UNIQUE,
    total_count INT DEFAULT 0,
    sentence_start_count INT DEFAULT 0,
    sentence_end_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_word_text (word_text),
    INDEX idx_total_count (total_count),
    INDEX idx_sentence_start (sentence_start_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Word pairs table: stores word transitions for Markov chain
CREATE TABLE word_pairs (
    pair_id INT AUTO_INCREMENT PRIMARY KEY,
    first_word_id INT NOT NULL,
    second_word_id INT NOT NULL,
    transition_count INT DEFAULT 1,
    transition_probability DECIMAL(10, 8) DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (first_word_id) REFERENCES words(word_id) ON DELETE CASCADE,
    FOREIGN KEY (second_word_id) REFERENCES words(word_id) ON DELETE CASCADE,
    UNIQUE KEY unique_pair (first_word_id, second_word_id),
    INDEX idx_first_word (first_word_id),
    INDEX idx_second_word (second_word_id),
    INDEX idx_transition_count (transition_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- N-grams table: stores N-gram sequences for variable-order N-gram generation
CREATE TABLE ngrams (
    ngram_id INT AUTO_INCREMENT PRIMARY KEY,
    n INT NOT NULL,
    ngram_text VARCHAR(500) NOT NULL,
    next_word_id INT NOT NULL,
    transition_count INT DEFAULT 1,
    transition_probability DECIMAL(10, 8) DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (next_word_id) REFERENCES words(word_id) ON DELETE CASCADE,
    UNIQUE KEY unique_ngram (n, ngram_text(191), next_word_id),
    INDEX idx_n (n),
    INDEX idx_ngram_text (ngram_text(191)),
    INDEX idx_next_word (next_word_id),
    INDEX idx_transition_count (transition_count),
    INDEX idx_n_ngram (n, ngram_text(191))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Imported files table: tracks files imported into the system
CREATE TABLE imported_files (
    file_id INT AUTO_INCREMENT PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    file_path VARCHAR(1024) NOT NULL,
    word_count INT DEFAULT 0,
    import_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pending', 'processing', 'completed', 'failed') DEFAULT 'pending',
    error_message TEXT,
    INDEX idx_filename (filename),
    INDEX idx_import_date (import_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
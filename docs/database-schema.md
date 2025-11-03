# Database Schema Documentation

## Overview

The Sentence Builder database consists of three main tables that store words, word transitions, and file import history.

## Entity Relationship Diagram

```
┌─────────────────┐
│     words       │
├─────────────────┤
│ word_id (PK)    │◄────┐
│ word_text       │     │
│ total_count     │     │
│ sentence_start  │     │
│ sentence_end    │     │
└─────────────────┘     │
                        │
                        │ FK
                        │
                  ┌─────┴─────────────┐
                  │   word_pairs      │
                  ├───────────────────┤
                  │ pair_id (PK)      │
                  │ first_word_id     │◄───┐
                  │ second_word_id    │    │ FK
                  │ transition_count  │    │
                  │ probability       │    │
                  └───────────────────┘    │
                                          │
                                          │
                                    ┌─────┘
                                    │
                                    │
┌─────────────────────┐
│  imported_files     │
├─────────────────────┤
│ file_id (PK)        │
│ filename            │
│ file_path           │
│ word_count          │
│ import_date         │
│ status              │
└─────────────────────┘
```

## Table Definitions

### 1. words

Stores unique words and their occurrence statistics.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| word_id | INT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| word_text | VARCHAR(255) | NOT NULL, UNIQUE | The word itself |
| total_count | INT | DEFAULT 0 | Total occurrences in all texts |
| sentence_start_count | INT | DEFAULT 0 | Times word starts a sentence |
| sentence_end_count | INT | DEFAULT 0 | Times word ends a sentence |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation time |
| updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | Last update time |

**Indexes:**
- PRIMARY KEY on word_id
- UNIQUE INDEX on word_text
- INDEX on total_count (for sorting by frequency)
- INDEX on sentence_start_count (for finding sentence starters)

**Usage:**
- Track word frequency across all imported texts
- Identify good starting words for sentence generation
- Detect sentence boundaries

### 2. word_pairs

Stores word transition information for Markov chain.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| pair_id | INT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| first_word_id | INT | NOT NULL, FOREIGN KEY | First word in the pair |
| second_word_id | INT | NOT NULL, FOREIGN KEY | Second word in the pair |
| transition_count | INT | DEFAULT 1 | Number of times this transition occurs |
| transition_probability | DECIMAL(10,8) | DEFAULT 0.0 | Calculated probability |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation time |
| updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | Last update time |

**Indexes:**
- PRIMARY KEY on pair_id
- UNIQUE INDEX on (first_word_id, second_word_id)
- INDEX on first_word_id (for lookups)
- INDEX on second_word_id (for reverse lookups)
- INDEX on transition_count (for sorting)

**Foreign Keys:**
- first_word_id → words(word_id) ON DELETE CASCADE
- second_word_id → words(word_id) ON DELETE CASCADE

**Usage:**
- Store Markov chain transitions
- Calculate transition probabilities
- Generate next word predictions

### 3. imported_files

Tracks files imported into the system.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| file_id | INT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| filename | VARCHAR(255) | NOT NULL | Name of the file |
| file_path | VARCHAR(1024) | NOT NULL | Full path to the file |
| word_count | INT | DEFAULT 0 | Number of words processed |
| import_date | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | When file was imported |
| status | ENUM | NOT NULL | Import status |
| error_message | TEXT | NULL | Error details if failed |

**Status Values:**
- `pending`: Queued for processing
- `processing`: Currently being processed
- `completed`: Successfully imported
- `failed`: Import failed

**Indexes:**
- PRIMARY KEY on file_id
- INDEX on filename
- INDEX on import_date
- INDEX on status

**Usage:**
- Track import history
- Prevent duplicate imports
- Debug failed imports

## Query Patterns

### Get all possible next words for a given word
```sql
SELECT w2.word_text, wp.transition_probability
FROM word_pairs wp
JOIN words w1 ON wp.first_word_id = w1.word_id
JOIN words w2 ON wp.second_word_id = w2.word_id
WHERE w1.word_text = ?
ORDER BY wp.transition_probability DESC;
```

### Get most common sentence starters
```sql
SELECT word_text, sentence_start_count
FROM words
WHERE sentence_start_count > 0
ORDER BY sentence_start_count DESC
LIMIT 10;
```

### Calculate transition probabilities
```sql
UPDATE word_pairs wp
JOIN (
    SELECT first_word_id, SUM(transition_count) as total
    FROM word_pairs
    GROUP BY first_word_id
) totals ON wp.first_word_id = totals.first_word_id
SET wp.transition_probability = wp.transition_count / totals.total;
```

## Maintenance

### Recalculate probabilities after updates
Should be run after bulk inserts:
```sql
CALL recalculate_probabilities();
```

### Clean up orphaned records
```sql
DELETE FROM words WHERE total_count = 0 AND word_id NOT IN (
    SELECT DISTINCT first_word_id FROM word_pairs
    UNION
    SELECT DISTINCT second_word_id FROM word_pairs
);
```
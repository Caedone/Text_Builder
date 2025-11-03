# Database Setup

This directory contains the database schema and sample data for the Sentence Builder application.

## Files

- **schema.sql**: Creates the database structure with three main tables:
  - `words`: Stores unique words and their frequency statistics
  - `word_pairs`: Stores word transitions for Markov chain generation
  - `imported_files`: Tracks files imported into the system

- **sample-data.sql**: Provides sample data for testing and development

## Setup Instructions

1. **Install MySQL** (if not already installed):
   ```bash
   # macOS with Homebrew
   brew install mysql

   # Ubuntu/Debian
   sudo apt-get install mysql-server
   ```

2. **Start MySQL service**:
   ```bash
   # macOS
   brew services start mysql

   # Ubuntu/Debian
   sudo systemctl start mysql
   ```

3. **Run the schema script**:
   ```bash
   mysql -u root -p < schema.sql
   ```

4. **Load sample data** (optional):
   ```bash
   mysql -u root -p < sample-data.sql
   ```

5. **Update database.properties**:
   - Navigate to `src/main/resources/database.properties`
   - Update `db.username` and `db.password` with your MySQL credentials
   - Never commit this file with real credentials

## Database Schema

### words table
Stores individual words and their statistics:
- `word_id`: Unique identifier
- `word_text`: The actual word (unique)
- `total_count`: Total occurrences across all texts
- `sentence_start_count`: Number of times word appears at sentence start
- `sentence_end_count`: Number of times word appears at sentence end

### word_pairs table
Stores word transitions for Markov chain:
- `pair_id`: Unique identifier
- `first_word_id`: Reference to first word
- `second_word_id`: Reference to second word
- `transition_count`: Number of times this transition occurs
- `transition_probability`: Calculated probability of transition

### imported_files table
Tracks imported text files:
- `file_id`: Unique identifier
- `filename`: Name of the file
- `file_path`: Full path to the file
- `word_count`: Number of words in the file
- `import_date`: When the file was imported
- `status`: Import status (pending, processing, completed, failed)
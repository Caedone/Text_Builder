# Sentence Builder - Design Document

## CS4485 Senior Capstone Project

## 1. Project Overview

### 1.1 Purpose
Sentence Builder is a text generation application that uses Markov chain algorithms to analyze input text and generate contextually relevant sentences.

### 1.2 Scope
The application will:
- Import and process text files
- Build Markov chain models (first-order and second-order)
- Generate text based on learned patterns
- Provide autocomplete functionality
- Persist data in MySQL database
- Provide a user-friendly JavaFX interface

## 2. System Architecture

### 2.1 High-Level Architecture
The application follows a layered architecture:
- **Presentation Layer**: JavaFX UI components and controllers
- **Service Layer**: Business logic and algorithm implementations
- **Data Access Layer**: Database operations and connection management
- **Data Layer**: MySQL database

### 2.2 Component Diagram
```
┌─────────────────────────────────────┐
│     JavaFX UI (Controllers)         │
├─────────────────────────────────────┤
│      Service Layer                  │
│  - MarkovChainService               │
│  - TextProcessingService            │
│  - DatabaseService                  │
├─────────────────────────────────────┤
│      Algorithm Layer                │
│  - MarkovChainGenerator             │
│  - TextTokenizer                    │
├─────────────────────────────────────┤
│      DAO Layer                      │
│  - WordDAO                          │
│  - WordPairDAO                      │
├─────────────────────────────────────┤
│      MySQL Database                 │
└─────────────────────────────────────┘
```

## 3. Detailed Design

### 3.1 Markov Chain Algorithm

#### First-Order Markov Chain
- Predicts next word based on current word only
- State: single word
- Transition: probability of word B following word A

#### Second-Order Markov Chain
- Predicts next word based on previous two words
- State: pair of words
- Transition: probability of word C following words (A, B)

### 3.2 Key Classes

#### MarkovChainGenerator
- Maintains word transition maps
- Trains on input text
- Generates text based on learned patterns
- Supports both first-order and second-order chains

#### TextProcessingService
- Tokenizes text into words
- Handles file import and parsing
- Updates database with word statistics

#### DatabaseService
- Manages database connections (HikariCP)
- Coordinates DAO operations
- Handles transactions

### 3.3 Database Design

See `database-schema.md` for detailed schema information.

## 4. User Interface Design

### 4.1 Main Window
- Menu bar with File, Edit, View, Help
- Tab-based interface for different functions

### 4.2 Text Generator Tab
- Algorithm selection (first-order/second-order)
- Starting word input with autocomplete
- Word count slider
- Generate button
- Output text area

### 4.3 File Import Tab
- File selection dialog
- Import progress bar
- File list with status

### 4.4 Database View Tab
- Word statistics table
- Word pair visualization
- Search and filter capabilities

## 5. Algorithm Details

### 5.1 Training Process
1. Read and tokenize input text
2. Identify sentence boundaries
3. Count word occurrences
4. Build transition probability tables
5. Store in database

### 5.2 Generation Process
1. Select starting word (random or user-specified)
2. Look up possible next words with probabilities
3. Use weighted random selection
4. Repeat until max words or sentence end marker
5. Return generated text

### 5.3 Autocomplete
1. Take current word/context
2. Query word pairs for highest probability matches
3. Return top N suggestions

## 6. Error Handling

- Database connection failures: retry with exponential backoff
- File import errors: log error, update file status
- Invalid input: validate and show user-friendly messages
- Algorithm errors: graceful degradation to simpler model

## 7. Performance Considerations

- Use connection pooling (HikariCP) for database access
- Batch database inserts during file import
- Cache frequently accessed word pairs in memory
- Use prepared statements to prevent SQL injection

## 8. Future Enhancements

- Support for additional algorithms (LSTM, GPT-style)
- Export generated text to files
- Statistical analysis of generated vs. training text
- Web interface
- Multi-language support
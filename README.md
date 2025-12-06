# Sentence Builder - Markov Chain Text Generator

**CS4485 Senior Capstone Project**
**University of Texas at Dallas**
**Fall 2025**

---

## Team Members

- **Manraj Singh** (mxs220007)
- **Johnathan Pedraza** (jxp220060)
- **Caedon Ewing** (CSE220000)
- **Bhaskar Atmakuri** (BXA210025)
- **Rahman-Danish, Rizvy**

---

## Project Overview

Sentence Builder is a JavaFX-based desktop application that leverages Markov chain algorithms and N-gram models to generate coherent text based on imported training corpora. The system analyzes statistical patterns in text files to learn word relationships and transition probabilities, then uses this learned model to generate novel sentences that maintain the stylistic characteristics of the source material.

This project demonstrates the practical application of probabilistic text generation, database design, user interface development, and software engineering principles in a complete end-to-end application.

---

## Features

### Core Functionality
- **Multiple Text Generation Algorithms**
  - First-order Markov chains (single word context)
  - Second-order Markov chains (two word context)
  - Configurable N-gram generation (N=2 through N=5)

- **Intelligent Text Processing**
  - Automatic tokenization and text normalization
  - Sentence boundary detection
  - Word frequency and position tracking
  - Support for multiple file formats (TXT, PDF, DOC, DOCX)

- **Database Integration**
  - Persistent storage of word statistics and relationships
  - Efficient HikariCP connection pooling
  - Import history tracking with status monitoring
  - Optimized queries for real-time text generation

- **User Interface**
  - Intuitive JavaFX-based graphical interface
  - Real-time autocomplete suggestions
  - Database browser for exploring learned patterns
  - Progress indicators for long-running operations
  - File import validation and error handling

---

## Project Structure

```
Text_Builder/
├── database/
│   ├── schema.sql              # Database table definitions
│   ├── sample-data.sql         # Sample training data
│   └── README.md               # Database setup guide
├── docs/
│   ├── design-document.md      # System architecture
│   ├── user-manual.md          # End-user documentation
│   └── database-schema.md      # Schema documentation
├── src/
│   ├── main/
│   │   ├── java/edu/utdallas/cs4485/sentencebuilder/
│   │   │   ├── algorithm/      # Markov chain & N-gram implementations
│   │   │   ├── controller/     # JavaFX UI controllers
│   │   │   ├── dao/            # Database access objects
│   │   │   ├── model/          # Data transfer objects
│   │   │   ├── service/        # Business logic layer
│   │   │   └── util/           # Helper utilities
│   │   └── resources/
│   │       ├── css/            # Stylesheets
│   │       ├── fxml/           # UI layouts
│   │       └── *.properties    # Configuration files
│   └── test/
│       └── java/               # JUnit test suites
├── test-files/                 # Sample text corpora
├── pom.xml                     # Maven build configuration
└── README.md                   # This file
```

---

## Installation Instructions

### 1. Extract the Project
After extracting the ZIP file, navigate to the project directory:
```bash
cd Text_Builder
```

### 2. Database Setup

Start your MySQL server and execute the following commands:

```bash
# Create the database and tables
mysql -u root -p < database/schema.sql

# (Optional) Load sample training data
mysql -u root -p < database/sample-data.sql
```

### 3. Configure Database Connection

Edit `src/main/resources/database.properties` with your MySQL credentials:

```properties
db.url=jdbc:mysql://localhost:3306/sentence_builder?useSSL=false&serverTimezone=UTC
db.username=YOUR_USERNAME
db.password=YOUR_PASSWORD
```

**Note**: Never commit this file with real credentials to version control.

### 4. Build the Application

Using Maven, compile and package the application:

```bash
# Clean and compile
mvn clean compile

# Run unit tests (optional but recommended)
mvn test

# Package the application
mvn package
```

### 5. Run the Application

Launch the application using Maven:

```bash
mvn javafx:run
```

Alternatively, run the packaged JAR file:

```bash
java -jar target/sentence-builder-1.0-SNAPSHOT.jar
```


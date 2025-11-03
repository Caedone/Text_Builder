
## Technologies Used

- **Java 11**: Core programming language
- **JavaFX 17**: User interface framework
- **MySQL 8.0**: Database for storing words and word pairs
- **Maven**: Build automation and dependency management
- **HikariCP**: Database connection pooling
- **JUnit 5**: Unit testing framework

## Features

- **Text Import**: Import text files to train the Markov chain model
- **Text Generation**: Generate sentences using first-order or second-order Markov chains
- **Auto-Complete**: Get word suggestions based on current context
- **Database Persistence**: Store and retrieve word statistics and transitions
- **File Management**: Track imported files and their processing status
- **Visual Interface**: User-friendly JavaFX interface for all operations

## Project Structure

```
sentence-builder/
├── database/              # Database schema and sample data
├── docs/                  # Documentation
├── src/
│   ├── main/
│   │   ├── java/         # Application source code
│   │   │   └── edu/utdallas/cs4485/sentencebuilder/
│   │   │       ├── controller/      # JavaFX controllers
│   │   │       ├── model/           # Data models
│   │   │       ├── service/         # Business logic
│   │   │       ├── dao/             # Database access
│   │   │       ├── algorithm/       # Markov chain implementation
│   │   │       └── util/            # Utility classes
│   │   └── resources/    # FXML, CSS, properties files
│   └── test/             # Unit tests
├── pom.xml               # Maven configuration
└── README.md
```

## Prerequisites

- **Java Development Kit (JDK) 11 or higher**
- **Maven 3.6 or higher**
- **MySQL 8.0 or higher**
- **JavaFX 17** (included as Maven dependency)

## Installation

1. **Clone the repository** (when ready):
   ```bash
   git clone [repository-url]
   cd sentence-builder
   ```

2. **Set up the database**:
   ```bash
   mysql -u root -p < database/schema.sql
   mysql -u root -p < database/sample-data.sql
   ```

3. **Configure database connection**:
   - Edit `src/main/resources/database.properties`
   - Update `db.username` and `db.password` with your MySQL credentials

4. **Build the project**:
   ```bash
   mvn clean install
   ```

5. **Run the application**:
   ```bash
   mvn javafx:run
   ```

## Usage

1. **Import Text Files**: Use the File Import interface to load text files for training
2. **Generate Text**: Select the algorithm type and starting word to generate sentences
3. **View Database**: Browse stored words and word pairs
4. **Auto-Complete**: Type a word to see suggestions based on the trained model

## Development

### Building
```bash
mvn clean compile
```

### Running Tests
```bash
mvn test
```

### Packaging
```bash
mvn package
```

### Running from JAR
```bash
java -jar target/sentence-builder-1.0-SNAPSHOT.jar
```

## Documentation

See the `docs/` directory for additional documentation:
- Design Document
- Database Schema Details
- User Manual

## License

This project is created for educational purposes as part of CS4485 at UT Dallas.

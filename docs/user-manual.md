# Sentence Builder - User Manual

## Table of Contents
1. [Getting Started](#getting-started)
2. [Importing Text Files](#importing-text-files)
3. [Generating Text](#generating-text)
4. [Using Autocomplete](#using-autocomplete)
5. [Viewing Database](#viewing-database)
6. [Troubleshooting](#troubleshooting)

## Getting Started

### Launching the Application

1. Open a terminal in the project directory
2. Run: `mvn javafx:run`
3. The main window will appear with four tabs

### Main Interface

The application has four main sections:
- **Text Generator**: Generate sentences using Markov chains
- **File Import**: Import text files for training
- **Database View**: Browse stored words and transitions
- **Settings**: Configure application preferences

## Importing Text Files

### Supported File Formats
- Plain text files (.txt)
- Documents (.doc, .docx)
- PDF files (.pdf)

### Import Process

1. **Select the File Import tab**
2. **Click "Browse" to select a file**
   - Maximum file size: 50 MB
   - UTF-8 encoding recommended
3. **Click "Import"**
   - Progress bar shows import status
   - File appears in import history with status
4. **Wait for completion**
   - Status changes to "Completed" when done
   - Word count is displayed

### Best Practices
- Import multiple diverse texts for better generation
- Larger training sets produce more natural results
- Avoid importing duplicate files

## Generating Text

### Basic Generation

1. **Select the Text Generator tab**
2. **Choose algorithm type**:
   - **First-Order**: Faster, simpler patterns
   - **Second-Order**: Slower, more natural results
3. **Set word count**: Use slider (10-200 words)
4. **Click "Generate"**
   - Application randomly selects starting word
   - Generated text appears in output area

### Advanced Generation

1. **Specify starting word**:
   - Type a word in the "Start Word" field
   - Use autocomplete suggestions
   - Leave blank for random start
2. **Adjust parameters**:
   - Word count: Length of generated text
   - Temperature: Randomness (higher = more random)
3. **Click "Generate"**

### Understanding Results

- **Coherence**: How well the text makes sense
  - First-order: Less coherent but faster
  - Second-order: More coherent but slower
- **Generation time**: Displayed after generation
- **Starting word**: Shows which word was used

## Using Autocomplete

### In Text Generator

1. **Start typing in the "Start Word" field**
2. **Suggestions appear automatically**
   - Based on words in database
   - Sorted by frequency
3. **Click a suggestion to select it**

### How It Works

- Autocomplete uses Markov chain data
- Suggests words with high transition probabilities
- Learns from imported text

## Viewing Database

### Words Table

1. **Select the Database View tab**
2. **Click "Words" sub-tab**
3. **Browse word statistics**:
   - Word text
   - Total occurrences
   - Sentence start count
   - Sentence end count

### Word Pairs Table

1. **Click "Word Pairs" sub-tab**
2. **View transition data**:
   - First word → Second word
   - Transition count
   - Probability

### Search and Filter

- **Search**: Type in search box to filter results
- **Sort**: Click column headers to sort
- **Filter**: Use dropdown to filter by criteria

## Troubleshooting

### Common Issues

#### Application Won't Start
- **Check Java version**: Requires JDK 11+
- **Verify Maven**: Run `mvn --version`
- **Check database**: Ensure MySQL is running

#### Database Connection Failed
1. Verify MySQL is running:
   ```bash
   # macOS
   brew services list

   # Linux
   sudo systemctl status mysql
   ```
2. Check credentials in `database.properties`
3. Test connection: `mysql -u username -p`

#### Import Failed
- **File too large**: Max size is 50 MB
- **Unsupported format**: Use .txt, .doc, .docx, or .pdf
- **File encoding**: Convert to UTF-8
- **Check logs**: View error message in import history

#### Generation Produces Poor Results
- **Import more text**: Need more training data
- **Try second-order**: More coherent than first-order
- **Check starting word**: Some words have limited transitions
- **Verify database**: Ensure word pairs exist

#### Slow Performance
- **Reduce word count**: Generate fewer words
- **Use first-order**: Faster than second-order
- **Check database**: Ensure indexes are present
- **Restart application**: Clear memory cache

### Getting Help

If issues persist:
1. Check application logs in `logs/application.log`
2. Verify database schema matches `database/schema.sql`
3. Try with sample data: `mysql -u root -p < database/sample-data.sql`
4. Contact development team

## Tips and Best Practices

### For Best Results
- Import diverse, high-quality text
- Use second-order Markov chains
- Start with common words
- Generate 50-100 words for best coherence

### Performance Optimization
- Import files in batches
- Periodically recalculate probabilities
- Clean up unused words
- Monitor database size

### Data Management
- Export generated text regularly
- Back up database before major imports
- Track which files have been imported
- Remove duplicate or poor-quality training data
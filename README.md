# CrosswayBoardGame

A Java implementation of the **Crossway board game**, developed as a student project for the *Software Development Methods* course.
The rules of the game are based on the original rules by [Mark Steere](http://marksteeregames.com) and are
available in the [Rules.md](Rules.md) file.

## 📁 Project Structure

```
app/
└── src/main/java/org/boardgames/crossway/
    ├── App.java
    ├── controller/
    ├── model/
    │   ├── rules/
    │   └── ...
    ├── ui/
    └── utils/
```

## Run the Application

You can run the application in two ways: using the pre-built binary or building it from source.

### Using the binary

1. Download the latest release from the [Releases page](https://github.com/GabrielePintus/CrosswayBoardGame/releases/)
2. Extract the downloaded archive.
3. Navigate to the extracted directory.
4. Navigate to the `bin` directory.
5. Run the application by double-clicking it or using the command line:
   - On Windows:
     ```bash
     .\app.bat
     ```
   - On Linux/Mac:
     ```bash
     ./app
     ```

### Build from source

1. Ensure you have a Java Development Kit (JDK) installed.
2. Clone the repository and navigate to the project directory:
   ```bash
   git clone https://github.com/GabrielePintus/CrosswayBoardGame.git
   cd CrosswayBoardGame
   ```
3. Build and run the application using the Gradle wrapper (Kotlin DSL):
   ```bash
   ./gradlew run
   ```
   On Windows use `gradlew.bat run`.

## Contributing
Contributions are welcome! If you find a bug or have a feature request, please open an issue or submit a pull request.  
For larger changes, please discuss them first by opening an issue to avoid conflicts.

## Planned features

- [ ] Add player management (e.g., player names, scores).
- [ ] Implement explicitly the pie rule
- [ ] Add multi-language support.
- [ ] Improve the user interface with more visual feedback.
- [ ] Add sound effects and music.
- [ ] Add network multiplayer support.
- [ ] Implement an AI opponent for single-player mode.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
package org.boardgames.crossway.utils;

import org.boardgames.crossway.model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

/**
 * JUnit tests for {@link GameSerializer}.
 *
 * Tests cover serialization/deserialization operations, file I/O error handling,
 * and edge cases like non-existent files and invalid JSON data.
 */
@ExtendWith(MockitoExtension.class)
class GameSerializerTest {

    @Mock private Game mockGame;

    @TempDir Path tempDir;

    private File testFile;
    private final String validJsonData = "{\"gameState\":\"test\",\"players\":[]}";
    private final String invalidJsonData = "{invalid json content}";

    @BeforeEach
    void setUp() throws IOException {
        testFile = tempDir.resolve("test-game.json").toFile();
    }

    /**
     * Tests that a valid game object can be serialized to a file and the file content
     * matches the expected JSON string.
     */
    @Test
    @DisplayName("save() serializes valid game to file")
    void testSave_ValidGame_WritesJsonToFile() throws IOException {
        // Arrange
        when(mockGame.toJson()).thenReturn(validJsonData);

        // Act
        GameSerializer.save(mockGame, testFile);

        // Assert
        assertTrue(testFile.exists(), "File should be created");
        String fileContent = Files.readString(testFile.toPath());
        assertEquals(validJsonData, fileContent, "File content should match game JSON");
        verify(mockGame).toJson();
    }

    /**
     * Tests that an exception thrown during JSON serialization is propagated,
     * and no file is created.
     */
    @Test
    @DisplayName("save() propagates exception when JSON serialization fails")
    void testSave_GameToJsonThrowsException_PropagatesException() {
        // Arrange
        RuntimeException jsonException = new RuntimeException("JSON serialization failed");
        when(mockGame.toJson()).thenThrow(jsonException);

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            GameSerializer.save(mockGame, testFile);
        });

        assertEquals("JSON serialization failed", thrown.getMessage());
        assertFalse(testFile.exists(), "File should not be created when serialization fails");
        verify(mockGame).toJson();
    }

    /**
     * Tests that an IOException thrown during file writing is propagated.
     */
    @Test
    @DisplayName("save() propagates IOException on file write error")
    void testSave_FileWriteError_ThrowsIOException() throws IOException {
        // Arrange
        when(mockGame.toJson()).thenReturn(validJsonData);

        // Use MockedStatic to simulate file write failure instead of relying on file permissions
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.writeString(any(Path.class), anyString()))
                    .thenThrow(new IOException("Write permission denied"));

            // Act & Assert
            IOException thrown = assertThrows(IOException.class, () -> {
                GameSerializer.save(mockGame, testFile);
            });

            assertEquals("Write permission denied", thrown.getMessage());
            verify(mockGame).toJson();
        }
    }

    /**
     * Tests that a valid JSON file can be loaded and deserialized back into a Game object.
     */
    @Test
    @DisplayName("load() returns Game from valid JSON file")
    void testLoad_ValidJsonFile_ReturnsGame() throws IOException {
        // Arrange
        Files.writeString(testFile.toPath(), validJsonData);
        Game expectedGame = mock(Game.class);

        try (MockedStatic<Game> gameMock = mockStatic(Game.class)) {
            gameMock.when(() -> Game.fromJson(validJsonData)).thenReturn(expectedGame);

            // Act
            Game result = GameSerializer.load(testFile);

            // Assert
            assertSame(expectedGame, result, "Should return the game created by Game.fromJson");
            gameMock.verify(() -> Game.fromJson(validJsonData));
        }
    }

    /**
     * Tests that an attempt to load a non-existent file throws an IOException.
     */
    @Test
    @DisplayName("load() throws IOException for non-existent file")
    void testLoad_NonExistentFile_ThrowsIOException() {
        // Arrange
        File nonExistentFile = tempDir.resolve("non-existent.json").toFile();

        // Act & Assert
        IOException thrown = assertThrows(IOException.class, () -> {
            GameSerializer.load(nonExistentFile);
        });

        assertTrue(thrown.getMessage().contains("Cannot read from file"));
        assertTrue(thrown.getMessage().contains(nonExistentFile.getAbsolutePath()));
    }

    /**
     * Tests that an IOException thrown during file reading is propagated.
     */
    @Test
    @DisplayName("load() throws IOException on file read error")
    void testLoad_UnreadableFile_ThrowsIOException() throws IOException {
        // Arrange
        Files.writeString(testFile.toPath(), validJsonData);

        // Use MockedStatic to simulate file read failure instead of relying on file permissions
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.readString(testFile.toPath()))
                    .thenThrow(new IOException("Read permission denied"));

            // Act & Assert
            IOException thrown = assertThrows(IOException.class, () -> {
                GameSerializer.load(testFile);
            });

            assertEquals("Read permission denied", thrown.getMessage());
        }
    }

    /**
     * Tests that loading a file with invalid JSON content results in an IOException,
     * with the original parsing exception as the cause.
     */
    @Test
    @DisplayName("load() throws IOException for invalid JSON content")
    void testLoad_InvalidJsonContent_ThrowsIOExceptionWithCause() throws IOException {
        // Arrange
        Files.writeString(testFile.toPath(), invalidJsonData);
        RuntimeException parseException = new RuntimeException("Invalid JSON format");

        try (MockedStatic<Game> gameMock = mockStatic(Game.class)) {
            gameMock.when(() -> Game.fromJson(invalidJsonData)).thenThrow(parseException);

            // Act & Assert
            IOException thrown = assertThrows(IOException.class, () -> {
                GameSerializer.load(testFile);
            });

            assertTrue(thrown.getMessage().contains("Failed to parse game data from file"));
            assertTrue(thrown.getMessage().contains(testFile.getName()));
            assertSame(parseException, thrown.getCause(), "Original exception should be preserved as cause");
            gameMock.verify(() -> Game.fromJson(invalidJsonData));
        }
    }

    /**
     * Tests that loading an empty file results in an IOException.
     */
    @Test
    @DisplayName("load() throws IOException for empty file")
    void testLoad_EmptyFile_ThrowsIOException() throws IOException {
        // Arrange
        Files.writeString(testFile.toPath(), "");
        RuntimeException parseException = new RuntimeException("Empty JSON content");

        try (MockedStatic<Game> gameMock = mockStatic(Game.class)) {
            gameMock.when(() -> Game.fromJson("")).thenThrow(parseException);

            // Act & Assert
            IOException thrown = assertThrows(IOException.class, () -> {
                GameSerializer.load(testFile);
            });

            assertTrue(thrown.getMessage().contains("Failed to parse game data from file"));
            assertSame(parseException, thrown.getCause());
            gameMock.verify(() -> Game.fromJson(""));
        }
    }

    /**
     * Tests a full save-then-load round trip to ensure data is correctly
     * preserved through the process.
     */
    @Test
    @DisplayName("save() and load() round-trip preserves game data")
    void testSaveLoad_RoundTrip_PreservesGameData() throws IOException {
        // Arrange
        String gameJson = "{\"level\":5,\"score\":1000}";
        when(mockGame.toJson()).thenReturn(gameJson);
        Game loadedGame = mock(Game.class);

        try (MockedStatic<Game> gameMock = mockStatic(Game.class)) {
            gameMock.when(() -> Game.fromJson(gameJson)).thenReturn(loadedGame);

            // Act
            GameSerializer.save(mockGame, testFile);
            Game result = GameSerializer.load(testFile);

            // Assert
            assertTrue(testFile.exists(), "File should exist after save");
            assertSame(loadedGame, result, "Loaded game should match expected");
            verify(mockGame).toJson();
            gameMock.verify(() -> Game.fromJson(gameJson));

            // Verify file content
            String fileContent = Files.readString(testFile.toPath());
            assertEquals(gameJson, fileContent);
        }
    }

    /**
     * Tests that saving a null game object throws a NullPointerException.
     */
    @Test
    @DisplayName("save() throws NullPointerException for null game")
    void testSave_NullGame_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            GameSerializer.save(null, testFile);
        });
    }

    /**
     * Tests that attempting to save to a null file object throws a NullPointerException.
     */
    @Test
    @DisplayName("save() throws NullPointerException for null file")
    void testSave_NullFile_ThrowsNullPointerException() {
        // Arrange
        when(mockGame.toJson()).thenReturn(validJsonData);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            GameSerializer.save(mockGame, null);
        });
    }

    /**
     * Tests that attempting to load from a null file object throws a NullPointerException.
     */
    @Test
    @DisplayName("load() throws NullPointerException for null file")
    void testLoad_NullFile_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            GameSerializer.load(null);
        });
    }

    /**
     * Tests that the serializer can handle large JSON data without issues.
     */
    @Test
    @DisplayName("save() handles large JSON data correctly")
    void testSave_LargeJsonData_HandlesCorrectly() throws IOException {
        // Arrange

        String largeJsonString = "{\"data\":\"" +
                // Create a large string (10KB)
                "x".repeat(10000) +
                "\"}";
        when(mockGame.toJson()).thenReturn(largeJsonString);

        // Act
        GameSerializer.save(mockGame, testFile);

        // Assert
        assertTrue(testFile.exists());
        String fileContent = Files.readString(testFile.toPath());
        assertEquals(largeJsonString, fileContent);
        assertTrue(testFile.length() > 10000, "File should contain large data");
        verify(mockGame).toJson();
    }

    /**
     * Tests that the serializer can handle JSON data containing special characters.
     */
    @Test
    @DisplayName("load() handles JSON with special characters correctly")
    void testLoad_SpecialCharactersInJson_HandlesCorrectly() throws IOException {
        // Arrange
        String jsonWithSpecialChars = "{\"name\":\"Test Game ñáéíóú\",\"emoji\":\"🎮🎯\"}";
        Files.writeString(testFile.toPath(), jsonWithSpecialChars);
        Game expectedGame = mock(Game.class);

        try (MockedStatic<Game> gameMock = mockStatic(Game.class)) {
            gameMock.when(() -> Game.fromJson(jsonWithSpecialChars)).thenReturn(expectedGame);

            // Act
            Game result = GameSerializer.load(testFile);

            // Assert
            assertSame(expectedGame, result);
            gameMock.verify(() -> Game.fromJson(jsonWithSpecialChars));
        }
    }
}
package university.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import university.exceptions.NoFileException;

class FileReaderTest {

    private final FileReader reader = new FileReader();
    private static final String FILE_LINES = "testfile.txt";    
    private static final String EMPTY_FILE = "emptyfile.txt";
    private static final String NON_EXISTING_FILE = "nonexisting.txt";

    @Test
    void read_ShouldReturnListStringsFromFile_WhenInputFileContainsLine() {
        List<String> expected = new ArrayList<>();
        expected.add("Math");
        expected.add("Physics");
        expected.add("Biology");
        List<String> actual = reader.read(FILE_LINES);
        assertEquals(expected, actual);
    }

    @Test
    void read_ShouldReturnEmptyList_WhenInputEmptyFile() {
        List<String> actual = reader.read(EMPTY_FILE);
        assertTrue(actual.isEmpty());
    }

    @Test
    void read_ShouldThrowNoFileException_WhenInputNonExistingFile() {
        assertThrows(NoFileException.class, () -> reader.read(NON_EXISTING_FILE));
    }

    @Test
    void read_ShouldThrowIllegalArgumentException_WhenNull() {
        assertThrows(IllegalArgumentException.class, () -> reader.read(null));
    }
}
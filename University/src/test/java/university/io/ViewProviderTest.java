package university.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class ViewProviderTest {

    private ViewProvider viewProvider;

    @BeforeEach
    void createTablesAndData() {
        viewProvider = new ViewProvider();
    }

    @Test
    void readString_ShouldReturnExpectedString_WhenInputStringViaConsole() {        
        String expected = "Test";
        ByteArrayInputStream input = new ByteArrayInputStream(expected.getBytes());
        System.setIn(input);
        String actual = viewProvider.readString();
        assertEquals(expected, actual);
    }

    @Test
    void readNumber_ShouldReturnExpectedNumber_WhenInputNumberViaConsole() {
        int expected = 5;
        ByteArrayInputStream input = new ByteArrayInputStream("5".getBytes());
        System.setIn(input);
        int actual = viewProvider.readNumber();        
        assertEquals(expected, actual);
    }

    @Test
    void printMessage_ShouldOutputInputInConsole_WhenInputString() {        
        String expected = "Test\r\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        viewProvider.printMessage("Test");
        String actual = output.toString();
        assertEquals(expected, actual);
    }
}

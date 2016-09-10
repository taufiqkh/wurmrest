import java.time.LocalDateTime;

import com.quiptiq.wurmrest.Result;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Basic tests for Result
 */
public class ResultTest {
    @Test
    public void errorResult() {
        Result<String> result = Result.error("Error");
        assertTrue("Result created with error should be in error", result.isError());
        assertFalse("Result created with error should not be success", result.isSuccess());
    }

    @Test
    public void successResult() {
        String value = "yay";
        Result<String> result = Result.success(value);
        assertFalse("Result created with success should not be in error", result.isError());
        assertTrue("Result created with success should identify as such", result.isSuccess());
        assertEquals("Result with success should retain value", value, result.getValue());
    }
}

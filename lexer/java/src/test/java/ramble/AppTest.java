package ramble;
import java.io.IOException;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import java.util.concurrent.Semaphore;
import java.util.Scanner;

/**
 * Unit test for simple App.
 */
public class AppTest{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws InterruptedException, IOException{
    	Semaphore englishLock = new Semaphore(1);
        Lexer testLexer = new Lexer(englishLock, "english");
        testLexer.parse();
        assertTrue(true);
    }
}

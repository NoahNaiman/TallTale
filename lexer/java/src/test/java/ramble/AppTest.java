package ramble;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws IOException{
    	Lexer testLexer = new Lexer();
    	testLexer.parseBook("JungleBook", ".txt");
        assertTrue(true);
    }
}

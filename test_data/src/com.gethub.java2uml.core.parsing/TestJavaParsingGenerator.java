import com.github.java2uml.core.Main;
import com.github.java2uml.core.parsing.CreateUmlCode;
import com.github.java2uml.core.parsing.CreateUmlCodeException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by nadcukandrej on 21.01.15.
 */
public class TestJavaParsingGenerator {
    
    @Test
    public void checkCreateUmlCode(){
        String examplePackage = "/Users/name/javaProject";
        String msg = null;
        try {
            new Main().main(new String[]{"java", examplePackage});
            Assert.assertNull(msg);
        } catch (CreateUmlCodeException e) {
            msg = e.getMessage();
            Assert.assertEquals("Folder is not exist", msg);
        } catch (Exception e) {
            e.getMessage();
        }

    }
}

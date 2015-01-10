package foo;


import demo.model.User;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * @Author dave 8/8/14 9:37 PM
 */
public class PlaygroundTests {

//    @Test
    public void converterTest(){
        ConversionService cs = new DefaultConversionService();
        String foo = cs.convert("Foo bar baz", String.class);
        Assert.assertEquals("Foo bar baz", foo);
    }

}

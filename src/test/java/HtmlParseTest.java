import VanMorrison.HtmlParser;
import org.junit.Test;

import static org.junit.Assert.*;


public class HtmlParseTest {
    @Test
    public void testSet(){

        HtmlParser parser = new HtmlParser("src/test/resources/testFileHtmlParser.html");

        //original = "test test ${hej} ab{${test1} hshshhsha kakka }}}}${test2}";
        String text = "test test inte hej ab{1tset hshshhsha kakka }}}}bra testat";
        parser.set("hej", "inte hej");
        parser.set("test1", "1tset");
        parser.set("test2", "bra testat");

        assertEquals(parser.getString(), text);
    }

    @Test
    public void doubleKeysTest(){
        HtmlParser parser = new HtmlParser("src/test/resources/testFileHtmlParser2.html");

        //original = "test test ${hej} ab{${test1}${test3} hshshhsha kakka }}}}${test2}";
        String text = "test test inte hej ab{1tsetajajaj hshshhsha kakka }}}}bra testat";
        parser.set("hej", "inte hej");
        parser.set("test1", "1tset");
        parser.set("test2", "bra testat");
        parser.set("test3", "ajajaj");

        assertEquals(parser.getString(), text);
    }

    @Test
    public void repeated(){
        HtmlParser parser = new HtmlParser("src/test/resources/testFileHtmlParser3.html");

        //original = "test test ${hej} ab{${test2} hshshhsha kakka }}}}${test2}${test2}";
        String text = "test test inte hej ab{bra hshshhsha kakka }}}}brabra";
        parser.set("hej", "inte hej");
        parser.set("test2", "bra");

        assertEquals(parser.getString(), text);
    }

    @Test
    public void manyRepeats(){
        HtmlParser parser = new HtmlParser("src/test/resources/testFileHtmlParser4.html");

        //original = "test ${test1}${test1}${test1}, ${test1}${test1} och några till ${test1}${test1}${test1}${test1}";
        String text = "test ha ha ha , ha ha  och lite till ha ha ha ha ";
        parser.set("test1", "ha ");

        assertEquals(parser.getString(), text);
    }

    @Test
    public void noReplace(){
        HtmlParser parser = new HtmlParser("src/test/resources/testFileHtmlParser.html");

        //original = "test test ${hej} ab{${test1} hshshhsha kakka }}}}${test2}";
        String text = "test test  ab{ hshshhsha kakka }}}}";

        assertEquals(parser.getString(), text);
    }

    @Test
    public void settingNoneExisting(){
        HtmlParser parser = new HtmlParser("src/test/resources/testFileHtmlParser.html");

        //original = "test test ${hej} ab{${test1} hshshhsha kakka }}}}${test2}";
        String text = "test test  ab{ hshshhsha kakka }}}}";
        parser.set("123", "321");

        assertEquals(parser.getString(), text);
    }
}

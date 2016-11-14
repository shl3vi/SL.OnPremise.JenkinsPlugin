package io.sealights.plugins.sealightsjenkins.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * Created by shahar on 11/8/2016.
 */
public class PropertiesUtilsTest {

    private static final String newLine = System.getProperty("line.separator");

    @Test
    public void toProperties_giveGoodString_shouldReturnValidProperties() {
        String propsStr = "hello=world" + newLine + "black=white";
        Properties p = PropertiesUtils.toProperties(propsStr);

        Assert.assertTrue("Returned properties should contain the key 'hello'", p.containsKey("hello"));
        Assert.assertTrue("Returned properties should contain the key 'black'", p.containsKey("black"));
        Assert.assertEquals("Value of key 'hello' should be 'world'", "world", p.get("hello"));
        Assert.assertEquals("Value of key 'black' should be 'white'", "white", p.get("black"));
    }

    @Test
    public void toProperties_giveGoodString_windowsEnding_shouldReturnValidProperties() {
        String propsStr = "hello=world" + StringUtils.windows_newline + "black=white";
        Properties p = PropertiesUtils.toProperties(propsStr);

        Assert.assertTrue("Returned properties should contain the key 'hello'", p.containsKey("hello"));
        Assert.assertTrue("Returned properties should contain the key 'black'", p.containsKey("black"));
        Assert.assertEquals("Value of key 'hello' should be 'world'", "world", p.get("hello"));
        Assert.assertEquals("Value of key 'black' should be 'white'", "white", p.get("black"));
    }

    @Test
    public void toProperties_giveGoodString_unixEnding_shouldReturnValidProperties() {
        String propsStr = "hello=world" + StringUtils.unix_newline + "black=white";
        Properties p = PropertiesUtils.toProperties(propsStr);

        Assert.assertTrue("Returned properties should contain the key 'hello'", p.containsKey("hello"));
        Assert.assertTrue("Returned properties should contain the key 'black'", p.containsKey("black"));
        Assert.assertEquals("Value of key 'hello' should be 'world'", "world", p.get("hello"));
        Assert.assertEquals("Value of key 'black' should be 'white'", "white", p.get("black"));
    }

    @Test
    public void toProperties_giveKeyWithLeadingAndTrailingSpaces_shouldReturnValidProperties() {
        String propsStr = "hello    =world" + newLine + "  black =white";
        Properties p = PropertiesUtils.toProperties(propsStr);

        Assert.assertTrue("Returned properties should contain the key 'hello'", p.containsKey("hello"));
        Assert.assertTrue("Returned properties should contain the key 'black'", p.containsKey("black"));
        Assert.assertEquals("Value of key 'hello' should be 'world'", "world", p.get("hello"));
        Assert.assertEquals("Value of key 'black' should be 'white'", "white", p.get("black"));
    }

    @Test
    public void toProperties_giveKeyWithSpaceInMiddle_shouldReturnValidPropertiesWithoutThisKey() {
        String propsStr = "hello=world" + newLine + "bl ack =white";
        Properties p = PropertiesUtils.toProperties(propsStr);

        Assert.assertTrue("Properties should contain only one key", p.size() == 1);
        Assert.assertTrue("Returned properties should contain the key 'hello'", p.containsKey("hello"));
        Assert.assertEquals("Value of key 'hello' should be 'world'", "world", p.get("hello"));
    }

    @Test
    public void toProperties_giveValueWithLeadingAndTrailingSpaces_shouldRemoveLeadingAndTrailingSpaces() {
        String propsStr = "hello=  wo rld  ";
        Properties p = PropertiesUtils.toProperties(propsStr);

        Assert.assertTrue("Returned properties should contain the key 'hello'", p.containsKey("hello"));
        Assert.assertEquals("Value of key 'hello' should be 'world'", "wo rld", p.get("hello"));
    }

    @Test
    public void toProperties_giveNonValidLine_shouldNotContainThisLine() {
        String propsStr = "hello=world" + newLine + "notValid";
        Properties p = PropertiesUtils.toProperties(propsStr);

        Assert.assertTrue("Properties should contain only one key", p.size() == 1);
        Assert.assertTrue("Returned properties should contain the key 'hello'", p.containsKey("hello"));
        Assert.assertEquals("Value of key 'hello' should be 'world'", "world", p.get("hello"));
    }


}

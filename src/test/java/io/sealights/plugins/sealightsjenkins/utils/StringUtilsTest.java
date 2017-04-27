package io.sealights.plugins.sealightsjenkins.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Ronis on 4/26/2017.
 */
public class StringUtilsTest {

    @Test
    public void trimStart_lengthGraterThen10_shouldReturnTrimmedString(){

        //Arrange
        String original = "abcdefghijk";

        //Act
        String trimmed = StringUtils.trimStart(original);

        //Assert
        Assert.assertEquals("..."+original.substring(original.length()-10),trimmed);
    }

    @Test
    public void trimStart_lengthLowerThen10_shouldReturnOriginalString(){

        //Arrange
        String original = "abcdefg";

        //Act
        String trimmed = StringUtils.trimStart(original);

        //Assert
        Assert.assertEquals(original,trimmed);
    }

    @Test
    public void trimStart_strIsNull_shouldReturnNull(){

        //Arrange
        String original =null;

        //Act
        String trimmed = StringUtils.trimStart(original);

        //Assert
        Assert.assertEquals(original,trimmed);

    }


    @Test
    public void trimStartByValue_lengthGraterThen10_shouldReturnTrimmedString(){

        //Arrange
        String original = "abcdefghijk";

        //Act
        String trimmed = StringUtils.trimStart(original,5);

        //Assert
        Assert.assertEquals("..."+original.substring(original.length()-5),trimmed);
    }

    @Test
    public void trimStartByValue_lengthLowerThenWantedLength_shouldReturnOriginalString(){

        //Arrange
        String original = "abcdefghijk";

        //Act
        String trimmed = StringUtils.trimStart(original,17);

        //Assert
        Assert.assertEquals(original,trimmed);
    }
}

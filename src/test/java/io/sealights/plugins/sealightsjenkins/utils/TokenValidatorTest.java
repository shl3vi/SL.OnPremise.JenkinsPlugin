package io.sealights.plugins.sealightsjenkins.utils;



import io.sealights.plugins.sealightsjenkins.entities.TokenData;
import io.sealights.plugins.sealightsjenkins.entities.ValidationError;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Nadav on 11/7/2016.
 */
public class TokenValidatorTest {
    @Test
    public void validate_nullTokenData_shouldReturnInvalidTokenError() {
        //Arrange
        TokenData tokenData = null;
        TokenValidator tokenValidator = new TokenValidator();

        //Act
        List<ValidationError> validationErrors = tokenValidator.validate(tokenData);

        int expectedErrors = 1;

        //Assert
        Assert.assertEquals("Expected to have '"+expectedErrors+"' validation error(s) but got '" + validationErrors.size() +"'.", validationErrors.size(), expectedErrors);
        Assert.assertEquals("Expected to have a field called '"+TokenValidator.TOKEN_DATA_FIELD+"'  but got '" + validationErrors.get(0).getName() +"'.", validationErrors.get(0).getName(), TokenValidator.TOKEN_DATA_FIELD);
        Assert.assertEquals("Expected to have problem of '"+TokenValidator.FIELD_CANT_BE_NULL+"'  but got '" + validationErrors.get(0).getProblem()+"'.", validationErrors.get(0).getProblem(), TokenValidator.FIELD_CANT_BE_NULL);
    }

    @Test
    public void validate_validTokenData_shouldNoValidationErrors() {
        //Arrange
        TokenData tokenData = new TokenData();
        tokenData.setRole(TokenData.AgentRole);
        tokenData.setSubject("a@MyCustomerId");
        tokenData.setServer("https://www.server.com");
        TokenValidator tokenValidator = new TokenValidator();

        //Act
        List<ValidationError> validationErrors = tokenValidator.validate(tokenData);

        int expectedErrors = 0;

        //Assert
        Assert.assertEquals("Expected to have '"+expectedErrors+"' validation error(s) but got '" + validationErrors.size() +"'.", validationErrors.size(), expectedErrors);
    }

    @Test
    public void validate_missingCustomerId_shouldReturnASingleValidationError() {
        //Arrange
        TokenData tokenData = new TokenData();
        tokenData.setRole(TokenData.AgentRole);
        //tokenData.setSubject("a@MyCustomerId");
        tokenData.setServer("https://www.server.com");
        TokenValidator tokenValidator = new TokenValidator();

        //Act
        List<ValidationError> validationErrors = tokenValidator.validate(tokenData);

        int expectedErrors = 1;

        //Assert
        Assert.assertEquals("Expected to have '"+expectedErrors+"' validation error(s) but got '" + validationErrors.size() +"'.", validationErrors.size(), expectedErrors);
        Assert.assertEquals("Expected to have a field called '"+TokenValidator.CUSTOMER_ID_FIELD+"'  but got '" + validationErrors.get(0).getName() +"'.", validationErrors.get(0).getName(), TokenValidator.CUSTOMER_ID_FIELD);
        Assert.assertEquals("Expected to have problem of '"+TokenValidator.FIELD_CANT_BE_NULL_OR_EMPTY+"'  but got '" + validationErrors.get(0).getProblem()+"'.", validationErrors.get(0).getProblem(), TokenValidator.FIELD_CANT_BE_NULL_OR_EMPTY);
    }

    @Test
    public void validate_missingServer_shouldReturnASingleValidationError() {
        //Arrange
        TokenData tokenData = new TokenData();
        tokenData.setRole(TokenData.AgentRole);
        tokenData.setSubject("a@MyCustomerId");
        //tokenData.setServer("https://www.server.com");
        TokenValidator tokenValidator = new TokenValidator();

        //Act
        List<ValidationError> validationErrors = tokenValidator.validate(tokenData);

        int expectedErrors = 1;

        //Assert
        Assert.assertEquals("Expected to have '"+expectedErrors+"' validation error(s) but got '" + validationErrors.size() +"'.", validationErrors.size(), expectedErrors);
        Assert.assertEquals("Expected to have a field called '"+TokenValidator.SERVER_FIELD+"'  but got '" + validationErrors.get(0).getName() +"'.", validationErrors.get(0).getName(), TokenValidator.SERVER_FIELD);
        Assert.assertEquals("Expected to have problem of '"+TokenValidator.FIELD_CANT_BE_NULL_OR_EMPTY+"'  but got '" + validationErrors.get(0).getProblem()+"'.", validationErrors.get(0).getProblem(), TokenValidator.FIELD_CANT_BE_NULL_OR_EMPTY);
    }

    @Test
    public void validate_missingRole_shouldReturnASingleValidationError() {
        //Arrange
        TokenData tokenData = new TokenData();
        //tokenData.setRole(TokenData.AgentRole);
        tokenData.setSubject("a@MyCustomerId");
        tokenData.setServer("https://www.server.com");
        TokenValidator tokenValidator = new TokenValidator();

        //Act
        List<ValidationError> validationErrors = tokenValidator.validate(tokenData);

        int expectedErrors = 1;

        //Assert

        Assert.assertEquals("Expected to have '"+expectedErrors+"' validation error(s) but got '" + validationErrors.size() +"'.", validationErrors.size(), expectedErrors);
        Assert.assertEquals("Expected to have a field called '"+TokenValidator.SERVER_FIELD+"'  but got '" + validationErrors.get(0).getName() +"'.", validationErrors.get(0).getName(), TokenValidator.ROLE_FIELD);
        Assert.assertEquals("Expected to have problem of '"+TokenValidator.FIELD_CANT_BE_NULL_OR_EMPTY+"'  but got '" + validationErrors.get(0).getProblem()+"'.", validationErrors.get(0).getProblem(), TokenValidator.FIELD_CANT_BE_NULL_OR_EMPTY);
    }

    @Test
    public void validate_invalidRole_shouldReturnASingleValidationError() {
        //Arrange
        TokenData tokenData = new TokenData();
        tokenData.setRole("FAKE_ROLE");
        tokenData.setSubject("a@MyCustomerId");
        tokenData.setServer("https://www.server.com");
        TokenValidator tokenValidator = new TokenValidator();

        //Act
        List<ValidationError> validationErrors = tokenValidator.validate(tokenData);

        int expectedErrors = 1;

        //Assert
        Assert.assertEquals("Expected to have '"+expectedErrors+"' validation error(s) but got '" + validationErrors.size() +"'.", validationErrors.size(), expectedErrors);
        Assert.assertEquals("Expected to have a field called '"+TokenValidator.SERVER_FIELD+"'  but got '" + validationErrors.get(0).getName() +"'.", validationErrors.get(0).getName(), TokenValidator.ROLE_FIELD);

        boolean containRuleName =  validationErrors.get(0).getProblem().contains("FAKE_ROLE");
        Assert.assertTrue("Expected that the problem will include the FAKE_ROLE string but got a problem of:'" + validationErrors.get(0).getProblem()+"'.", containRuleName);
    }
}

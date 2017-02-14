package io.sealights.plugins.sealightsjenkins.model;

import hudson.EnvVars;
import io.sealights.plugins.sealightsjenkins.TestHelper;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.NullLogger;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shahar on 2/14/2017.
 */
public class VariableInjectionActionTest {

    private TestHelper testHelper = new TestHelper();

    @Test
    public void buildEnvVars_additionalEnvVarsWithGoodValues_shouldInsertAllAdditionalEnvVars() throws IOException {
        // Arrange
        EnvVars actualEnvVars = createEnvVars();
        Map<String, String> additionalEnvVars = createAdditionalEnvVars();
        Logger logger = new NullLogger();
        VariableInjectionAction variableInjectionAction = new VariableInjectionAction(additionalEnvVars, logger);

        // act
        variableInjectionAction.buildEnvVars(testHelper.createBuildMock(), actualEnvVars);

        // assert
        EnvVars expectedEnvVars = createEnvVars();
        expectedEnvVars.put("SEALIGHTS_KEY1", "VAULE1");
        expectedEnvVars.put("SEALIGHTS_KEY2", "VAULE2");
        expectedEnvVars.put("SEALIGHTS_KEY3", "VAULE3");

        assertEnvVarsMaps(expectedEnvVars, actualEnvVars);
    }

    @Test
    public void buildEnvVars_additionalEnvVarsWithNullKey_shouldInsertAllAdditionalEnvVars() throws IOException {
        // Arrange
        EnvVars actualEnvVars = createEnvVars();
        HashMap<String, String> additionalEnvVars = createAdditionalEnvVars();
        additionalEnvVars.put(null, "value of null key");
        Logger logger = new NullLogger();
        VariableInjectionAction variableInjectionAction = new VariableInjectionAction(additionalEnvVars, logger);

        // act
        variableInjectionAction.buildEnvVars(testHelper.createBuildMock(), actualEnvVars);

        // assert
        EnvVars expectedEnvVars = createEnvVars();
        expectedEnvVars.put("SEALIGHTS_KEY1", "VAULE1");
        expectedEnvVars.put("SEALIGHTS_KEY2", "VAULE2");
        expectedEnvVars.put("SEALIGHTS_KEY3", "VAULE3");

        assertEnvVarsMaps(expectedEnvVars, actualEnvVars);
    }

    @Test
    public void buildEnvVars_nullAsAdditionalEnvVars_shouldNotCrush() throws IOException {
        // Arrange
        EnvVars actualEnvVars = createEnvVars();
        HashMap<String, String> additionalEnvVars = null;
        Logger logger = new NullLogger();
        VariableInjectionAction variableInjectionAction = new VariableInjectionAction(additionalEnvVars, logger);

        // act
        variableInjectionAction.buildEnvVars(testHelper.createBuildMock(), actualEnvVars);

        // assert
        EnvVars expectedEnvVars = createEnvVars();
        assertEnvVarsMaps(expectedEnvVars, actualEnvVars);
    }

    private void assertEnvVarsMaps(EnvVars expectedEnvVars, EnvVars actualEnvVars) {
        Assert.assertEquals("The EnvVars map size is not as expected", expectedEnvVars.size(), actualEnvVars.size());
        Assert.assertEquals("The EnvVars map content is not as expected", expectedEnvVars, actualEnvVars);
    }

    private EnvVars createEnvVars() {
        EnvVars envVars = new EnvVars();
        envVars.put("KEY1", "VAULE1");
        envVars.put("KEY2", "VAULE2");
        return envVars;
    }

    private HashMap<String, String> createAdditionalEnvVars() {
        HashMap<String, String> sealightsMap = new HashMap<>();
        sealightsMap.put("SEALIGHTS_KEY1", "VAULE1");
        sealightsMap.put("SEALIGHTS_KEY2", "VAULE2");
        sealightsMap.put("SEALIGHTS_KEY3", "VAULE3");
        return sealightsMap;
    }
}

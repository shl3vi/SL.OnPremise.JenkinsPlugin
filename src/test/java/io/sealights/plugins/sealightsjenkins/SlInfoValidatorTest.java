package io.sealights.plugins.sealightsjenkins;

import io.sealights.plugins.sealightsjenkins.integration.SeaLightsPluginInfo;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.NullLogger;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by shahar on 2/14/2017.
 */
public class SlInfoValidatorTest {

    private Logger logger = new NullLogger();

    @Test
    public void validate_giveNullAsPluginInfo_shouldReturnTrue() {
        // Arrange
        SlInfoValidator slInfoValidator = new SlInfoValidator(logger);
        SeaLightsPluginInfo slInfo = null;

        // Act
        boolean isValid = slInfoValidator.validate(slInfo);

        // Assert
        Assert.assertFalse("Plugin info should not be valid when plugin info is null", isValid);
    }

    @Test
    public void validate_withoutAppName_shouldReturnFalse() {
        // Arrange
        SlInfoValidator slInfoValidator = new SlInfoValidator(logger);
        SeaLightsPluginInfo slInfo = new SeaLightsPluginInfo();
        slInfo.setBuildSessionId(null);
        slInfo.setCreateBuildSessionId(true);
        slInfo.setAppName(null);
        slInfo.setBuildName("1");
        slInfo.setBranchName("branchy");
        slInfo.setPackagesIncluded("io.demo");

        // Act
        boolean isValid = slInfoValidator.validate(slInfo);

        // Assert
        Assert.assertFalse("Plugin info should not be valid without app name", isValid);
    }

    @Test
    public void validate_withoutBuildName_shouldReturnFalse() {
        // Arrange
        SlInfoValidator slInfoValidator = new SlInfoValidator(logger);
        SeaLightsPluginInfo slInfo = new SeaLightsPluginInfo();
        slInfo.setBuildSessionId(null);
        slInfo.setCreateBuildSessionId(true);
        slInfo.setAppName("app");
        slInfo.setBuildName(null);
        slInfo.setBranchName("branchy");
        slInfo.setPackagesIncluded("io.demo");

        // Act
        boolean isValid = slInfoValidator.validate(slInfo);

        // Assert
        Assert.assertFalse("Plugin info should not be valid without build name", isValid);
    }

    @Test
    public void validate_withoutBranchName_shouldReturnFalse() {
        // Arrange
        SlInfoValidator slInfoValidator = new SlInfoValidator(logger);
        SeaLightsPluginInfo slInfo = new SeaLightsPluginInfo();
        slInfo.setBuildSessionId(null);
        slInfo.setCreateBuildSessionId(true);
        slInfo.setAppName("app");
        slInfo.setBuildName("1");
        slInfo.setBranchName(null);
        slInfo.setPackagesIncluded("io.demo");

        // Act
        boolean isValid = slInfoValidator.validate(slInfo);

        // Assert
        Assert.assertFalse("Plugin info should not be valid without branch name", isValid);
    }

    @Test
    public void validate_withoutPackagesIncluded_shouldReturnFalse() {
        // Arrange
        SlInfoValidator slInfoValidator = new SlInfoValidator(logger);
        SeaLightsPluginInfo slInfo = new SeaLightsPluginInfo();
        slInfo.setBuildSessionId(null);
        slInfo.setCreateBuildSessionId(true);
        slInfo.setAppName("app");
        slInfo.setBuildName("1");
        slInfo.setBranchName("branchy");
        slInfo.setPackagesIncluded(null);

        // Act
        boolean isValid = slInfoValidator.validate(slInfo);

        // Assert
        Assert.assertFalse("Plugin info should not be valid without packages included", isValid);
    }

    @Test
    public void validate_withAllRequiredParameters_shouldReturnTrue() {
        // Arrange
        SlInfoValidator slInfoValidator = new SlInfoValidator(logger);
        SeaLightsPluginInfo slInfo = new SeaLightsPluginInfo();
        slInfo.setBuildSessionId(null);
        slInfo.setCreateBuildSessionId(true);
        slInfo.setAppName("app");
        slInfo.setBuildName("1");
        slInfo.setBranchName("branchy");
        slInfo.setPackagesIncluded("io.demo");

        // Act
        boolean isValid = slInfoValidator.validate(slInfo);

        // Assert
        Assert.assertTrue("Plugin info should be valid when all parameters are provided", isValid);
    }

    @Test
    public void validate_withBuildSessionIdWithCreateBuildSessionIdNoParameters_shouldReturnFalse() {
        // Arrange
        SlInfoValidator slInfoValidator = new SlInfoValidator(logger);
        SeaLightsPluginInfo slInfo = new SeaLightsPluginInfo();
        slInfo.setBuildSessionId("build-session-id");
        slInfo.setCreateBuildSessionId(true);
        slInfo.setAppName(null);
        slInfo.setBuildName(null);
        slInfo.setBranchName(null);
        slInfo.setPackagesIncluded(null);

        // Act
        boolean isValid = slInfoValidator.validate(slInfo);

        // Assert
        Assert.assertFalse("Plugin info should not be valid when createBuildSessionId is true and no parameters are provided", isValid);
    }

    @Test
    public void validate_withoutCreateBuildSessionIdAndWithBuildSessionId_shouldReturnTrue() {
        // Arrange
        SlInfoValidator slInfoValidator = new SlInfoValidator(logger);
        SeaLightsPluginInfo slInfo = new SeaLightsPluginInfo();
        slInfo.setBuildSessionId("build-session-id");
        slInfo.setCreateBuildSessionId(false);
        slInfo.setAppName(null);
        slInfo.setBuildName(null);
        slInfo.setBranchName(null);
        slInfo.setPackagesIncluded(null);

        // Act
        boolean isValid = slInfoValidator.validate(slInfo);

        // Assert
        Assert.assertTrue("Plugin info should be valid when build session id is provided", isValid);
    }

}

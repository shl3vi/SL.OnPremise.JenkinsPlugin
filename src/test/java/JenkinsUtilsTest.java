import hudson.EnvVars;
import io.sealights.plugins.sealightsjenkins.utils.JenkinsUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by shahar on 9/1/2016.
 */
public class JenkinsUtilsTest {

    @Test
    public void resolveNonEnvironmentVariableWithoutCurlyBrackets_ShouldReturnTheNonEnvironmentVariable(){
        String variable = "non environment variable";
        String expected = variable;
        performTest(variable, expected);
    }

    @Test
    public void resolveNonEnvironmentVariableWithCurlyBrackets_ShouldReturnTheNonEnvironmentVariable(){
        String variable = "${non environment variable}";
        String expected = variable;
        performTest(variable, expected);
    }

    @Test
    public void resolveEnvironmentVariableWithCurlyBrackets_ShouldReturnTheResolvedEnvironmentVariable(){
        String variable = "${BUILD_NAME}";
        String expected = "1";
        performTest(variable, expected);
    }

    @Test
    public void resolveEnvironmentVariableWithoutCurlyBrackets_ShouldReturnTheEnvironmentVariableUnresolved(){
        String variable = "BUILD_NAME";
        String expected = variable;
        performTest(variable, expected);
    }

    @Test
    public void resolveNull_ShouldReturnEmptyString(){
        String variable = null;
        String expected = "";
        performTest(variable, expected);
    }

    private void performTest(String var, String expected){
        EnvVars envVars = createEnvVars();
        String actual = JenkinsUtils.tryGetEnvVariable(envVars, var);
        Assert.assertEquals(expected, actual);
    }

    private EnvVars createEnvVars(){
        EnvVars envVars = new EnvVars();
        envVars.put("BUILD_NAME", "1");
        return envVars;
    }
}

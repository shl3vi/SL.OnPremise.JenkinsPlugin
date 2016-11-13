package io.sealights.plugins.sealightsjenkins.buildsteps.commands.executors;

import io.sealights.plugins.sealightsjenkins.buildsteps.commands.CommandMode;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.BaseCommandArguments;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.NullLogger;
import org.junit.Assert;
import org.junit.Test;

public class CommandExecutorsFactoryTest {

    private Logger nullLogger = new NullLogger();

    @Test
    public void createExecutor_withStartMode_shouldGetStartCommandExecutor() {
        //Arrange
        CommandExecutorsFactory factory = new CommandExecutorsFactory();
        CommandMode mode = new CommandMode.StartView("");
        //Act
        ICommandExecutor executor = factory.createExecutor(nullLogger, createBaseCommandArguments(mode));
        //Assert
        boolean isStartExecutor = executor instanceof StartCommandExecutor;
        Assert.assertTrue("The created executor is not an instance of 'StartCommandExecutor'", isStartExecutor);
    }

    @Test
    public void createExecutor_withEndMode_shouldGetStartCommandExecutor() {
        //Arrange
        CommandExecutorsFactory factory = new CommandExecutorsFactory();
        CommandMode mode = new CommandMode.EndView();
        //Act
        ICommandExecutor executor = factory.createExecutor(nullLogger, createBaseCommandArguments(mode));
        //Assert
        boolean isEndExecutor = executor instanceof EndCommandExecutor;
        Assert.assertTrue("The created executor is not an instance of 'EndCommandExecutor'", isEndExecutor);
    }

    @Test
    public void createExecutor_withUploadReportsMode_shouldGetStartCommandExecutor() {
        //Arrange
        CommandExecutorsFactory factory = new CommandExecutorsFactory();
        CommandMode mode = new CommandMode.UploadReportsView("", "", false, "");
        //Act
        ICommandExecutor executor = factory.createExecutor(nullLogger, createBaseCommandArguments(mode));
        //Assert
        boolean isUploadReportsExecutor = executor instanceof UploadReportsCommandExecutor;
        Assert.assertTrue("The created executor is not an instance of 'UploadReportsCommandExecutor'", isUploadReportsExecutor);
    }

    @Test
    public void createExecutor_withNullBaseArguments_shouldGetNullCommandExecutor() {
        //Arrange
        CommandExecutorsFactory factory = new CommandExecutorsFactory();
        //Act
        ICommandExecutor executor = factory.createExecutor(nullLogger, null);
        //Assert
        boolean isNullExecutor = executor instanceof NullCommandExecutor;
        Assert.assertTrue("The created executor is not an instance of 'NullCommandExecutor'", isNullExecutor);
    }

    @Test
    public void createExecutor_withNullMode_shouldGetNullCommandExecutor() {
        //Arrange
        CommandExecutorsFactory factory = new CommandExecutorsFactory();
        //Act
        ICommandExecutor executor = factory.createExecutor(nullLogger, createBaseCommandArguments(null));
        //Assert
        boolean isNullExecutor = executor instanceof NullCommandExecutor;
        Assert.assertTrue("The created executor is not an instance of 'NullCommandExecutor'", isNullExecutor);
    }


    private BaseCommandArguments createBaseCommandArguments(CommandMode mode) {
        BaseCommandArguments baseArgs = new BaseCommandArguments();
        baseArgs.setMode(mode);
        return baseArgs;
    }
}

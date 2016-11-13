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
    public void createExecutor_withStartMode_shouldGetStartCommandExecutor(){
        CommandExecutorsFactory factory = new CommandExecutorsFactory();
        CommandMode mode = new CommandMode.StartView("");
        ICommandExecutor executor = factory.createExecutor(nullLogger, createBaseCommandArguments(mode));
        boolean isStartExecutor = executor instanceof StartCommandExecutor;
        Assert.assertTrue(isStartExecutor);
    }

    @Test
    public void createExecutor_withEndMode_shouldGetStartCommandExecutor(){
        CommandExecutorsFactory factory = new CommandExecutorsFactory();
        CommandMode mode = new CommandMode.EndView();
        ICommandExecutor executor = factory.createExecutor(nullLogger, createBaseCommandArguments(mode));
        boolean isEndExecutor = executor instanceof EndCommandExecutor;
        Assert.assertTrue(isEndExecutor);
    }

    @Test
    public void createExecutor_withUploadReportsMode_shouldGetStartCommandExecutor(){
        CommandExecutorsFactory factory = new CommandExecutorsFactory();
        CommandMode mode = new CommandMode.UploadReportsView("","",false,"");
        ICommandExecutor executor = factory.createExecutor(nullLogger, createBaseCommandArguments(mode));
        boolean isUploadReportsExecutor = executor instanceof UploadReportsCommandExecutor;
        Assert.assertTrue(isUploadReportsExecutor);
    }

    @Test
    public void createExecutor_withNullBaseArguments_shouldGetNullCommandExecutor(){
        CommandExecutorsFactory factory = new CommandExecutorsFactory();
        ICommandExecutor executor = factory.createExecutor(nullLogger, null);
        boolean isUploadReportsExecutor = executor instanceof NullCommandExecutor;
        Assert.assertTrue(isUploadReportsExecutor);
    }

    @Test
    public void createExecutor_withNullMode_shouldGetNullCommandExecutor(){
        CommandExecutorsFactory factory = new CommandExecutorsFactory();
        ICommandExecutor executor = factory.createExecutor(nullLogger, createBaseCommandArguments(null));
        boolean isUploadReportsExecutor = executor instanceof NullCommandExecutor;
        Assert.assertTrue(isUploadReportsExecutor);
    }



    private BaseCommandArguments createBaseCommandArguments(CommandMode mode){
        BaseCommandArguments baseArgs = new BaseCommandArguments();
        baseArgs.setMode(mode);
        return baseArgs;
    }
}

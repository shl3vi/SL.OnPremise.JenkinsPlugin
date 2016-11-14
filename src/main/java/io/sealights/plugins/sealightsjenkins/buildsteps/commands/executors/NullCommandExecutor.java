package io.sealights.plugins.sealightsjenkins.buildsteps.commands.executors;

/**
 * null executor.
 */
public class NullCommandExecutor implements ICommandExecutor{

    public NullCommandExecutor() {
    }

    @Override
    public boolean execute() {
        return true;
    }
}

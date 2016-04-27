package io.sealigths.plugins.sealightsjenkins;


import hudson.FilePath;
import hudson.Launcher;
import hudson.Extension;
import hudson.tasks.*;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.AbstractProject;
import io.sealigths.plugins.sealightsjenkins.integration.FileUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

/**
 * @author Nadav
 */
public class RestoreBuildFile extends Recorder {

    private final boolean shouldRestore;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public RestoreBuildFile(boolean shouldRestore) {
        this.shouldRestore = shouldRestore;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public boolean getShouldRestore() {
        return shouldRestore;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        PrintStream logger = listener.getLogger();

        if (this.shouldRestore) {
            log(logger, "Searching for files to restore.");
            FilePath ws = build.getWorkspace();
            if (ws == null) {
                log(logger, "Failed to retrieve workspace path.");
                return false;
            }
            

            File currentDirectory = new File(ws.getRemote());
            boolean recursive = false;
            List<String> filesToRestore = FileUtils.searchFilesByExtension(currentDirectory, recursive, "slbak");
            for (String currentName : filesToRestore) {
                String newName = currentName.replace(".slbak","");
                boolean isSuccess = FileUtils.renameFileOrFolder(currentName, newName);
                if (isSuccess)
                    log(logger, "Restored '" + currentName + "' to '" + newName + "'.");
                else
                    log(logger, "Failed restoring '" + currentName + "' to '" + newName + "'.");
            }
        } else {
            log(logger, "No need to restore any files.");
        }

        return true;
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }


    /**
     * Descriptor for {@link RestoreBuildFile}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     * <p>
     * See <tt>src/main/resources/org/jenkinsci/plugins/testExample/RestoreBuildFile/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */


        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }


        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "SeaLights - Restore Build File";
        }

    }

    private void log(PrintStream logger, String message) {
        message = "[SeaLights Jenkins Plugin] " + message;
        logger.println(message);
    }
}

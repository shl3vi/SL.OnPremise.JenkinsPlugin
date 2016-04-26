package io.sealigths.plugins.sealightsjenkins;


import hudson.Launcher;
import hudson.Extension;
import hudson.model.Action;
import hudson.tasks.*;
import hudson.util.FormValidation;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.AbstractProject;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintStream;

/**
 *
 * @author Nadav
 */
public class TestExamplePublisher extends Recorder {

    private final boolean shouldRestore;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public TestExamplePublisher(boolean shouldRestore) {
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
        // This is where you 'build' the project.
        // Since this is a dummy, we just say 'hello world' and call that a build.

        PrintStream logger = listener.getLogger();

        // This also shows how you can consult the global configuration of the builder
        String message;
        if (this.shouldRestore)
            message = "Bonjour, " + shouldRestore + "!";
        else
            message = "Hello, " + shouldRestore + "!";

        logger.println(message);

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
     * Descriptor for {@link TestExamplePublisher}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.

     * See <tt>src/main/resources/org/jenkinsci/plugins/testExample/TestExamplePublisher/*.jelly</tt>
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
}

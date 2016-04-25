package io.sealigths.plugins.sealightsjenkins.language;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by shahar on 4/25/2016.
 */
public class Java implements CodeLanguage{

    private JavaFramework javaFramework;
    private Framework framework;

    @DataBoundConstructor
    public Java(JavaFramework javaFramework, Framework framework){

        this.javaFramework = javaFramework;
        this.framework = framework;
    }

    public JavaFramework getJavaFramework() {
        return javaFramework;
    }

    public void setJavaFramework(JavaFramework javaFramework) {
        this.javaFramework = javaFramework;
    }

    public Framework getFramework() {
        return framework;
    }

    public void setFramework(Framework framework) {
        this.framework = framework;
    }
}

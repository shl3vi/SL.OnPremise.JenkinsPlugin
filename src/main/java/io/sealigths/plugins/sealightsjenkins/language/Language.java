package io.sealigths.plugins.sealightsjenkins.language;

/**
 * Created by shahar on 4/25/2016.
 */
public enum Language {
    JAVA() {
        @Override public String getDisplayName() {
            return "Java";
        }
    },
    NODEJS() {
        @Override public String getDisplayName() {
            return "NodeJs";
        }
    },
    RUBY() {
        @Override public String getDisplayName() {
            return "Ruby";
        }
    };
    public abstract String getDisplayName();
}

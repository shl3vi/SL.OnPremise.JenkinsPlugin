package io.sealights.plugins.sealightsjenkins.utils;

/*
 *
 * This class help to enforce filtering value
 * by its include and exclude patterns.
 *
 * */
public class IncludeExcludeFilter {

    WildCardPattern include = null;
    WildCardPattern exclude = null;

    public IncludeExcludeFilter(String include, String exclude) {
        if (!StringUtils.isNullOrEmpty(include))
            this.include = new WildCardPattern(include);

        if (!StringUtils.isNullOrEmpty(exclude))
            this.exclude = new WildCardPattern(exclude);
    }

    public boolean filter(String value){

        if (StringUtils.isNullOrEmpty(value)){
            return false;
        }
        String modifiedValue = value.replace('/','.');
        if (isExcluded(modifiedValue)){
            return false;
        }

        if (isIncluded(modifiedValue)){
            return true;
        }

        return false;
    }

    private boolean isIncluded(String value){
        if (include != null)
            return include.matches(value);
        return true;
    }

    private boolean isExcluded(String value){
        if (exclude != null)
            return exclude.matches(value);
        return false;
    }


    @Override
    public String toString() {
        String i ="<include is null>";
        if (include != null)
            i = include.getInitialExpression();

        String e ="<exclude is null>";
        if (exclude != null)
            e = exclude.getInitialExpression();

        return "IncludeExcludeFilter{" +
                "include=" + i +
                ", exclude=" + e +
                '}';
    }
}

package io.sealigths.plugins.sealightsjenkins.utils;

import java.util.regex.Pattern;

public class WildcardPattern {

    private String initialExpression;
    private Pattern compiledPattern;

    public WildcardPattern(String initialExpression) {
        this.initialExpression = initialExpression;
    }

    private Pattern createCompiledPattern(){
        if (initialExpression == null)
            initialExpression = "";

        final String[] expressions = initialExpression.split("\\,");
        final StringBuilder finalRegex = new StringBuilder(initialExpression.length() * 2);
        for (int i = 0; i < expressions.length; i++) {
            String currentExpression = expressions[i];
            currentExpression = currentExpression.trim();
            if (i > 0)
            {
                finalRegex.append('|');
            }
            String encodedExpression = encodeExperssion(currentExpression);
            finalRegex.append('(').append(encodedExpression).append(')');
        }
        Pattern p = Pattern.compile(finalRegex.toString());
        return p;
    }

    public boolean matches(final String valueToMatch) {
        if (compiledPattern == null)
        {
            compiledPattern = createCompiledPattern();
        }
        return compiledPattern.matcher(valueToMatch).matches();
    }

    private static String encodeExperssion(final String expression) {
        final StringBuilder encodedExpression = new StringBuilder(expression.length() * 2);
        for (int i = 0; i < expression.length(); i++) {
            char currentChar = expression.charAt(i);
            switch (currentChar) {
                case '*':
                    encodedExpression.append(".*");
                    break;
                case '?':
                    encodedExpression.append(".");
                    break;
                default:
                    encodedExpression.append(Pattern.quote(String.valueOf(currentChar)));
                    break;
            }
        }
        return encodedExpression.toString();
    }

    public String getInitialExpression(){
        return initialExpression;
    }
}


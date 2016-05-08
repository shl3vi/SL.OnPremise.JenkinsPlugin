package io.sealigths.plugins.sealightsjenkins.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shahar on 5/8/2016.
 */
public class CommandLineHelper {

    static int IGNORE_WHITE_SPACES = 0;
    static int  IN_QUOTES = 1;
    static int AFTER_QUOTES = 2;
    static int IN_SEQUENCE = 3;
    static int ESCAPED = 4;

    public static List<String> toArgsArray(String argsAsString) {
        List<String> args = new ArrayList();
        int state = 0;
        StringBuilder currentArg = new StringBuilder();

        for (char c : argsAsString.toCharArray()) {
            if (state == IGNORE_WHITE_SPACES) {
                if (!Character.isWhitespace(c)) {
                    currentArg.append(c);
                    if (c == '"') {
                        state = IN_QUOTES;
                    } else {
                        state = IN_SEQUENCE;
                    }
                }
            }

            else if (state == IN_QUOTES) {
                currentArg.append(c);
                if (c == '"') {
                    state = AFTER_QUOTES;
//                }else if (c == '\\'){
//                    state = ESCAPED;
                }
            }

//            else if (state == ESCAPED){
//                if (c == '"' || c == '\\') {
//                    currentArg.setCharAt(currentArg.length() - 1, c);
//                }else {
//                    currentArg.append(c);
//                }
//                state = IN_QUOTES;
//            }

            else if (state == AFTER_QUOTES) {
                if (!Character.isWhitespace(c)) {
                    currentArg.append(c);
                    if (c == '"') {
                        state = IN_QUOTES;
                    } else {
                        state = IN_SEQUENCE;
                    }
                } else {
                    args.add(currentArg.toString());
                    currentArg.setLength(0);
                    state = IGNORE_WHITE_SPACES;
                }
            }

            else if (state == IN_SEQUENCE){
                if (!Character.isWhitespace(c)) {
                    currentArg.append(c);
                    if (c == '"') {
                        state = IN_QUOTES;
                    }
                } else {
                    args.add(currentArg.toString());
                    currentArg.setLength(0);
                    state = IGNORE_WHITE_SPACES;
                }
            }
        }
        args.add(currentArg.toString());
        return args;
    }
}

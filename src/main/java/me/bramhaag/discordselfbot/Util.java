package me.bramhaag.discordselfbot;

import org.apache.commons.lang3.StringUtils;

public class Util {

    public static String combineArgs(String[] args) {
        return StringUtils.join(args, ' ');
    }
}

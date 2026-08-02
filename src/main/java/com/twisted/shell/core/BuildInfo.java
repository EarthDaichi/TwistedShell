package com.twisted.shell.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class BuildInfo {

    private static final Properties properties = new Properties();

    static {

        try (InputStream in =
                     BuildInfo.class.getResourceAsStream(
                             "/META-INF/build.properties")) {

            if (in != null) {
                properties.load(in);
            }

        } catch (IOException e) {

            throw new ExceptionInInitializerError(e);

        }

    }

    private BuildInfo() {}

    public static String name() {
        return properties.getProperty("name");
    }

    public static String version() {
        return properties.getProperty("version");
    }

    public static String group() {
        return properties.getProperty("group");
    }

    public static String javaVersion() {
        return properties.getProperty("java");
    }

}
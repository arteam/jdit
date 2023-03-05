package com.github.arteam.jdit;

import org.jdbi.v3.core.Handle;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Component which is responsible for migrating data in the DB
 */
class DataMigration {

    private static final int BUFFER_SIZE = 1024;
    private final Handle handle;

    DataMigration(Handle handle) {
        this.handle = handle;
    }

    /**
     * Executes a script a from a classpath location
     *
     * @param scriptLocation script location (without the leading slash). If one exists, it's trimmed.
     */
    void executeScript(String scriptLocation) {
        String correctLocation = !scriptLocation.startsWith("/") ?
                scriptLocation : scriptLocation.substring(1);
        URL resource = Thread.currentThread().getContextClassLoader().getResource(correctLocation);
        if (resource == null) {
            throw new IllegalArgumentException("Unable to load resource at: " + scriptLocation);
        }
        try (InputStream is = resource.openStream()) {
            handle.createScript(readStream(is)).executeAsSeparateStatements();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read a stream", e);
        }
    }

    private static String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder(BUFFER_SIZE);
        InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
        char[] buffer = new char[BUFFER_SIZE];
        int readBytes;
        while ((readBytes = reader.read(buffer)) > -1) {
            sb.append(buffer, 0, readBytes);
        }
        return sb.toString();
    }
}

package com.jmpeax.femto.toml;


import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Toml {

    private final ConcurrentHashMap<String, Object> data;

    public Toml() {
        this.data = new ConcurrentHashMap<>();
    }

    public boolean load(String toml) {
        if (Objects.isNull(toml)) {
            return false;
        }
        var tomlPath = Path.of(toml);
        if (Files.notExists(tomlPath)) {
            return false;
        }
        String currentSection = null;
        try (BufferedReader reader = Files.newBufferedReader(tomlPath)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                if (line.startsWith("[")) {
                    currentSection  = line.substring(1, line.length() - 1);
                    data.put(currentSection, new ConcurrentHashMap<>());
                } else {

                    var keyValue = line.split("=");
                    var key = keyValue[0].trim();
                    var value = keyValue[1].trim();
                    if (currentSection == null) {
                        data.put(key, value);
                    }else {
                        ((ConcurrentHashMap<String, Object>) data.get(currentSection)).put(key, value);
                    }

                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Toml.class.getName()).severe(ex.getMessage());
            return false;
        }
        return true;
    }
}

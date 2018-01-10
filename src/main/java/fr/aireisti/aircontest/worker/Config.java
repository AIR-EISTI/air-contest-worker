package fr.aireisti.aircontest.worker;

import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {
    private static Ini config;
    public static Ini getConfig() {
        if (config == null) {
            config = new Ini();
            try {
                config.load(new File("config.ini"));
            } catch (IOException e) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, "Failed to load config", e);
            }
        }
        return config;
    }

    public static String get(String key) {
        String[] path = key.split("\\.");
        if (path.length <= 1)
            return null;

        Ini.Section tmpSection = getConfig().get(path[0]);

        return tmpSection.get(path[1]);
    }

    public static String get(String key, String alt) {
        String value = get(key);
        if (value == null) {
            return alt;
        }
        return value;
    }
}

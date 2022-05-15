package com.redfrog.api;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;

public class ConfigFile {

    private final File file;
    private final YamlConfiguration config;


    public ConfigFile(RedFrogPlugin plugin, String name) {
        file = new File(plugin.getDataFolder(), name);
        config = new YamlConfiguration();

        if (!file.exists()) {
            InputStream resource = plugin.getResource(name);

            if (resource != null)
                plugin.saveResource(name, false);
        }
    }


    public YamlConfiguration getConfig() {
        return config;
    }


    /**
     * Opens and loads the config file. It will create one if it does not exist.
     */
    public void open() {
        if (!file.exists()) {
            file.getParentFile().mkdir();

            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Saves the config changes into the config file.
     */
    public void apply() {
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

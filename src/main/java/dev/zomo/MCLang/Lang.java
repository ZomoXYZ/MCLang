package dev.zomo.MCLang;

import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Lang {

    private Plugin plugin = null;
    private String lang = "";
    private FileConfiguration langConfig = null;
    private FileConfiguration langConfigBackup = null;

    /**
     * Constructor for the Language support
     *
     * @author Ashley Zomo
     * @version 1.0.1
     * @since 2020-12-03
     * @param setPlugin an instance of org.bukkit.plugin.java.JavaPlugin
     * @param setLanguage code for the language (example: "en_us") 
     */
    public Lang(Plugin setPlugin, String setLanguage) {
        plugin = setPlugin;
        lang = setLanguage;
        langConfigBackup = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("lang/" + lang + ".yml")));
        loadFile();
    }

    /**
     * Load and process the language file
     *
     * @author Ashley Zomo
     * @version 1.0.1
     * @since 2020-12-03
     */
    private void loadFile() {

        File file = new File(plugin.getDataFolder(), "lang/" + lang + ".yml");

        boolean fileResolved = false;

        if (!file.exists()) {
            try {
                plugin.saveResource("lang/" + lang + ".yml", true);
                file = new File(plugin.getDataFolder(), "lang/" + lang + ".yml");
                fileResolved = true;
            } catch (IllegalArgumentException err) {
                plugin.getLogger().info("Language " + lang + " is not available, defaulting to en_us");
                lang = "en_us";
                loadFile();
            }
        } else {
            fileResolved = true;
        }

        if (fileResolved) {
            langConfig = YamlConfiguration.loadConfiguration(file);

            if (langConfig.getString("version") != null && !langConfig.getString("version").equals(plugin.getDescription().getVersion())) {
                String logMessage = "Language file for " + lang + " is out of date and will be updated ";
                logMessage+= "(current: " + langConfig.getString("version") + ", ";
                logMessage+= "required: " + plugin.getDescription().getVersion() + ")";
                plugin.getLogger().info(logMessage);

                langConfig = null;
                file.delete();
                loadFile();
            }
        }

    }

    /**
     * Change the language
     *
     * @author Ashley Zomo
     * @version 1.0.0
     * @since 2020-12-03
     * @param setLanguage code for the language (example: "en_us")
     */
    public void setLang(String setLanguage) {
        lang = setLanguage;
        loadFile();
    }

    /**
     * Reload and reprocess the language file
     *
     * @author Ashley Zomo
     * @version 1.0.0
     * @since 2020-12-03
     */
    public void reloadFile() {
        loadFile();
    }

    /**
     * Load the template from the file
     *
     * @author Ashley Zomo
     * @version 1.0.0
     * @since 2020-12-03
     * @param id       ID of the string from the language file (example:
     *                 "afk.onAfk")
     * @param template an instance of LangTemplate
     */
    public String string(String id, LangTemplate template){
        return template.apply(string(id));
    }

    /**
     * Load the template from the file
     *
     * @author Ashley Zomo
     * @version 1.0.1
     * @since 2020-12-03
     * @param id ID of the string from the language file (example: "afk.onAfk")
     */
    public String string(String id) {

        String value = "";

        List<String> values = null;

        if (langConfig != null) {
            values = langConfig.getStringList(id);
            
            if (values.size() == 0)
                value = langConfig.getString(id);
        }

        if ((values == null || values.size() == 0) && value.length() == 0) {
            values = langConfigBackup.getStringList(id);

            if (values.size() == 0)
                value = langConfigBackup.getString(id);
        }

        if (values != null && values.size() > 0) {
            int index = (int) Math.round(Math.random() * (values.size() - 1));
            value = values.get(index);
        }

        return value;
    }

}
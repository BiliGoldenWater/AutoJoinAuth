package indi.goldenwater;

import indi.goldenwater.listeners.OnBotOnlineEvent;
import indi.goldenwater.listeners.OnBotReloginEvent;
import indi.goldenwater.listeners.OnGroupMessageEvent;
import indi.goldenwater.listeners.OnMemberJoinRequestEvent;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class AutoJoinAuth extends JavaPlugin {
    public static final AutoJoinAuth INSTANCE = new AutoJoinAuth();
    private final Map<Long, List<Object>> passedUsers = new HashMap<>();
    private final Properties prop = new Properties();
    private InputStream inputStream;
    private File configFile;
    private Bot botInstance;

    private AutoJoinAuth() {
        super(new JvmPluginDescriptionBuilder("indi.goldenwater.AutoJoinAuth", "1.0.1")
                .name("AutoJoinAuth")
                .author("Golden_Water")
                .build());
    }

    @Override
    public void onEnable() {
        configFile = new File(getConfigFolder(), "config.properties");
        try {
            if (!configFile.exists()) {
                if (configFile.createNewFile()) {
                    new FileOutputStream(configFile).write("audit_group=0\ntarget_group=0\nauto_reject=1".getBytes());
                }
            }
            inputStream = new FileInputStream(configFile);
            prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        OnBotOnlineEvent.register();
        OnBotReloginEvent.register();
        OnGroupMessageEvent.register();
        OnMemberJoinRequestEvent.register();

        getLogger().info("Plugin loaded!");
    }

    @Override
    public void onDisable() {
        if (inputStream != null) {
            try {
                inputStream.close();
                saveConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        OnBotOnlineEvent.unregister();
        OnBotReloginEvent.unregister();
        OnGroupMessageEvent.unregister();
        OnMemberJoinRequestEvent.unregister();
        getLogger().info("Plugin unloaded!");
    }

    public void saveConfig() {
        try (OutputStream outputStream = new FileOutputStream(configFile)) {
            prop.store(outputStream, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bot getBotInstance() {
        return botInstance;
    }

    public void setBotInstance(Bot botInstance) {
        this.botInstance = botInstance;
    }

    public Properties getProp() {
        return prop;
    }

    public Map<Long, List<Object>> getPassedUsers() {
        return passedUsers;
    }
}
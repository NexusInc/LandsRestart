package ru.landsproject.landsrestart;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.landsproject.api.command.controller.CommandController;
import ru.landsproject.api.configuration.Configuration;
import ru.landsproject.api.configuration.Type;
import ru.landsproject.api.util.interfaces.Initable;
import ru.landsproject.api.util.scheduler.SchedulerTask;
import ru.landsproject.landsrestart.command.RestartCommand;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.concurrent.*;

@Getter
public final class LandsRestart extends JavaPlugin implements Initable {

    @Getter
    private static LandsRestart instance;
    private Configuration configuration;
    private CommandController commandController;
    private SchedulerTask scheduledPool;
    @Override
    public void onEnable() {
        instance = this;
        init();
    }

    @Override
    public void onDisable() {
        destruct();
    }


    @Override
    public void init() {
        configuration = new Configuration("config.yml", getDataFolder(), Type.YAML);
        commandController = new CommandController();
        scheduledPool = new SchedulerTask();

        configuration.init();
        commandController.init();
        scheduledPool.init();

        configuration.useDefaultColorful();

        commandController.registerCommands(this, new RestartCommand());
    }

    @Override
    public void destruct() {
        configuration.destruct();
        commandController.destruct();
        scheduledPool.init();
    }

}

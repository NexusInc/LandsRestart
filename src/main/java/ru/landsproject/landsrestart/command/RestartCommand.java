package ru.landsproject.landsrestart.command;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.landsproject.api.command.BasicCommand;
import ru.landsproject.api.configuration.Messager;
import ru.landsproject.api.util.interfaces.Command;
import ru.landsproject.landsrestart.LandsRestart;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.bukkit.Sound.valueOf;

@Command(name = "restartdelay", aliases = {"rd", "restartd", "rdelay"})
public class RestartCommand extends BasicCommand {
    @Override
    public List<String> tabPressComplete(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        return null;
    }

    @Override
    public void run(CommandSender sender, String s, String[] args) {
//        Player player = (Player) sender;
        if(!sender.isOp() && sender.hasPermission("*") && sender.hasPermission("landsrestart.restart")) return;
        if(args.length != 1) {
            sender.sendMessage(LandsRestart.getInstance().getConfiguration().getString("messages.use"));
            return;
        }
        String rawArg = args[0];
        int cooldown = -1;
        try {
            cooldown = Integer.parseInt(rawArg);
        } catch (NumberFormatException e) {
            sender.sendMessage(LandsRestart.getInstance().getConfiguration().getString("messages.use"));
            return;
        }
        if(cooldown <= 0) {
            sender.sendMessage(LandsRestart.getInstance().getConfiguration().getString("messages.use"));
            return;
        }
        Runnable reloadTask = (() -> {
            Bukkit.getScheduler().runTask(LandsRestart.getInstance(), ()->{
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), LandsRestart.getInstance().getConfiguration().getString("settings.command"));
            });
        });
        for (int i = 5; i >= 1; i--) {
            if((cooldown - i) <= 0) continue;
            int finalI = i;
            LandsRestart.getInstance().getScheduledPool().scheduleTask(() -> {
                Bukkit.broadcastMessage(LandsRestart.getInstance().getConfiguration().getString("messages.later").replace("<duration>", String.valueOf(Messager.getRuTimeForm(finalI))));
                Bukkit.getOnlinePlayers().forEach(p -> {
                    p.playSound(p.getLocation(), Sound.valueOf(LandsRestart.getInstance().getConfiguration().getString("settings.sounds.later")), 1, 1);
                });
            }, cooldown - i, TimeUnit.SECONDS);
        }
        if(cooldown > 10) {
            LandsRestart.getInstance().getScheduledPool().scheduleTask(() -> {
                Bukkit.broadcastMessage(LandsRestart.getInstance().getConfiguration().getString("messages.later").replace("<duration>", String.valueOf(Messager.getRuTimeForm(10))));
            }, cooldown - 10, TimeUnit.SECONDS);
        }
        LandsRestart.getInstance().getScheduledPool().scheduleTask(reloadTask, cooldown, TimeUnit.SECONDS);
        sender.sendMessage(LandsRestart.getInstance().getConfiguration().getString("messages.scheduled").replace("<duration>", String.valueOf(Messager.getRuTimeForm(cooldown))));
    }
}

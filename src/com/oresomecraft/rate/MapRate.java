package com.oresomecraft.rate;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MapRate extends JavaPlugin {

    private static MapRate plugin;
    public YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/MapRate/config.yml"));

    /*Gives full access to admin commands regardless of permission(s). Given only to trusted people who won't get their dicks
    sucked from exempted players to get an unexemption.*/
    public String[] FULL_ACCESS = {"__r3", "anomalousrei", "psgs", "noblefable", "zachoz", "_husky_", "scruffyrules", "danshrdr"};

    public static MapRate getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {
        createConfig();

        registerCommands();

        plugin = this;

        new BukkitRunnable() {
            public void run() {
                try {
                    config.save(new File("plugins/MapRate/config.yml"));
                } catch (IOException e) {
                    System.out.println("[MapRate] Error saving config: " + e.getMessage());
                }
            }
        }.runTaskTimer(this, 0L, 30 * 20L);
    }

    private void createConfig() {
        boolean exists = new File("plugins/MapRate/config.yml").exists();
        if (!exists) {
            if (new File("plugins/MapRate").mkdir()) {
                config.options().header("MapRate, by AnomalousRei.");
                try {
                    config.save("plugins/MapRate/config.yml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * *******************************************************************
     * Code to use for sk89q's command framework goes below this comment! *
     * ********************************************************************
     */

    private CommandsManager<CommandSender> commands;

    private void registerCommands() {
        final MapRate plugin = this;
        // Register the commands that we want to use
        commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender player, String perm) {
                return plugin.hasPermission(player, perm);
            }
        };
        commands.setInjector(new SimpleInjector(this));
        final CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, commands);

        cmdRegister.register(Commands.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatColor.RED + "Number expected, string received instead.");
            } else {
                sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
                sender.sendMessage(ChatColor.RED + "Message: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }

    public boolean hasPermission(CommandSender sender, String perm) {
        if (!(sender instanceof Player))
            if (sender.hasPermission(perm))
                return ((sender.isOp() && (sender instanceof ConsoleCommandSender)));
        return (sender.isOp() || sender instanceof ConsoleCommandSender ||
                sender.hasPermission(perm) || Arrays.asList(FULL_ACCESS).contains(sender.getName().toLowerCase()));
    }

}
package com.oresomecraft.rate;

import com.oresomecraft.OresomeBattles.api.*;
import com.oresomecraft.maps.MapsPluginAPI;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class Commands {
    MapRate plugin;

    public Commands(MapRate pl) {
        plugin = pl;
    }

    @Command(aliases = {"exemption", "norate"},
            desc = "Rating exemption utility command",
            usage = "<player>",
            min = 1,
            max = 1,
            flags = "suo")
    @CommandPermissions({"maprate.staff"})
    public void exemption(CommandContext args, CommandSender sender) {
        if (args.argsLength() == 0 && !(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console cannot check his/her exemption status!");
            return;
        }
        Player p;
        if (!args.hasFlag('o') && Bukkit.getPlayer(args.getString(0)) == null && args.argsLength() == 1) {
            sender.sendMessage(ChatColor.RED + "Error: That player was not found!");
            sender.sendMessage(ChatColor.RED + "Use the -o flag if you want to use an offline player!");
            return;
        } else {
            p = Bukkit.getPlayer(args.getString(0));
        }
        if (args.hasFlag('s')) {
            Utility.addExempt(args.getString(0));
            sender.sendMessage(ChatColor.RED + "Player " + ChatColor.GREEN + args.getString(0) + ChatColor.RED + " is now exempt from rating!");
        } else if (args.hasFlag('u')) {
            Utility.removeExempt(args.getString(0));
            sender.sendMessage(ChatColor.RED + "Player " + ChatColor.GREEN + args.getString(0) + ChatColor.RED + " is now allowed to rate!");
        } else if (p != null) {
            sender.sendMessage(ChatColor.BOLD + "[MapRate] " + ChatColor.RESET + ChatColor.RED +
                    "Exemption status for " + p.getDisplayName());
            sender.sendMessage(ChatColor.GREEN + "Is exempt: " + Utility.isExempt(p.getName()));
            sender.sendMessage(ChatColor.GREEN + "Has admin rights: " + Arrays.asList(MapRate.getInstance().FULL_ACCESS).contains(p.getName()));
            sender.sendMessage(ChatColor.RED + "Use the -s flag with this command to exempt a player from rating!");
            sender.sendMessage(ChatColor.RED + "Use the -u flag with this command to unexempt a player from rating!");
        } else {
            sender.sendMessage(ChatColor.BOLD + "[MapRate] " + ChatColor.RESET + ChatColor.RED +
                    "Exemption status for " + args.getString(0));
            sender.sendMessage(ChatColor.GREEN + "Is exempt: " + Utility.isExempt(args.getString(0)));
            sender.sendMessage(ChatColor.GREEN + "Has admin rights: " + Arrays.asList(MapRate.getInstance().FULL_ACCESS).contains(args.getString(0)));
            sender.sendMessage(ChatColor.RED + "Use the -s flag with this command to exempt a player from rating!");
            sender.sendMessage(ChatColor.RED + "Use the -u flag with this command to unexempt a player from rating!");
        }
    }

    @Command(aliases = {"rate", "ratemap"},
            desc = "Rate for the current map playnig",
            usage = "<1-5>",
            min = 1,
            max = 1)
    public void rate(CommandContext args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console cannot view the awesome book!");
            return;
        }
        if (BattlesAccess.getArena().equalsIgnoreCase("none")) {
            sender.sendMessage(ChatColor.RED + "You cannot rate a map if there isn't one playing!");
            return;
        }
        int selection = Integer.parseInt(args.getString(0));
        if (selection < 1 || selection > 5) {
            sender.sendMessage(ChatColor.RED + "Error: Your rating must be between 1 and 5!");
            return;
        }
        Utility.setRating(sender.getName(), selection);
        sender.sendMessage(ChatColor.GREEN + "Your rating of " + Utility.convertRating(args.getString(0)) +
                ChatColor.GREEN + " for " + MapsPluginAPI.resolveShortName(BattlesAccess.getArena()) + " was added!");
    }

    @Command(aliases = {"viewratings", "rview"},
            desc = "Views the current map\'s ratings",
            min = 0,
            max = 0)
    public void book(CommandContext args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console cannot view the awesome book!");
            return;
        }
        if (BattlesAccess.getArena().equalsIgnoreCase("none")) {
            sender.sendMessage(ChatColor.RED + "You cannot view a map's ratings if there is no battle playing!");
            return;
        }
        if (((Player) sender).getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "You must be in the lobby or spectating to view a map's ratings!");
            return;
        }
        Player p = (Player) sender;
        p.getInventory().addItem(Utility.returnBook(BattlesAccess.getArena()));
    }
}
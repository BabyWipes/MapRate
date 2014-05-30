package com.oresomecraft.rate;

import com.oresomecraft.OresomeBattles.api.BattlesAccess;
import com.oresomecraft.maps.MapsPluginAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utility {

    /**
     * Prevents a player from rating maps.
     *
     * @param p The player to exempt.
     */
    public static void addExempt(String p) {
        List<String> tmp1 = MapRate.getInstance().getConfig().getStringList("exempt");
        if (!tmp1.contains(p)) {
            tmp1.add(p);
            MapRate.getInstance().getConfig().set("exempt", tmp1);
        }
    }

    /**
     * Unexempts a player from rating maps.
     *
     * @param p The player to unexempt.
     */
    public static void removeExempt(String p) {
        List<String> tmp1 = MapRate.getInstance().getConfig().getStringList("exempt");
        tmp1.remove(p);
        MapRate.getInstance().getConfig().set("exempt", tmp1);
    }

    /**
     * Checks whether the player is exempt from rating maps or not.
     *
     * @param p The player to check.
     * @return Whether the player is exempt.
     */
    public static boolean isExempt(String p) {
        return !Arrays.asList(MapRate.getInstance().FULL_ACCESS).contains(p.toLowerCase()) && MapRate.getInstance().getConfig().getStringList("exempt").contains(p);
    }

    /**
     * Adds a rating to a map.
     *
     * @param player The player who rated.
     * @param rating The rating.
     */
    public static void setRating(String player, int rating) {
        MapRate.getInstance().getConfig().set(BattlesAccess.getArena(), player + ";" + rating);
    }

    /**
     * Gets ratings from a map.
     */
    public static List<String> getRatings(String map) {
        return MapRate.getInstance().getConfig().getStringList(map);
    }

    /**
     * Returns a custom string that joins the rating and player's name together and returns it as 2 strings.
     *
     * @param string The string to convert.
     * @return The converted String[].
     */
    public static String[] convertRatingAndGiver(String string) {
        return string.split(";");
    }

    /**
     * Returns a book full of ratings.
     *
     * @param map The map to check.
     * @return The rating book.
     */
    public static ItemStack returnBook(String map) {
        List<String> ratings = getRatings(map);
        ItemStack i = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bm = (BookMeta) i.getItemMeta();
        bm.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Ratings for " + MapsPluginAPI.resolveShortName(map));
        bm.setAuthor(ChatColor.BLUE + "OresomeCraft");
        String builder = "";
        ArrayList<String> pages = new ArrayList<String>();
        int iterate = 0;
        for (String rate : ratings) {
            if (iterate == 12) {
                iterate = 0;
                pages.add(builder);
                builder = "";
            }
            String[] convert = convertRatingAndGiver(rate);
            if (iterate == 0) {
                builder = convert[0] + ChatColor.BLACK + ": " + convertRating(convert[1]) + ChatColor.RESET;
            } else {
                builder = builder + "\n" + ChatColor.BLACK + convert[0] + ": " + convertRating(convert[1]) + ChatColor.RESET;
            }
            iterate++;
        }
        if (!builder.equals("")) {
            pages.add(builder);
        }
        for (String string : pages) {
            bm.addPage(string);
        }
        i.setItemMeta(bm);
        return i;
    }

    /**
     * Returns a string converted from an integer with a max of 5.
     *
     * @param rate The rating to be converted.
     * @return The converted rating.
     */
    public static String convertRating(String rate) {
        int rating = Integer.parseInt(rate);
        if (rating > 5 || rating < 1) return ChatColor.YELLOW + "***";
        if (rating == 5) return ChatColor.DARK_GREEN + "*****";
        if (rating == 4) return ChatColor.GREEN + "****";
        if (rating == 3) return ChatColor.YELLOW + "***";
        if (rating == 2) return ChatColor.RED + "**";
        if (rating == 1) return ChatColor.DARK_RED + "*";
        return ChatColor.YELLOW + "***";
    }

}
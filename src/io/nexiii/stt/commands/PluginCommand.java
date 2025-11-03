package io.nexiii.stt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.nexiii.stt.SimpleTabTPS;

public class PluginCommand implements CommandExecutor {

    private static final String GITHUB_LINK = "https://github.com/Nexiii/SimpleTabTPS";

    private final SimpleTabTPS plugin;

    public PluginCommand(SimpleTabTPS plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage("§8§m----- §d§l SimpleTabTPS §7by §d§lNexiii §r§8§m------------");
            sender.sendMessage("");
            sender.sendMessage("§7Displays TPS, RAM and Ping in the tab list.");
            sender.sendMessage("");
            if (plugin.getConfig().getBoolean("sourcecode-message", true)) {
            	sender.sendMessage("§7Source Code:");
                sender.sendMessage("§d§n" + GITHUB_LINK);
                sender.sendMessage("");
            }
            sender.sendMessage("§8§m----------------------------------------");
        } else {
            sender.sendMessage("§7Unknown subcommand: §c" + args[0]);
            sender.sendMessage("§7Usage: §a/stt");
        }

        return true;
    }
}
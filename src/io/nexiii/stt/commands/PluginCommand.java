package io.nexiii.stt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PluginCommand implements CommandExecutor {

    private static final String GITHUB_LINK = "https://github.com/Nexiii/SimpleTabTPS";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        switch (args.length) {
            case 0 -> sender.sendMessage("§bSimpleTabTPS Source Code: §f" + GITHUB_LINK);
            default -> {
                sender.sendMessage("§cUnbekannter Unterbefehl: §f" + args[0]);
                sender.sendMessage("§7Verwendung: §e/stt");
            }
        }

        return true;
    }
}
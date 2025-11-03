package io.nexiii.stt;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import io.nexiii.stt.commands.PluginCommand;

public class SimpleTabTPS extends JavaPlugin {

    private static SimpleTabTPS instance;
    private long lastCheck = System.currentTimeMillis();
    private double estimatedTps = 20.0;
    private long lastWarning = 0;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.getCommand("stt").setExecutor(new PluginCommand(this));

        String enabledMsg = getConfig().getString("enabled-message", "§a[SimpleTabTPS] Plugin enabled!");
        getServer().getConsoleSender().sendMessage(enabledMsg);

        startTabUpdater();
    }

    @Override
    public void onDisable() {
        String disabledMsg = getConfig().getString("disabled-message", "§c[SimpleTabTPS] Plugin disabled!");
        getServer().getConsoleSender().sendMessage(disabledMsg);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setPlayerListFooter("");
        }
    }

    private void startTabUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                double currentTps = getServerTps();
                long usedMb = getUsedRamMb();
                long maxMb = getMaxRamMb();
                double ramPercent = ((double) usedMb / maxMb) * 100.0;

                for (Player p : Bukkit.getOnlinePlayers()) {
                    int ping = getPlayerPing(p);

                    String pingColor = getColorForPing(ping);
                    String ramColor = getColorForRam(ramPercent);
                    String tpsColor = getColorForTps(currentTps);

                    String header = "§7RAM: " + ramColor + usedMb + " MB §7/ §7§l" + maxMb + " MB";
                    String footer = "§7Ping: " + pingColor + ping + " ms §r§7| TPS: " + tpsColor + String.format("%.2f", currentTps);

                    p.setPlayerListHeader(header);
                    p.setPlayerListFooter(footer);
                }

                if (getConfig().getBoolean("performance-warnings", true)) {
                    sendPerformanceWarnings(currentTps, ramPercent);
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    private void sendPerformanceWarnings(double tps, double ramPercent) {
        long now = System.currentTimeMillis();
        if (now - lastWarning > 30_000) {
            if (tps < 18.0 || ramPercent > 85.0) {
                for (Player op : Bukkit.getOnlinePlayers()) {
                    if (op.isOp()) {
                        op.sendMessage("§c⚠ Server Performance Warning ⚠");
                        if (tps < 18.0) {
                            op.sendMessage("§7Low TPS detected: " + getColorForTps(tps) + String.format("%.2f", tps));
                        }
                        if (ramPercent > 85.0) {
                            op.sendMessage("§7High RAM usage: " + getColorForRam(ramPercent) + String.format("%.0f", ramPercent) + "%");
                        }
                    }
                }
                lastWarning = now;
            }
        }
    }

    private long getUsedRamMb() {
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
    }

    private long getMaxRamMb() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.maxMemory() / (1024 * 1024);
    }

    private double getServerTps() {
        try {
            Method method = Bukkit.getServer().getClass().getMethod("getTPS");
            double[] tps = (double[]) method.invoke(Bukkit.getServer());
            return Math.min(20.0, tps[0]);
        } catch (Exception e) {
            long now = System.currentTimeMillis();
            long diff = now - lastCheck;
            lastCheck = now;

            double msPerTick = diff / 20.0;
            estimatedTps = Math.min(20.0, 1000.0 / msPerTick);
            return estimatedTps;
        }
    }

    private int getPlayerPing(Player player) {
        try {
            return player.getPing();
        } catch (Exception e) {
            return 0;
        }
    }

    private String getColorForPing(int ping) {
        if (ping < 80) return "§a§l";
        if (ping < 150) return "§e§l";
        return "§c§l";
    }

    private String getColorForRam(double percent) {
        if (percent < 60) return "§a§l";
        if (percent < 85) return "§e§l";
        return "§c§l";
    }

    private String getColorForTps(double tps) {
        if (tps >= 19.5) return "§a§l";
        if (tps >= 18.0) return "§e§l";
        return "§c§l";
    }

    public static SimpleTabTPS getInstance() {
        return instance;
    }
}

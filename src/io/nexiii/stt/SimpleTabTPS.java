package io.nexiii.stt;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SimpleTabTPS extends JavaPlugin{

	private static SimpleTabTPS instance;
	private long lastCheck = System.currentTimeMillis();
    private double estimatedTps = 20.0;

	@Override
	public void onEnable() {

        saveDefaultConfig();

        String enabledMsg = getConfig().getString("enabled-message", "");
        getServer().getConsoleSender().sendMessage(enabledMsg);
        startTabUpdater();
		
	}
	
	@Override
	public void onDisable() {

		String disabledMsg = getConfig().getString("disabled-message", "");
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
                String ramInfo = getRamUsage();

                for (Player p : Bukkit.getOnlinePlayers()) {
                    int ping = getPlayerPing(p);


                    String header = "§7RAM: §l§a" + ramInfo;

                    String footer = "§7Ping: §l§a" + ping + " ms §r§7| TPS: §l§a" + String.format("%.2f", currentTps);

                    p.setPlayerListHeader(header);
                    p.setPlayerListFooter(footer);
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    private String getRamUsage() {
        Runtime runtime = Runtime.getRuntime();
        long max = runtime.maxMemory() / (1024 * 1024);
        long used = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        return used + " MB §7/ " + max + " MB";
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

	public static SimpleTabTPS getInstance() {
		return instance;
	}
	
}

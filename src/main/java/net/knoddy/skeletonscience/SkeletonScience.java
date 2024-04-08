package net.knoddy.skeletonscience;

import org.bukkit.plugin.java.JavaPlugin;

public class SkeletonScience extends JavaPlugin {
    @Override
    public void onEnable() {
        CommandKit commandKit = new CommandKit();
        this.getCommand("beginsimulation").setExecutor(commandKit);
        this.getCommand("stopsimulation").setExecutor(commandKit);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);

        getLogger().info("SkeletonScience is enabled");
    }
}

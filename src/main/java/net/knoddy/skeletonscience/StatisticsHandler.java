package net.knoddy.skeletonscience;

import org.bukkit.Bukkit;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public class StatisticsHandler {
    public static CsvWriter csvWriter;

    // Trial-independent variables
    public static int n_trials = 20;
    public static int kb_level = 7;
    public static int zombies_per_sim = 8;
    public static int trials = 0;
    public static final double plane_y = -60;
    public static boolean experimental = false;
    public static boolean killswitch = false;

    // Trial-time variables
    public static Instant start_time;
    public static ArrayList<UUID> zombies = new ArrayList<>();
    public static UUID subject_id;
}

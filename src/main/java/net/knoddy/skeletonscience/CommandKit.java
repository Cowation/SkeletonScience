package net.knoddy.skeletonscience;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Instant;
import java.util.Random;

public class CommandKit implements CommandExecutor {
    private static final Random random = new Random();
    public static World world = Bukkit.getWorld("world");

    private static Zombie summonSubject(int kb_level) {
        Location loc = new Location(world, 0, StatisticsHandler.plane_y, 0);
        Zombie zombie = (Zombie) world.spawnEntity(loc, EntityType.ZOMBIE);

        zombie.setPersistent(true);
        zombie.setCustomName("Subject");
        zombie.setRemoveWhenFarAway(false);
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION, 0, false, false, false));
//        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 0, false, false, false));

        if (!zombie.isAdult()) {
            zombie.setAdult();
        }

        ItemStack sword = new ItemStack(Material.SLIME_BALL);
        if (kb_level > 0) {
            sword.addUnsafeEnchantment(Enchantment.KNOCKBACK, kb_level);
        }
        zombie.getEquipment().setItemInMainHand(sword);
        zombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        zombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        zombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        zombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));

        return zombie;
    }

    private static Location generateRandomLocation(double radius) {
        double angle = 2 * Math.PI * random.nextDouble();
        double r = radius * Math.sqrt(random.nextDouble());

        double x = r * Math.cos(angle);
        double z = r * Math.sin(angle);

        return new Location(world, x, StatisticsHandler.plane_y, z);
    }

    private static Zombie summonZombie() {
        Location loc = generateRandomLocation(23);
        Zombie zombie = (Zombie) world.spawnEntity(loc, EntityType.ZOMBIE);

        zombie.setPersistent(true);
        zombie.setCustomName("Attacker");
        zombie.setRemoveWhenFarAway(false);
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, PotionEffect.INFINITE_DURATION, 255, false, false, false));
//        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, PotionEffect.INFINITE_DURATION, 0, false, false, false));

        if (!zombie.isAdult()) {
            zombie.setAdult();
        }

        EntityEquipment equipment = zombie.getEquipment();
        if (equipment != null) {
            equipment.setItemInMainHand(null);
            equipment.setItemInOffHand(null);
            equipment.setHelmet(null);
            equipment.setChestplate(null);
            equipment.setLeggings(null);
            equipment.setBoots(null);
        }

        return zombie;
    }

    private static void clear_zombies() {
        for (Entity entity : world.getEntities()) {
            if (entity.getType() == EntityType.ZOMBIE) {
                entity.remove();
            }
        }
    }

    public static void execute_trial(int kb_level) {
        Bukkit.broadcastMessage("TRIAL STARTING: Running trial " + (StatisticsHandler.trials + 1) + " with Knockback " + kb_level);

        clear_zombies();
        Zombie subject = summonSubject(kb_level);

        // Assign subject ID and zombie ID for this experiment
        StatisticsHandler.subject_id = subject.getUniqueId();
        StatisticsHandler.zombies.clear();
        for (int i = 0; i < StatisticsHandler.zombies_per_sim; i++) {
            Zombie zombie = summonZombie();
            zombie.setTarget(subject);
            subject.setTarget(zombie);
            StatisticsHandler.zombies.add(zombie.getUniqueId());
        }

        StatisticsHandler.start_time = Instant.now();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (command.getName().equalsIgnoreCase("beginsimulation")) {
                if (args.length > 0) {
                    try {
                        StatisticsHandler.n_trials = Integer.parseInt(args[0]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("Please enter a valid number.");
                    }
                }

                if (args.length > 1) {
                    try {
                        StatisticsHandler.kb_level = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("Please enter a valid number.");
                    }
                }

                if (args.length > 2) {
                    try {
                        StatisticsHandler.zombies_per_sim = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("Please enter a valid number.");
                    }
                }

                if (args.length > 3) {
                    StatisticsHandler.incremental = Boolean.parseBoolean(args[3]);
                }

                StatisticsHandler.csvWriter = new CsvWriter(FileNameGenerator.generateTimestampedFileName("data", "csv"), new String[] {"Knockback Level", "Time Survived"});

                Bukkit.broadcastMessage("Beginning simulation with Knockback level " + StatisticsHandler.kb_level + ", " + StatisticsHandler.zombies_per_sim + " zombies and " + StatisticsHandler.n_trials + " trials...");
                StatisticsHandler.killswitch = false;
                StatisticsHandler.current_kb_level = 0;
                StatisticsHandler.trials = 0;

                if (StatisticsHandler.incremental) {
                    execute_trial(0);
                } else {
                    execute_trial(StatisticsHandler.kb_level);
                }

                return true;
            } else if (command.getName().equalsIgnoreCase("stopsimulation")) {
                Bukkit.broadcastMessage("Force stopping simulation...");
                StatisticsHandler.killswitch = true;

                return true;
            }
        }

        return false;
    }
}

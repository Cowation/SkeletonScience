package net.knoddy.skeletonscience;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;

public class DeathListener implements Listener {
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        boolean stop_trial = false;
        String stop_reason = "";

        if (e.getEntity() instanceof Zombie zombie) {
            if (StatisticsHandler.zombies.contains(zombie.getUniqueId())) {
                StatisticsHandler.zombies.remove(zombie.getUniqueId());

                if (StatisticsHandler.zombies.isEmpty()) {
                    stop_reason = "All zombies died";
                    stop_trial = true;
                }
            } else if (StatisticsHandler.subject_id.equals(zombie.getUniqueId())) {
                stop_reason = "Subject died";
                stop_trial = true;
            }
        }

        if (stop_trial && !StatisticsHandler.killswitch) {
            StatisticsHandler.trials++;
            Duration duration = Duration.between(StatisticsHandler.start_time, Instant.now());
            BigDecimal microseconds = new BigDecimal(duration.toNanos()).divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);
            BigDecimal seconds = microseconds.divide(BigDecimal.valueOf(1000000), 6, RoundingMode.HALF_UP);
            BigDecimal tickRate = BigDecimal.valueOf(Bukkit.getServer().getServerTickManager().getTickRate());
            BigDecimal seconds_adjusted = microseconds.multiply(tickRate).divide(BigDecimal.valueOf(20 * 1000000), 6, RoundingMode.HALF_UP);

            Bukkit.broadcastMessage("TRIAL ENDED: " + stop_reason + " (real " + seconds + "s, adjusted " + seconds_adjusted + "s)");
            boolean subjectDied = stop_reason.equals("Subject died");
            StatisticsHandler.csvWriter.appendLine(new String[] {String.valueOf(StatisticsHandler.current_kb_level), String.valueOf(seconds_adjusted)});

            if (StatisticsHandler.trials < StatisticsHandler.n_trials) {
//                if (StatisticsHandler.trials >= StatisticsHandler.n_trials / 2) {
//                    StatisticsHandler.experimental = true;
//                }

                if (StatisticsHandler.incremental) {
                    StatisticsHandler.current_kb_level = StatisticsHandler.trials / ((StatisticsHandler.n_trials + StatisticsHandler.kb_level) / (StatisticsHandler.kb_level + 1));
                    CommandKit.execute_trial(StatisticsHandler.current_kb_level);
                } else {
                    CommandKit.execute_trial(StatisticsHandler.kb_level);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        Entity attacker = e.getDamager();

        if (damaged.getUniqueId().equals(StatisticsHandler.subject_id) && attacker instanceof Zombie zombie) {
            ((Zombie) damaged).setTarget(zombie);
        }
    }
}

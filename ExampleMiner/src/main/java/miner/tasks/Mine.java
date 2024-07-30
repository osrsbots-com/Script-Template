package miner.tasks;

import com.osrsbots.orb.api.interact.interactables.entities.Objects;
import com.osrsbots.orb.api.interact.interactables.types.RSObject;
import com.osrsbots.orb.api.interact.interactables.types.RSPlayer;
import com.osrsbots.orb.api.util.ClientUI;
import com.osrsbots.orb.api.util.Delay;
import com.osrsbots.orb.api.util.Random;
import com.osrsbots.orb.scripts.framework.ScriptTask;
import lombok.extern.slf4j.Slf4j;
import miner.Mining;

@Slf4j
public class Mine implements ScriptTask {


    private final Mining plugin;

    public Mine(final Mining plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean validate() {
        return plugin.getPlayer().getAnimation() == -1;
    }

    @Override
    public int execute() {
        // Find rock
        final RSObject rocks = Objects.query()
                .nameContains("Copper rock")
                .maxDistance(5)
                .results().nearestToPlayer();

        // No rock :'(
        if (rocks == null) {
            log.info("Unable to find any Copper rocks!");
            return Random.nextInt(1000, 5000);
        }

        // Mine rocks
        log.info("Interacting...");
        ClientUI.highlightEntity(rocks);

        if (rocks.interact("Mine")) {
            final RSPlayer player = plugin.getPlayer();

            // Delay until player starts moving
            if (Delay.untilMoving(player, 1500, 3000)) {
                log.info("Waiting while moving...");

                // Delay while moving
                final int dis = Math.max(2, Math.min(10, rocks.getWorldLocation().distanceTo(player.getWorldLocation())));
                Delay.whileMoving(player, dis * 750, dis * 1500);

                log.info("Moving delay finished.");
            } else {
                log.info("Failed to start moving towards rock.");
            }
        } else {
            log.info("Failed to interact!");
        }

        return -1;
    }
}

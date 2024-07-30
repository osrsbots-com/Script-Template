package miner.tasks;

import com.osrsbots.orb.api.interact.interactables.entities.Items;
import com.osrsbots.orb.api.util.Random;
import com.osrsbots.orb.scripts.framework.ScriptTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Drop implements ScriptTask {

    @Override
    public boolean validate() {
        return Items.isInventoryFull();
    }

    @Override
    public int execute() {
        // Drop all copper ore
        final boolean dropped = Items.drop("Copper ore");
        log.info("Dropped Ores: " + dropped);

        // Random delay, between 0-4s
        return Random.nextInt(0, 4000);
    }
}

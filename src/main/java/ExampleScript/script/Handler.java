package script;

import com.osrsbots.orb.api.interact.interactables.entities.Items;
import com.osrsbots.orb.api.interact.interactables.entities.Objects;
import com.osrsbots.orb.api.interact.interactables.entities.Player;
import com.osrsbots.orb.api.interact.interactables.types.RSObject;
import com.osrsbots.orb.api.util.Delay;
import com.osrsbots.orb.api.util.Random;
import com.osrsbots.orb.api.util.ScriptUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Handler {

    public static void loop(SimpleChopper script) {
        log.info("@Loop~ " + script.state);

        switch (script.state) {
            case LOGIN:
                // Wait until logged in, cache reference to local player (used to check animation and see if idle)
                if (Player.isLoggedIn() && (script.player = Player.get()) != null) {
                    log.info("@LOGIN~ Ready!");
                    script.state = SimpleChopper.State.CHOP;
                }
                break;
            case CHOP:
                // Check if inventory is full
                if (Items.isInventoryFull()) {
                    log.info("@CHOP~ Inventory is full!");
                    script.state = SimpleChopper.State.DROP;
                    return;
                }

                // Scan game for desired object
                final RSObject tree = Objects.query()
                        .types(RSObject.Type.NORMAL)
                        .names("Tree")
                        .actions("Chop down")
                        .results().nearestToPlayer();

                // Check if we found the object
                if (tree == null) {
                    /*  TODO - Do something here, walk to the desired spot, logout, etc  */
                    ScriptUtil.stop("Failed to find tree!");
                    return;
                }

                // We DID find a tree - lets interact
                tree.interact("Chop down");

                /*
                    Script will keep looping unless we delay
                    NOTE: An improvement would be to adjust the min/max
                    time depending on a variable such as the distance of the tree
                 */
                if (Delay.untilAnimating(script.player, 6000, 9000)) {
                    log.info("@CHOP~ " + tree);

                    // Idle until tree is chopped
                    script.state = SimpleChopper.State.IDLE;
                } else {
                    // Failed to interact with tree
                    // NOTE: You should identify why, maybe the tree was chopped down or unreachable
                    log.info("@CHOP~ Failed to delay until animating!");
                }
                break;
            case IDLE:
                // Check if player is idle
                if (script.player.getAnimation() == -1) {

                    // Idle for a random amount of loops before chopping again
                    if (script.idles++ > Random.nextInt(2, 5)) {
                        log.info("@IDLE~ Ready!");

                        // Ready to chop another tree
                        script.state = SimpleChopper.State.CHOP;
                    }
                } else {
                    // Player's animation id may temporarily become -1 but player may begin animating again next frame
                    // Reset idle counter if animation is not -1
                    script.idles = 0;
                }

                // Idle while player is chopping...
                break;
            case DROP:
                // Drop all logs
                if (Items.drop("Logs")) {
                    script.state = SimpleChopper.State.IDLE;

                    log.info("@DROP~ Done!");
                }
                break;
        }
    }
}

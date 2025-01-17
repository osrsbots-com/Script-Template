package miner;

import com.osrsbots.orb.api.interact.interactables.entities.Items;
import com.osrsbots.orb.api.interact.interactables.entities.Objects;
import com.osrsbots.orb.api.interact.interactables.types.RSObject;
import com.osrsbots.orb.api.interact.interactables.types.RSPlayer;
import com.osrsbots.orb.api.interact.interactables.widgets.Bank;
import com.osrsbots.orb.api.interact.interactables.world.traverse.Traverse;
import com.osrsbots.orb.api.util.AnimationID;
import com.osrsbots.orb.api.util.ClientUI;
import com.osrsbots.orb.api.util.Delay;
import com.osrsbots.orb.api.util.Random;
import com.osrsbots.orb.scripts.framework.ScriptMeta;
import com.osrsbots.orb.scripts.framework.task.ScriptTask;
import com.osrsbots.orb.scripts.framework.task.TaskScript;
import net.runelite.api.coords.WorldArea;

import java.util.List;

@ScriptMeta(name = "ExampleMiner", author = "ORB", version = 0.1)
public class Mining implements TaskScript {

    public enum BankState {
        TRAVEL_TO_BANK, OPEN, DEPOSIT, WITHDRAW, CLOSE, TRAVEL_TO_MINES
    }

    final WorldArea bank = new WorldArea(3250, 3419, 8, 5, 0);

    final WorldArea mines = new WorldArea(3280, 3370, 11, 5, 0);

    public BankState bankState;

    public volatile boolean isMining;

    public int idles, idleLimit;

    public RSPlayer player;

    @Override
    public void onPlayerLogin(RSPlayer player) {
        this.player = player;
    }

    @Override
    public void onPlayerLogout() {
        this.player = null;
    }

    @Override
    public void onStart(String[] args) {
        // Reset
        this.isMining = false;

        this.bankState = null;
        this.player = null;

        this.idles = 0;
        this.idleLimit = Random.nextInt(2, 3);

        // Loop delay
        setLoopDelay(400, 1200);
    }

    final List<AnimationID> miningAnimIds = List.of(
            AnimationID.MINING_BRONZE_PICKAXE,
            AnimationID.MINING_IRON_PICKAXE,
            AnimationID.MINING_BLACK_PICKAXE,
            AnimationID.MINING_STEEL_PICKAXE,
            AnimationID.MINING_MITHRIL_PICKAXE,
            AnimationID.MINING_ADAMANT_PICKAXE,
            AnimationID.MINING_RUNE_PICKAXE
    );

    @Override
    public List<ScriptTask> setTasks() {
        return ScriptTask.Builder.create()
                /*Banking */
                .condition(() -> bankState != null, Random.nextInt(-4, 0)).process(() -> bankState)
                .matches(BankState.TRAVEL_TO_BANK, () -> {
                    if (Boolean.TRUE.equals(Traverse.to(bank))) {
                        bankState = BankState.OPEN;
                    }
                }, Random.nextInt(200, 800))
                .matches(BankState.OPEN, () -> {
                    if (Bank.isOpen() || Bank.openRandom()) {
                        bankState = BankState.DEPOSIT;
                    }
                })
                .matches(BankState.DEPOSIT, () -> {
                    if (Bank.depositInventory()) {
                        bankState = BankState.CLOSE;
                    }
                })
                .matches(BankState.CLOSE, () -> {
                    if (!Bank.isOpen() || Bank.close()) {
                        bankState = BankState.TRAVEL_TO_MINES;
                    }
                })
                .matches(BankState.TRAVEL_TO_MINES, () -> {
                    if (Boolean.TRUE.equals(Traverse.to(mines))) {
                        bankState = null;
                    }
                })
                .calculate()
                /* Idle for X seconds */
                .condition(() -> isMining).action(() -> {
                    if (player.getAnimation() == -1) {
                        if (idles++ > idleLimit) {
                            log.info("Idle limit reached [" + idleLimit + "]");
                            idleLimit = Random.nextInt(3, 6);
                            isMining = false;
                            idles = 0;
                        }
                    } else {
                        idles = 0;
                    }
                })
                /* Mine Copper rocks */
                .condition(() -> player.getAnimation() == -1).action(new Mine())
                .collect();
    }

    class Mine implements Runnable {
        @Override
        public void run() {
            if (Items.isInventoryFull()) {
                bankState = BankState.TRAVEL_TO_BANK;
                return;
            }

            final RSObject rocks = Objects.query().names("Copper rocks")
                    .maxDistance(15).results().nearestToPlayer();

            // No rock :'(
            if (rocks == null || !rocks.interact("Mine")) {
                log.info("Unable to find or interact with Copper rocks!");
                return;
            }

            ClientUI.highlightEntity(rocks);

            // Use distance of rock to player to delay for a reasonable amount of time (Contains distance within bounds of 2-10)
            final int dis = Math.max(5, Math.min(15, rocks.getWorldLocation().distanceTo(player.getWorldLocation())));

            // Delay until mining anim starts
            if (Delay.until(() -> {
                final int i = player.getAnimation();

                for (AnimationID anim : miningAnimIds) {
                    if (anim.getId() == i) {
                        return true;
                    }
                }
                return false;
            }, 4000, dis * 1000)) {
                log.info("Player has started mining!");
                isMining = true;
            } else {
                log.info("Failed to start mining!");
            }
        }
    }
}
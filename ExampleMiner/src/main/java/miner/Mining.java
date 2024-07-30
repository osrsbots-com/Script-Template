package miner;

import com.osrsbots.orb.api.interact.interactables.types.RSPlayer;
import com.osrsbots.orb.api.util.Random;
import com.osrsbots.orb.scripts.framework.ScriptMeta;
import com.osrsbots.orb.scripts.framework.ScriptTask;
import com.osrsbots.orb.scripts.framework.TaskScript;
import com.osrsbots.orb.scripts.managers.LoginManager;
import lombok.Getter;
import miner.tasks.Drop;
import miner.tasks.Mine;

import java.util.List;

@Getter
@ScriptMeta(name = "ExampleMiner", author = "ORB", version = 0.1)
public class Mining implements TaskScript {

    private RSPlayer player;

    /* OPTIONAL */
    @Override
    public void onStart(String[] args) {

      /*
        NOTE: When the TaskScript is processed: All tasks validated, in the case
            that all tasks return false on validate(), the default delay is 0-600ms. 1 game tick is 600ms.

            It is highly recommended to include a default delay.
            It is encouraged that you change the default values, in most cases, the maximum delay should be substantial.

            It is not recommended to set the loop to be near instant like below, due to unnecessary resource usage.
            setLoopDelay(0, 1);
       */


        setLoopDelay(0, Random.nextInt(400, 600));
    }


    @Override
    public boolean processTasks() {
        return this.player != null && LoginManager.loggedIn();
    }

    @Override
    public void onPlayerLogin(final RSPlayer player) {
        this.player = player;
    }

    @Override
    public List<ScriptTask> setTasks() {
        return List.of(
                new Drop(),
                new Mine(this)
        );
    }
}

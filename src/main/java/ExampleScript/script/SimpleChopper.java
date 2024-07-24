package script;

import com.osrsbots.orb.api.interact.interactables.types.RSPlayer;
import com.osrsbots.orb.api.util.ClientUI;
import com.osrsbots.orb.api.util.ScriptUtil;
import com.osrsbots.orb.scripts.framework.Script;
import com.osrsbots.orb.scripts.framework.ScriptMeta;
import util.GameListener;

import java.util.concurrent.atomic.AtomicBoolean;

@ScriptMeta(name = "SimpleChopper6", author = "ORB", version = 0.1)
public class SimpleChopper implements Script {

    /*
        Create a new enum, called State
        This is used to track the player's progress and identfy
        Which action the script should do during each loop
    */
    public enum State {
        LOGIN, CHOP, IDLE, DROP
    }

    // Keep track of our set State
    State state;

    // Keep track of local player
    RSPlayer player;

    // Keep track of how many loops Player is idle
    int idles = 0;

    // Keep reference to our instance of util.ThreatListener
    GameListener threatListener;

    // Keep reference to our user interface
    UserInterface ui;

    // Use Atomic as this variable is reference on both the script and swing UI threads
    AtomicBoolean pressedStart;

    // Called once when script is started
    @Override
    public void onStart(String[] args) {
        pressedStart = new AtomicBoolean(false);

        // Apply custom panel
        if (!ClientUI.applyCustomSettings(
                (ui = new UserInterface(this)).rootPanel)
        ) {
            // Failed to add settings panel - unable to start
            ScriptUtil.stop("Unable to apply custom settings!");
            return;
        }

        // Set how often loop() is called
        ScriptUtil.setLoopDelay(1000, 2000);

        // Create thread listener - register events
        threatListener = new GameListener();
        ScriptUtil.addSubscriptionListener(threatListener);

        // Set beginning state
        state = State.LOGIN;
    }

    @Override
    public void onStop() {
        // You MUST remove the listener onStop() - otherwise the class will continue to receive events (Memory leak)
        if (threatListener != null)
            ScriptUtil.removeSubscriptionListener(threatListener);
    }

    @Override
    public void loop() {
        if (pressedStart.get())
            Handler.loop(this);
    }
}

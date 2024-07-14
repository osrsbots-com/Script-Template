package util;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ActorDeath;
import net.runelite.client.eventbus.Subscribe;

@Slf4j
public class GameListener {

    // Method name must be named in relation to event, eg; onEventName
    @Subscribe
    public void onActorDeath(ActorDeath e) {
        // Do something....
    }

}

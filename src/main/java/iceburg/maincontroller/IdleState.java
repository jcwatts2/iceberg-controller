package iceburg.maincontroller;


import iceburg.events.ProximityEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 *
 */
public class IdleState implements IceburgState {

    private boolean isNew;

    private Logger logger;

    public IdleState() {
        isNew = true;
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public IceburgState handleEvent(final ProximityEvent proximityEvent) {


        if (proximityEvent.isPersonPresent()) {
            this.logger.debug("Idle State - person is present, transition to proximity");
            return new ProximityState();
        } else {
            this.logger.debug("Idle State - person is not present, maintain idle");
            return this;
        }
    }

    @Override
    public void performAction(final Map<Integer, SensorState> sensorStateMap,
                              final LightController lightController, final SoundController soundController) {

        if (isNew == false) {
            this.logger.debug("Idle State - still in idle, nothing to do");
            return;
        }

        this.logger.debug("Idle State - just entered idle state. Reset untouched facets to idle");

        for (SensorState state : sensorStateMap.values()) {
            if (state instanceof UntouchedState) {
                lightController.changeLightToIdle(state.getLightId());
            }
        }
        soundController.handleProximity(false);
    }

    @Override
    public void changeLight(final Integer lightId, final LightController lightController) {
        lightController.changeLightToIdle(lightId);
    }
}

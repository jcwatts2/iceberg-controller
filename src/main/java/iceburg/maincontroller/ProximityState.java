package iceburg.maincontroller;


import iceburg.events.ProximityEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 *
 */
public class ProximityState implements IceburgState {

    private Set<Integer> sensors;

    private boolean isNew;

    private Logger logger;

    public ProximityState() {
        super();

        this.logger = LoggerFactory.getLogger(this.getClass());

        this.sensors = new HashSet<>();
        this.isNew = true;
    }

    @Override
    public IceburgState handleEvent(final ProximityEvent proximityEvent) {

        if (proximityEvent.isPersonPresent()) {

            this.logger.debug("ProximityState - Person is present. Adding yes for sensor {}",
                    proximityEvent.getSensorNumber());

            sensors.add(proximityEvent.getSensorNumber());

        } else {

            this.logger.debug("ProximityState - Person not present. Removing yes for sensor {}",
                    proximityEvent.getSensorNumber());

            sensors.remove(proximityEvent.getSensorNumber());

            if (sensors.isEmpty()) {

                this.logger.debug("ProximityState - Person not present. All person present indicators removed." +
                        " Entering idle state");

                return new IdleState();
            }
        }
        return this;
    }

    @Override
    public void performAction(final Map<Integer, SensorState> sensorStateMap,
                              final LightController lightController, final SoundController soundController) {

        if (this.isNew == false) {
            this.logger.info("Already in ProximityState. Nothing to do.");
            return;
        }

        this.logger.info("Just entered ProximityState. Changing untouched facets to proximity.");

        for (SensorState state : sensorStateMap.values()) {
            if (state instanceof UntouchedState) {
                this.changeLight(state.getLightId(), lightController);
            }
        }
        soundController.handleProximity(true);

        this.isNew = false;
    }

    @Override
    public void changeLight(final Integer lightId, final LightController lightController) {
        lightController.changeLightToProximity(lightId);
    }
}

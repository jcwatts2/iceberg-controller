package iceburg.maincontroller;


import iceburg.events.ProximityEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 *
 */
public class ProximityState implements IceburgState {

    private Set<Integer> sensors;

    private boolean isNew;

    public ProximityState() {
        super();
        this.sensors = new HashSet<Integer>();
        this.isNew = true;
    }

    @Override
    public IceburgState handleEvent(final ProximityEvent proximityEvent) {

        if (proximityEvent.isPersonPresent()) {
            sensors.add(proximityEvent.getSensorNumber());
        } else {
            sensors.remove(proximityEvent.getSensorNumber());

            if (sensors.isEmpty()) {
                return new IdleState();
            }
        }
        return this;
    }

    @Override
    public void performAction(final Map<Integer, SensorState> sensorStateMap,
                              final LightController lightController, final SoundController soundController) {

        if (this.isNew == false) { return; }

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

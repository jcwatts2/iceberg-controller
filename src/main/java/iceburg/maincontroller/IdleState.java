package iceburg.maincontroller;


import iceburg.events.ProximityEvent;

import java.util.Map;


/**
 *
 */
public class IdleState implements IceburgState {

    private boolean isNew;

    public IdleState() {
        isNew = true;
    }

    @Override
    public IceburgState handleEvent(final ProximityEvent proximityEvent) {

        if (proximityEvent.isPersonPresent()) {
            return new ProximityState();
        } else {
            return this;
        }
    }

    @Override
    public void performAction(final Map<Integer, SensorState> sensorStateMap,
                              final LightController lightController, final SoundController soundController) {

        if (isNew == false) { return; }

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

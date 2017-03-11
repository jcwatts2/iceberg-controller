package iceburg.maincontroller;


import iceburg.events.ProximityEvent;

import java.util.Map;


/**
 * Created by jwatts on 3/10/17.
 */
public interface IceburgState {

    IceburgState handleEvent(ProximityEvent proximityEvent);

    void performAction(Map<Integer, SensorState> sensorStateMap,
                       LightController lightController, SoundController soundController);

    void changeLight(Integer lightId, LightController lightController);
}

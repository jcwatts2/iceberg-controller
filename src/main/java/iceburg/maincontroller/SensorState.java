package iceburg.maincontroller;


import iceburg.events.EventHub;
import iceburg.events.TouchEvent;


/**
 * An interface which represents the touch state for a specific sensor.
 */
public interface SensorState {

    SensorState handleEvent(TouchEvent event);

    Integer getSensorNumber();

    Integer getLightId();

    String getIceburgId();

    void performAction(EventHub eventHub, IceburgState iceburgState,
                       LightController lightController, SoundController soundController);
}

package iceburg.maincontroller;

import iceburg.events.EventHub;
import iceburg.events.TouchEvent;

/**
 * An interface which represents the touch state for a specific sensor.
 */
public interface SensorState {

    SensorState handleEvent(TouchEvent event);

    Integer getSensorNumber();

    String getIceburgId();

    void performAction(EventHub eventHub);
}

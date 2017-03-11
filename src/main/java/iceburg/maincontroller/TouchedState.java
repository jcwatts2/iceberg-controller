package iceburg.maincontroller;


import iceburg.events.EventHub;
import iceburg.events.TouchEvent;

import iceburg.maincontroller.Configuration.SensorToLight;

import lombok.ToString;


/**
 * Class which represents that a local sensor on an iceberg is currently
 * being touched.
 */
@ToString
public class TouchedState extends AbstractState {

    public TouchedState(String iceburgId, SensorToLight sensorToLight) {
        super(iceburgId, sensorToLight);
    }

    public SensorState handleEvent(final TouchEvent event) {

        if (this.getSensorNumber().equals(event.getSensorNumber()) == false) {
            return this;
        }

        if (event.isTouched()) {
            return handleTouch(event);
        } else {
            return handleUnTouch(event);
        }
    }

    public void performAction(EventHub eventHub, IceburgState iceburgState, LightController lightController,
            SoundController soundController) {

        lightController.changeLightToTouched(this.getLightId());
        soundController.handleTouch(this.getSensorNumber());
    }

    private SensorState handleUnTouch(final TouchEvent event) {

        if (this.getIceburgId().equals(event.getIcebergId())) {

            return new UntouchedState(this.getIceburgId(), this.getSensorToLight());

        } else {
            return this;
        }
    }

    private SensorState handleTouch(final TouchEvent event) {

        if (this.getIceburgId().equals(event.getIcebergId())) {
            return this;
        } else {
            return new CorrespondenceTouchState(this.getIceburgId(), this.getSensorToLight(), event.getIcebergId());
        }
    }
}

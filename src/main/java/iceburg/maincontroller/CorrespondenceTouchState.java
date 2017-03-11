package iceburg.maincontroller;


import iceburg.events.EventHub;
import iceburg.events.TouchEvent;

import iceburg.maincontroller.Configuration.SensorToLight;

import lombok.ToString;


/**
 *
 */
@ToString
public class CorrespondenceTouchState extends AbstractState {

    private String otherIceburgId;

    public CorrespondenceTouchState(String icebergId, SensorToLight sensorToLight, String otherIceburgId) {

        super(icebergId, sensorToLight);
        this.otherIceburgId = otherIceburgId;
    }

    public SensorState handleEvent(TouchEvent event) {

        if (this.getSensorNumber().equals(event.getSensorNumber()) == false) {
            return this;
        }

        if (event.isTouched()) {
            return this;
        } else {
            return this.handleUntouched(event);
        }
    }

    public SensorState handleUntouched(TouchEvent event) {

        if (this.getIceburgId().equals(event.getIcebergId())) {
            return new OtherBergTouchedState(this.getIceburgId(), this.getSensorToLight(), this.otherIceburgId);

        } else {
            return new TouchedState(this.getIceburgId(), this.getSensorToLight());
        }
    }

    public void performAction(EventHub eventHub, IceburgState iceburgState, LightController lightController,
                              SoundController soundController) {

        lightController.changeLightToCorrespondingTouch(this.getLightId());
        soundController.handleCorrespondingTouch(this.getSensorNumber());
    }
}

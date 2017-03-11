package iceburg.maincontroller;


import iceburg.events.EventHub;
import iceburg.events.TouchEvent;

import iceburg.maincontroller.Configuration.SensorToLight;

import lombok.ToString;


/**
 * Created by jwatts on 8/27/16.
 */
@ToString
public class UntouchedState extends AbstractState {

    public UntouchedState(String iceburgId, SensorToLight sensorToLight) {
        super(iceburgId, sensorToLight);
    }

    public void performAction(EventHub eventHub, IceburgState iceburgState, LightController lightController,
            SoundController soundController) {

        iceburgState.changeLight(this.getLightId(), lightController);
        soundController.handleUntouched(this.getSensorNumber());
    }

    public SensorState handleEvent(final TouchEvent event) {

        if (this.getSensorNumber().equals(event.getSensorNumber()) == false) {
            return this;
        }

        if (event.isTouched()) {
            return this.handleTouched(event);
        } else {
            return this;
        }
    }

    private SensorState handleTouched(final TouchEvent event) {

        if (this.getIceburgId().equals(event.getIcebergId())) {

            return new TouchedState(this.getIceburgId(), this.getSensorToLight());

        } else {

            return new OtherBergTouchedState(this.getIceburgId(), this.getSensorToLight(),
                    event.getIcebergId());
        }
    }
}

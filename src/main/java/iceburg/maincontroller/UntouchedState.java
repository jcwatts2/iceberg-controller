package iceburg.maincontroller;


import iceburg.events.TouchEvent;

import lombok.ToString;


/**
 * Created by jwatts on 8/27/16.
 */
@ToString
public class UntouchedState extends AbstractState {

    public UntouchedState(String iceburgId, Integer sensorNumber) {

        super(iceburgId, sensorNumber);
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

            return new TouchedState(this.getIceburgId(), this.getSensorNumber());

        } else {

            return new OtherBergTouchedState(this.getIceburgId(), this.getSensorNumber(),
                    event.getIcebergId());
        }
    }
}

package iceburg.maincontroller;


import iceburg.events.TouchEvent;

import lombok.ToString;


/**
 * Class which represents that a local sensor on an iceberg is currently
 * being touched.
 */
@ToString
public class TouchedState extends AbstractState {

    public TouchedState(String iceburgId, Integer sensorNumber ) {
        super(iceburgId, sensorNumber);
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

    private SensorState handleUnTouch(final TouchEvent event) {

        if (this.getIceburgId().equals(event.getIcebergId())) {

            return new UntouchedState(this.getIceburgId(), this.getSensorNumber());

        } else {
            return this;
        }
    }

    private SensorState handleTouch(final TouchEvent event) {

        if (this.getIceburgId().equals(event.getIcebergId())) {
            return this;
        } else {
            return new CorrespondenceTouchState(this.getIceburgId(), this.getSensorNumber(),
                    event.getIcebergId());
        }
    }
}

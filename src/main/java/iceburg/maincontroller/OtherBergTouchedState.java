package iceburg.maincontroller;


import iceburg.events.TouchEvent;

import lombok.ToString;


/**
 *
 *
 */
@ToString
public class OtherBergTouchedState extends TouchedState {

    private String otherIcebergId;

    public OtherBergTouchedState(String localIcebergId, Integer sensorNumber,
                                 String icebergId) {

        super(localIcebergId, sensorNumber);
        this.otherIcebergId = icebergId;
    }

    public SensorState handleEvent(TouchEvent event) {

        if (this.getSensorNumber().equals(event.getSensorNumber()) == false) {
            return this;
        }

        if (event.isTouched()) {
            return this.handleTouched(event);
        } else {
            return this.handleUnTouched(event);
        }
    }

    public SensorState handleTouched(TouchEvent event) {

        if (this.getIceburgId().equals(event.getIcebergId())) {

            return new CorrespondenceTouchState(this.getIceburgId(), this.getSensorNumber(),
                    this.otherIcebergId);

        } else {
            return new OtherBergTouchedState(this.getIceburgId(), this.getSensorNumber(),
                    event.getIcebergId());
        }
    }

    public SensorState handleUnTouched(TouchEvent event) {

        if (this.getIceburgId().equals(event.getIcebergId())) {

            return this;

        } else if (this.otherIcebergId.equals(event.getIcebergId())) {

            return new UntouchedState(this.getIceburgId(), this.getSensorNumber());

        } else {

            return this;
        }
    }
}

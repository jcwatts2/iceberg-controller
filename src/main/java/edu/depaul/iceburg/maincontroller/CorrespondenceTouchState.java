package edu.depaul.iceburg.maincontroller;


import edu.depaul.iceburg.events.EventHub;
import edu.depaul.iceburg.events.MultiTouchEvent;
import edu.depaul.iceburg.events.TouchEvent;


/**
 *
 */
public class CorrespondenceTouchState extends AbstractState {

    private String otherIceburgId;

    private Object lock;

    public CorrespondenceTouchState(String icebergId, Integer sensorNumber,
                                    String otherIcebugId) {

        super(icebergId, sensorNumber);
        this.otherIceburgId = otherIcebugId;
        this.lock = new Object();
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

    public void performAction(EventHub eventHub) {

        synchronized (this.lock) {

            MultiTouchEvent event = new MultiTouchEvent(this.getIceburgId(),
                    this.getSensorNumber(), true, System.currentTimeMillis());

            for (int i = 0; i < 3; i++) {
                eventHub.postEvent(event);
            }
        }
    }

    public SensorState handleUntouched(TouchEvent event) {

        if (this.getIceburgId().equals(event.getIcebergId())) {

            return new OtherBergTouchedState(this.getIceburgId(), this.getSensorNumber(),
                    this.otherIceburgId);

        } else {

            return new TouchedState(this.getIceburgId(), this.getSensorNumber());
        }
    }
}

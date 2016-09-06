package iceburg.maincontroller;


import iceburg.events.EventHub;


/**
 * Created by jwatts on 8/28/16.
 */
public abstract class AbstractState implements SensorState {

    private String iceburgId;

    private Integer sensorNumber;

    public AbstractState(String iceburgId, Integer sensorNumber) {
        this.iceburgId = iceburgId;
        this.sensorNumber = sensorNumber;
    }

    public void performAction(EventHub eventHub) {
        //do nothing
    }

    public String getIceburgId() {
        return this.iceburgId;
    }

    public Integer getSensorNumber() {
        return this.sensorNumber;
    }
}

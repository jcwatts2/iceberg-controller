package iceburg.maincontroller;


import iceburg.events.EventHub;

import iceburg.maincontroller.Configuration.SensorToLight;


/**
 * Created by jwatts on 8/28/16.
 */
public abstract class AbstractState implements SensorState {

    private String iceburgId;

    private SensorToLight sensorToLight;

    public AbstractState(String iceburgId, SensorToLight sensorToLight) {
        this.iceburgId = iceburgId;
        this.sensorToLight = sensorToLight;
    }

    public void performAction(EventHub eventHub, IceburgState iceburgState, LightController lightController,
                              SoundController soundController) {
    }

    @Override
    public Integer getLightId() {
        return this.sensorToLight.getLightId();
    }

    public String getIceburgId() {
        return this.iceburgId;
    }

    public Integer getSensorNumber() {
        return this.sensorToLight.getSensorId();
    }

    public SensorToLight getSensorToLight() {
        return this.sensorToLight;
    }
}

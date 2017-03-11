package iceburg.maincontroller;


import lombok.Data;
import lombok.ToString;

import java.util.List;


/**
 * Created by jwatts on 3/9/17.
 */
@Data
@ToString
public final class Configuration {

    private String iceburgId;

    private List<SensorToLight> sensorsToLights;

    private String serialPort;

    private int baudRate;

    private int allLightsIndicator;

    private String oscHost;

    private Integer oscPort;

    private Long finaleTime;

    @Data
    public static final class SensorToLight {

        private int lightId;
        private int sensorId;

        public SensorToLight() {
            this(0,0);
        }

        public SensorToLight(int sensorId, int lightId) {
            this.lightId = lightId;
            this.sensorId = sensorId;
        }
    }
}

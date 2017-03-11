package iceburg.maincontroller;


import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import java.util.Arrays;


/**
 *
 */
public class SoundController {

    private enum ProximityIndicator {OFF, ON};

    private enum TouchSensorIndicator {OFF, ON, CORRESPONDING};

    private OSCPortOut sender;

    private Configuration configuration;

    private Logger logger;

    public SoundController(Configuration configuration) {
        this.configuration = configuration;
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    public void init() {

        try {
            this.sender = new OSCPortOut(InetAddress.getByName(configuration.getOscHost()), configuration.getOscPort());

        } catch (SocketException ex) {
            this.logger.error(String.format("Error starting OSC Sender with host:%s and port:%s",
                    configuration.getOscHost(), configuration.getOscPort()), ex);

        } catch (UnknownHostException ex) {
            this.logger.error(String.format("Error starting OSC Sender with host:%s and port:%s",
                    configuration.getOscHost(), configuration.getOscPort()), ex);
        }
    }

    public void handleProximity(boolean personPresent) {

        Integer proximityIndicator = personPresent ?
                ProximityIndicator.ON.ordinal() : ProximityIndicator.OFF.ordinal();

        sendOSCMessageInResponseToEvent(new OSCMessage("/iceberg/proximity",
                Arrays.asList(new Object[]{proximityIndicator})));
    }

    public void handleUntouched(Integer sensorNumber) {
        sendOSCMessageInResponseToEvent(new OSCMessage("/iceberg/sensor",
                Arrays.asList(new Object[]{sensorNumber, TouchSensorIndicator.OFF.ordinal()})));
    }

    public void handleCorrespondingTouch(Integer sensorNumber) {
        sendOSCMessageInResponseToEvent(new OSCMessage("/iceberg/sensor",
                Arrays.asList(new Object[]{sensorNumber, TouchSensorIndicator.CORRESPONDING.ordinal()})));
    }

    public void handleTouch(Integer sensorNumber) {
        sendOSCMessageInResponseToEvent(new OSCMessage("/iceberg/sensor",
                Arrays.asList(new Object[]{sensorNumber, TouchSensorIndicator.ON.ordinal()})));
    }

    private void sendOSCMessageInResponseToEvent(OSCMessage message) {

        try {
            this.sender.send(message);

        } catch (IOException ex) {
            this.logger.error("Error sending touch event to OSC:", ex);
        }
    }
}

package iceburg.maincontroller;


import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by jwatts on 3/9/17.
 */
public class LightController {

    private static final Integer IDLE = 0;

    private static final Integer PROXIMITY = 1;

    private static final Integer TOUCHED = 2;

    private static final Integer CORRESPONDING_TOUCH = 3;

    private static final Integer FINALE = 3;

    private Configuration configuration;

    private OutputStream outputStream;

    private Logger logger;

    public LightController(Configuration configuration) {
        this.configuration = configuration;
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    public void init() {

        this.logger.debug("Setting up LightController");

        if (this.configuration.getSerialPort() == null) {
            throw new IllegalArgumentException("Serial port must be specified.");
        }

        CommPortIdentifier portIdentifier = null;

        try {
            portIdentifier = CommPortIdentifier.getPortIdentifier(this.configuration.getSerialPort());

        } catch (NoSuchPortException ex) {
            throw new InitializationException("Comm port was not found. Config: " + this.configuration, ex);
        }

        if(portIdentifier.isCurrentlyOwned()) {

            throw new IllegalArgumentException(String.format("Serial port %s already occupied.",
                    this.configuration.getSerialPort()));

        } else {

            int timeout = 2000;

            CommPort commPort = null;
            try {
                commPort = portIdentifier.open(this.getClass().getName(), timeout);
            } catch (PortInUseException ex) {
                throw new IllegalArgumentException(String.format("Serial port %s already occupied.",
                        this.configuration.getSerialPort()), ex);
            }

            if(commPort instanceof SerialPort) {

                try {

                    this.logger.debug("Serial Port setup initialized");

                    SerialPort serialPort = (SerialPort)commPort;
                    serialPort.setSerialPortParams(this.configuration.getBaudRate(), SerialPort.DATABITS_8,
                                SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                    this.outputStream = serialPort.getOutputStream();

                    this.logger.debug("Serial Port initialization complete");

                } catch (UnsupportedCommOperationException ex) {
                    throw new InitializationException("Unsupported comm exception. Config: " + this.configuration, ex);

                } catch (IOException ex) {
                    throw new InitializationException("Error creating output stream. Config: " + this.configuration, ex);
                }
            } else {
                throw new InitializationException("Comm port is of incorrect type. Config: " + this.configuration);
            }
        }
    }

    public void changeLightToIdle(int light) {
        this.postCommandToLight(IDLE, light);
    }

    public void changeLightToProximity(int light) {
        this.postCommandToLight(PROXIMITY, light);
    }

    public void changeLightToTouched(int light) {
        this.postCommandToLight(TOUCHED, light);
    }

    public void changeLightToCorrespondingTouch(int light) {
        this.postCommandToLight(CORRESPONDING_TOUCH, light);
    }

    public void finalAct() {
        this.postCommandToLight(FINALE, configuration.getAllLightsIndicator());
    }

    public void changeAllToIdle() {
        this.postCommandToLight(IDLE, configuration.getAllLightsIndicator());
    }

    private String getCommandForLight(Integer lightNumber, Integer command) {
        return String.format("facet:%s:%s", lightNumber, command);
    }

    private void postCommandToLight(Integer command, Integer light) {

        try {
            LightController.this.outputStream.write(LightController.this.getCommandForLight(light, command).getBytes());

        } catch (IOException ex) {
            this.logger.error("Unable to post all light command {} to light {}", command, light, ex);
        }
    }
}

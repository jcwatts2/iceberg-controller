package iceburg.maincontroller;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import iceburg.events.Event;
import iceburg.events.EventListener;
import iceburg.events.EventType;
import iceburg.events.ProximityEvent;
import iceburg.events.TouchEvent;

import iceburg.events.rabbitmq.RabbitMQEventHub;

import iceburg.maincontroller.Configuration.SensorToLight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Application class of the Iceburg Controller
 */
public class IceburgController {

    private static final String EVENTS_EXCHANGE = "events";

    private static final String RABBITMQ_URL = "amqp://localhost";

    private Map<Integer, SensorState> sensorStateMap;

    private IceburgState iceburgState;

    private RabbitMQEventHub eventHub;

    private LightController lightController;

    private SoundController soundController;

    private Configuration configuration;

    private final Logger logger;

    public IceburgController() {
        super();
        this.logger = LoggerFactory.getLogger(this.getClass());
        sensorStateMap = new HashMap<>();
        this.iceburgState = new IdleState();
    }

    public void init(Configuration configuration) {

        this.logger.debug("Init IceburgController: {}", configuration);
        this.configuration = configuration;

        this.lightController = new LightController(configuration);
        this.lightController.init();

        this.soundController = new SoundController(configuration);
        this.soundController.init();

        this.eventHub = new RabbitMQEventHub();
        this.eventHub.setRabbitMQUrl(RABBITMQ_URL);
        this.eventHub.setExchangeName(EVENTS_EXCHANGE);
        this.eventHub.init(configuration.getIceburgId());

        for (int i = 0; i < configuration.getSensorsToLights().size(); i++) {

            SensorToLight sensorToLight = configuration.getSensorsToLights().get(i);

            sensorStateMap.put(sensorToLight.getSensorId(),
                    new UntouchedState(configuration.getIceburgId(), sensorToLight));
        }

        this.eventHub.addListener(new EventListener() {

            public List<EventType> getWantedEvents() {
                return Arrays.asList(new EventType[]{EventType.PROXIMITY, EventType.TOUCH_ALL});
            }

            public void handleEvent(final Event e) {

                IceburgController.this.logger.debug("Handle event {}", e);

                if (e instanceof TouchEvent) {
                    IceburgController.this.handleEvent((TouchEvent) e);

                } else if (e instanceof ProximityEvent) {
                    IceburgController.this.handleEvent((ProximityEvent) e);
                }
            }
        });
    }

    synchronized void handleEvent(ProximityEvent event) {
        IceburgState newState = this.iceburgState.handleEvent(event);
        newState.performAction(this.sensorStateMap, this.lightController, this.soundController);
        this.iceburgState = newState;
    }

    synchronized void handleEvent(TouchEvent event) {

        SensorState state = this.sensorStateMap.get(event.getSensorNumber());

        if (state != null) {
            SensorState newState = state.handleEvent(event);
            this.sensorStateMap.put(event.getSensorNumber(), newState);
            this.logger.debug("State {} handling event {} to new state {}", state, event, newState);

            newState.performAction(this.eventHub, this.iceburgState, this.lightController, this.soundController);
        }

        if (this.allCorrespondingTouch()) {
            runFinale();
        }
    }

    private void runFinale() {

        this.lightController.finalAct();

        try {
            Thread.currentThread().sleep(this.configuration.getFinaleTime());

        } catch (InterruptedException ex) {
            this.logger.error("Finale sleep interrupted", ex);
        }
        this.lightController.changeAllToIdle();
    }

    private boolean allCorrespondingTouch() {

        boolean allCorresponding = true;

        for (SensorState state : this.sensorStateMap.values()) {
            allCorresponding = allCorresponding && (state instanceof CorrespondenceTouchState);
        }
        return allCorresponding;
    }

    public void printUsage() {
        System.out.println("java iceburg.maincontroller.IceburgController config.yml");
    }

    public static void main(String[] args) {

        IceburgController controller = new IceburgController();

        if (args.length < 1) {
            controller.printUsage();
            return;
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        try {
            Configuration configuration = mapper.readValue(new java.io.File(args[0]), Configuration.class);
            controller.init(configuration);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            controller.printUsage();
            return;
        }
    }
}

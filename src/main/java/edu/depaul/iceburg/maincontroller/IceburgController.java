package edu.depaul.iceburg.maincontroller;


import edu.depaul.iceburg.events.Event;
import edu.depaul.iceburg.events.EventListener;
import edu.depaul.iceburg.events.EventType;
import edu.depaul.iceburg.events.TouchEvent;

import edu.depaul.iceburg.events.rabbitmq.RabbitMQEventHub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private RabbitMQEventHub eventHub;

    private final Logger logger;

    public IceburgController() {
        super();
        this.logger = LoggerFactory.getLogger(this.getClass());
        sensorStateMap = new HashMap<>();
    }

    public void init(String icebergId, Integer numberOfSensors) {

        this.logger.debug("Init IceburgController: icebergId: {}, number of sensors: {}", icebergId, numberOfSensors);

        this.eventHub = new RabbitMQEventHub();
        this.eventHub.setRabbitMQUrl(RABBITMQ_URL);
        this.eventHub.setExchangeName(EVENTS_EXCHANGE);
        this.eventHub.init(icebergId);

        for (int i = 1; i < (numberOfSensors + 1); i++) {
            sensorStateMap.put(i, new UntouchedState(icebergId, i));
        }

        this.eventHub.addListener(new EventListener() {

            public List<EventType> getWantedEvents() {
                return Arrays.asList(new EventType[]{EventType.TOUCH_ALL});
            }

            public void handleEvent(final Event e) {
                IceburgController.this.logger.debug("Handle event {}", e);
                IceburgController.this.handleEvent((TouchEvent) e);
            }
        });
    }

    synchronized void handleEvent(TouchEvent event) {

        SensorState state = this.sensorStateMap.get(event.getSensorNumber());

        if (state != null) {
            SensorState newState = state.handleEvent(event);
            this.sensorStateMap.put(event.getSensorNumber(), newState);
            this.logger.debug("State {} handling event {} to new state {}", state, event, newState);

            newState.performAction(this.eventHub);
        }
    }

    public static void main(String[] args) {

        IceburgController controller = new IceburgController();

        if (args.length == 2) {
            String icebergId = args[0];
            Integer numberOfSensors = Integer.valueOf(args[1]);

            controller.init(icebergId, numberOfSensors);
        }
    }
}

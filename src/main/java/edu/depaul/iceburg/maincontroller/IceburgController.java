package edu.depaul.iceburg.maincontroller;


import edu.depaul.iceburg.events.Event;
import edu.depaul.iceburg.events.EventListener;
import edu.depaul.iceburg.events.EventType;
import edu.depaul.iceburg.events.TouchEvent;

import edu.depaul.iceburg.events.rabbitmq.RabbitMQEventHub;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by jwatts on 8/28/16.
 */
public class IceburgController {

    private String icebergId;

    private Integer numberOfSensors;

    private static final String EVENTS_EXCHANGE = "events";

    private static final String RABBITMQ_URL = "amqp://localhost";

    private Map<Integer, SensorState> sensorStateMap;

    private RabbitMQEventHub eventHub;

    public IceburgController() {
        super();
        sensorStateMap = new HashMap<Integer, SensorState>();
    }

    public void init(String icebergId, Integer numberOfSensors) {

        this.icebergId = icebergId;

        this.eventHub = new RabbitMQEventHub();
        this.eventHub.setRabbitMQUrl(RABBITMQ_URL);
        this.eventHub.setExchangeName(EVENTS_EXCHANGE);
        this.eventHub.init(this.icebergId);

        for (int i = 1; i < (numberOfSensors + 1); i++) {
            sensorStateMap.put(i, new UntouchedState(this.icebergId, i));
        }

        this.eventHub.addListener(new EventListener() {

            public List<EventType> getWantedEvents() {
                return Arrays.asList(new EventType[]{EventType.TOUCH_ALL});
            }

            public void handleEvent(final Event e) {
                IceburgController.this.handleEvent((TouchEvent) e);
            }
        });
    }

    synchronized void handleEvent(TouchEvent event) {

        SensorState state = this.sensorStateMap.get(event.getSensorNumber());

        if (state != null) {
            SensorState newState = state.handleEvent(event);
            this.sensorStateMap.put(event.getSensorNumber(), newState);
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

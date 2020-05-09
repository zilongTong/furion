package org.furion.core.listener;

import org.furion.core.enumeration.EventTriggerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FurionEventListener {


    private final static Logger LOGGER = LoggerFactory.getLogger(FurionEventListener.class);

    private Map<EventTriggerType, FurionEvent> eventMap = new ConcurrentHashMap<>();

    public void addListener(EventTriggerType type, Object target, Method callBack) {
        eventMap.put(type, new FurionEvent(target, callBack));
    }

    private void trigger(FurionEvent event) {
        event.setSource(this);
        event.setTime(System.currentTimeMillis());
        try {
            event.getCallback().invoke(event.getTarget(), event);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    protected void trigger(EventTriggerType e) {
        if (eventMap.containsKey(e)) {
            trigger(eventMap.get(e).setTrigger(e));
        }
    }
}

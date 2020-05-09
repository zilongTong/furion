package org.furion.core.listener;

import org.furion.core.enumeration.EventTriggerType;

import java.lang.reflect.Method;

public class FurionEvent<SC, TG> {

    /**
     * 事件源
     */
    private SC source;
    private TG target;
    private Method callback;

    private EventTriggerType trigger;

    private long time;

    public FurionEvent(TG target, Method callback) {
        this.target = target;
        this.callback = callback;
    }

    public SC getSource() {
        return source;
    }

    public TG getTarget() {
        return target;
    }

    public void setTarget(TG target) {
        this.target = target;
    }

    public Method getCallback() {
        return callback;
    }

    public void setCallback(Method callback) {
        this.callback = callback;
    }

    public EventTriggerType getTrigger() {
        return trigger;
    }

    public long getTime() {
        return time;
    }

    FurionEvent setTime(long time) {
        this.time = time;
        return this;
    }

    FurionEvent setSource(SC source) {
        this.source = source;
        return this;
    }

    FurionEvent setTrigger(EventTriggerType trigger) {
        this.trigger = trigger;
        return this;
    }
}

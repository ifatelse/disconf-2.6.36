package com.baidu.disconf.web.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2022/11/18 17:38
 * @Version : 1.0
 * @Copyright : Copyright (c) 2022 All Rights Reserved
 **/
public class EventDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(EventDispatcher.class);

    private static final CopyOnWriteArrayList<Entry> LISTENER_HUB = new CopyOnWriteArrayList<Entry>();

    /**
     * add event listener
     */
     public static void addEventListener(AbstractEventListener listener) {
        for (Class<? extends Event> type : listener.interest()) {
            getEntry(type).listeners.addIfAbsent(listener);
        }
    }

    public static void fireEvent(Event event) {
        if (null == event) {
            throw new IllegalArgumentException();
        }

        for (AbstractEventListener listener : getEntry(event.getClass()).listeners) {
            try {
                listener.onEvent(event);
            } catch (Exception e) {
                logger.error(e.toString(), e);
            }
        }
    }

    static Entry getEntry(Class<? extends Event> eventType) {
        for (; ; ) {
            for (Entry entry : LISTENER_HUB) {
                if (entry.eventType == eventType) {
                    return entry;
                }
            }

            Entry tmp = new Entry(eventType);
            /*
             *  false means already exists
             */
            if (LISTENER_HUB.addIfAbsent(tmp)) {
                return tmp;
            }
        }
    }

    static private class Entry {
        final Class<? extends Event> eventType;
        final CopyOnWriteArrayList<AbstractEventListener> listeners;

        Entry(Class<? extends Event> type) {
            eventType = type;
            listeners = new CopyOnWriteArrayList<>();
        }

        @Override
        public boolean equals(Object obj) {
            if (null == obj || obj.getClass() != getClass()) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            return eventType == ((Entry)obj).eventType;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

    }


}

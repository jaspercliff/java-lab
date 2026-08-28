package com.jasper.eventBus;

import com.google.common.eventbus.Subscribe;

/**
 * @version 1.0
 * @Author jasper
 * @Date 2024-09-19
 */
public class SimpleEventListener {

    @Subscribe
    public void listen(SimpleEvent event) {
        System.out.println("SimpleEventListener listen: " + event.message());
    }
}

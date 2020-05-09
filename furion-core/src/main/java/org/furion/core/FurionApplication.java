package org.furion.core;


import org.furion.core.protocol.server.FurionDefaultHttpServerBootstrap;


public class FurionApplication {

    public static void main(String[] args) {
        new FurionDefaultHttpServerBootstrap().run(args);
    }
}

package org.furion.core.circuitbreaker;

import org.furion.core.bean.eureka.Server;

import java.io.Serializable;

public class Resource  implements Cloneable, Serializable {

    private Server server;
    private String uri;

    public Resource(Server server, String uri) {
        this.server = server;
        this.uri = uri;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}

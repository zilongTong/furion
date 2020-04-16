package org.furion.core.ribbon;

import org.furion.core.bean.eureka.Server;

import java.util.List;

public interface ILoadBalancer {

     void addServers(List<Server> newServers);

     Server chooseServer(Object key);

     void markServerDown(Server server);

     List<Server> getReachableServers();

     List<Server> getAllServers();

}

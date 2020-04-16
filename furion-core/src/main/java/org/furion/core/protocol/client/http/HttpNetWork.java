package org.furion.core.protocol.client.http;

import org.furion.core.bean.eureka.Server;

public interface HttpNetWork<T, R> {

    R send(T t);

}

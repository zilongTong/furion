package org.furion.core.context;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-31
 */
public class RequestLRUContext {

    private static ConcurrentLRUHashMap<Long, RequestCommand> furionRequestMap = new ConcurrentLRUHashMap<Long, RequestCommand>(10240);

    public static void add(Long requestId, RequestCommand request) {
        furionRequestMap.put(requestId, request);
    }

    public static RequestCommand get(Long requestId) {
        return furionRequestMap.get(requestId);
    }

    public static void remove(Long requestId) {
        if (furionRequestMap != null && furionRequestMap.size() > 0) {

            furionRequestMap.remove(requestId);

        }
    }
}

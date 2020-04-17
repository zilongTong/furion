package org.furion.core.context;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-31
 */
public class RequestLRUContext {

    private static ConcurrentLRUHashMap<Long, FurionRequest> furionRequestMap = new ConcurrentLRUHashMap<Long, FurionRequest>(10240);

    public static void add(Long requestId, FurionRequest request) {
        furionRequestMap.put(requestId, request);
    }

    public static FurionRequest get(Long requestId) {
        return furionRequestMap.get(requestId);
    }

    public static void remove(Long requestId) {
        if (furionRequestMap != null && furionRequestMap.size() > 0) {

            furionRequestMap.remove(requestId);

        }
    }
}

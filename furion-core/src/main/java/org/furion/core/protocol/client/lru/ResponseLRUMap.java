package org.furion.core.protocol.client.lru;

import org.furion.core.utils.FurionResponse;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-31
 */
public class ResponseLRUMap {

    private static ConcurrentLRUHashMap<String, FurionResponse> furionResponseMap = new ConcurrentLRUHashMap<>(10240);

    public static void add(String requestId, FurionResponse furionResponse) {
        furionResponseMap.put(requestId, furionResponse);
    }

    public static FurionResponse get(String requestId) {
        return furionResponseMap.get(requestId);
    }

    public static void remove(FurionResponse furionResponse) {
        furionResponseMap.entrySet.stream().forEach(e -> {
            if (e.getValue().equals(furionResponse)) {
                furionResponseMap.remove(furionResponse);
            }
        });
    }
}

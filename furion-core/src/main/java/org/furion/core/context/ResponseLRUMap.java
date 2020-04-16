package org.furion.core.context;

import org.furion.core.utils.FurionResponse;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-31
 */
public class ResponseLRUMap {

    private static ConcurrentLRUHashMap<Long, FurionResponse> furionResponseMap = new ConcurrentLRUHashMap<Long, FurionResponse>(10240);

    public static void add(Long requestId, FurionResponse furionResponse) {
        furionResponseMap.put(requestId, furionResponse);
    }

    public static FurionResponse get(Long requestId) {
        return furionResponseMap.get(requestId);
    }

    public static void remove(FurionResponse furionResponse) {
        if (furionResponseMap != null && furionResponseMap.size() > 0) {
            furionResponseMap.entrySet.stream().forEach(e -> {
                if (e.getValue().equals(furionResponse)) {
                    furionResponseMap.remove(e.getKey());
                }
            });
        }
    }
}

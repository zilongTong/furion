package org.furion.core.protocol.client.lru;

import java.util.concurrent.CountDownLatch;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-31
 */
public class CountDownLatchLRUMap {

    private static ConcurrentLRUHashMap<String, CountDownLatch> countDownLatchMap = new ConcurrentLRUHashMap<>(10240);

    public static void add(String responseId, CountDownLatch countDownLatch) {
        countDownLatchMap.put(responseId, countDownLatch);
    }

    private static CountDownLatch get(String responseId) {
        return countDownLatchMap.get(responseId);
    }

    public static void remove(CountDownLatch countDownLatch) {
        countDownLatchMap.entrySet.stream().forEach(e -> {
            if (e.getValue().equals(countDownLatch)) {
                countDownLatchMap.remove(countDownLatch);
            }
        });
    }

}

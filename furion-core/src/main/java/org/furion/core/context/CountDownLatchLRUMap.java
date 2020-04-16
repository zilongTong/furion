package org.furion.core.context;

import java.util.concurrent.CountDownLatch;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-31
 */
public class CountDownLatchLRUMap {

    private static ConcurrentLRUHashMap<Long, CountDownLatch> countDownLatchMap = new ConcurrentLRUHashMap<Long, CountDownLatch>(10240);

    public static void add(Long responseId, CountDownLatch countDownLatch) {
        countDownLatchMap.put(responseId, countDownLatch);
    }

    public static CountDownLatch get(Long responseId) {
        return countDownLatchMap.get(responseId);
    }

    public static void remove(CountDownLatch countDownLatch) {
        if (countDownLatchMap != null && countDownLatchMap.size() > 0) {
            countDownLatchMap.entrySet.stream().forEach(e -> {
                if (e.getValue().equals(countDownLatch)) {
                    countDownLatchMap.remove(e.getKey());
                }
            });
        }
    }
}

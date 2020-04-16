package org.furion.core.discovery;


import lombok.Data;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-19
 */
@Data
public class EurekaExecutor {

    private Long timeGap;


    public void start() {
        RegistryFetchTask.getInstance().execute(timeGap);
    }

    public void destroy() {
        RegistryFetchTask.getInstance().stop();
    }

    public static void main(String[] args) {
        System.out.println(1);
    }

}

package org.furion.core.exception;

public class IllegalFilterUninstallException extends RuntimeException {


    public IllegalFilterUninstallException() {
        super(String.format("routeFilter can not be uninstall！"));
    }

}

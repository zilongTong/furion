package org.furion.core.bean.eureka;

public class Server {

    public static final String UNKNOWN_ZONE = "UNKNOWN";
    private String host;
    private int port;
    private String scheme;
    private volatile String id;
    private volatile boolean isAliveFlag;
    private String zone;
    private volatile boolean readyToServe;
    private Server.MetaInfo simpleMetaInfo;

    public Server(String host, int port) {
        this((String) null, host, port);
    }

    public Server(String scheme, String host, int port) {
        this.port = 80;
        this.zone = "UNKNOWN";
        this.readyToServe = true;
        this.simpleMetaInfo = new NamelessClass_1();
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.id = host + ":" + port;
        this.isAliveFlag = false;
    }

    class NamelessClass_1 implements Server.MetaInfo {
        NamelessClass_1() {
        }

        public String getAppName() {
            return null;
        }

        public String getServerGroup() {
            return null;
        }

        public String getServiceIdForDiscovery() {
            return null;
        }

        public String getInstanceId() {
            return Server.this.id;
        }
    }

    public Server(String id) {
        this.port = 80;
        this.zone = "UNKNOWN";
        this.readyToServe = true;


        this.simpleMetaInfo = new NamelessClass_1();
        this.setId(id);
        this.isAliveFlag = false;
    }

    public void setAlive(boolean isAliveFlag) {
        this.isAliveFlag = isAliveFlag;
    }

    public boolean isAlive() {
        return this.isAliveFlag;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isAliveFlag() {
        return isAliveFlag;
    }

    public void setAliveFlag(boolean aliveFlag) {
        isAliveFlag = aliveFlag;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public boolean isReadyToServe() {
        return readyToServe;
    }

    public void setReadyToServe(boolean readyToServe) {
        this.readyToServe = readyToServe;
    }

    public MetaInfo getSimpleMetaInfo() {
        return simpleMetaInfo;
    }

    public void setSimpleMetaInfo(MetaInfo simpleMetaInfo) {
        this.simpleMetaInfo = simpleMetaInfo;
    }

    public interface MetaInfo {
        String getAppName();

        String getServerGroup();

        String getServiceIdForDiscovery();

        String getInstanceId();
    }
}

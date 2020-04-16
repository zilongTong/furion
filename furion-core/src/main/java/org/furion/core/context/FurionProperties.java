package org.furion.core.context;


import org.apache.commons.lang3.ClassUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class FurionProperties {

    public static final List<String> SECURITY_HEADERS = Arrays.asList("Pragma", "Cache-Control", "X-Frame-Options", "X-Content-Type-Options", "X-XSS-Protection", "Expires");
    private String prefix = "";
    private boolean stripPrefix = true;
    private Boolean retryable = false;
    private Map<String, FurionProperties.FurionRoute> routes = new LinkedHashMap();
    private boolean addProxyHeaders = true;
    private boolean addHostHeader = false;
    private Set<String> ignoredServices = new LinkedHashSet();
    private Set<String> ignoredPatterns = new LinkedHashSet();
    private Set<String> ignoredHeaders = new LinkedHashSet();
    private boolean ignoreSecurityHeaders = true;
    private boolean forceOriginalQueryStringEncoding = false;

    private boolean ignoreLocalService = true;
    private FurionProperties.Host host = new FurionProperties.Host();
    private boolean traceRequestBody = true;
    private boolean removeSemicolonContent = true;
    private Set<String> sensitiveHeaders = new LinkedHashSet(Arrays.asList("Cookie", "Set-Cookie", "Authorization"));
    private boolean sslHostnameValidationEnabled = true;
    private boolean setContentLength;
    private boolean includeDebugHeader;
    private int initialStreamBufferSize;

    public FurionProperties() {

        this.setContentLength = false;
        this.includeDebugHeader = false;
        this.initialStreamBufferSize = 8192;
    }


    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isStripPrefix() {
        return this.stripPrefix;
    }

    public void setStripPrefix(boolean stripPrefix) {
        this.stripPrefix = stripPrefix;
    }

    public Boolean getRetryable() {
        return this.retryable;
    }

    public void setRetryable(Boolean retryable) {
        this.retryable = retryable;
    }

    public Map<String, FurionProperties.FurionRoute> getRoutes() {
        return this.routes;
    }

    public void setRoutes(Map<String, FurionProperties.FurionRoute> routes) {
        this.routes = routes;
    }

    public boolean isAddProxyHeaders() {
        return this.addProxyHeaders;
    }

    public void setAddProxyHeaders(boolean addProxyHeaders) {
        this.addProxyHeaders = addProxyHeaders;
    }

    public boolean isAddHostHeader() {
        return this.addHostHeader;
    }

    public void setAddHostHeader(boolean addHostHeader) {
        this.addHostHeader = addHostHeader;
    }

    public Set<String> getIgnoredServices() {
        return this.ignoredServices;
    }

    public void setIgnoredServices(Set<String> ignoredServices) {
        this.ignoredServices = ignoredServices;
    }

    public Set<String> getIgnoredPatterns() {
        return this.ignoredPatterns;
    }

    public void setIgnoredPatterns(Set<String> ignoredPatterns) {
        this.ignoredPatterns = ignoredPatterns;
    }

    public boolean isIgnoreSecurityHeaders() {
        return this.ignoreSecurityHeaders;
    }

    public void setIgnoreSecurityHeaders(boolean ignoreSecurityHeaders) {
        this.ignoreSecurityHeaders = ignoreSecurityHeaders;
    }

    public boolean isForceOriginalQueryStringEncoding() {
        return this.forceOriginalQueryStringEncoding;
    }

    public void setForceOriginalQueryStringEncoding(boolean forceOriginalQueryStringEncoding) {
        this.forceOriginalQueryStringEncoding = forceOriginalQueryStringEncoding;
    }


    public boolean isIgnoreLocalService() {
        return this.ignoreLocalService;
    }

    public void setIgnoreLocalService(boolean ignoreLocalService) {
        this.ignoreLocalService = ignoreLocalService;
    }

    public FurionProperties.Host getHost() {
        return this.host;
    }

    public void setHost(FurionProperties.Host host) {
        this.host = host;
    }

    public boolean isTraceRequestBody() {
        return this.traceRequestBody;
    }

    public void setTraceRequestBody(boolean traceRequestBody) {
        this.traceRequestBody = traceRequestBody;
    }

    public boolean isRemoveSemicolonContent() {
        return this.removeSemicolonContent;
    }

    public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
        this.removeSemicolonContent = removeSemicolonContent;
    }

    public Set<String> getSensitiveHeaders() {
        return this.sensitiveHeaders;
    }

    public void setSensitiveHeaders(Set<String> sensitiveHeaders) {
        this.sensitiveHeaders = sensitiveHeaders;
    }

    public boolean isSslHostnameValidationEnabled() {
        return this.sslHostnameValidationEnabled;
    }

    public void setSslHostnameValidationEnabled(boolean sslHostnameValidationEnabled) {
        this.sslHostnameValidationEnabled = sslHostnameValidationEnabled;
    }


    public boolean isSetContentLength() {
        return this.setContentLength;
    }

    public void setSetContentLength(boolean setContentLength) {
        this.setContentLength = setContentLength;
    }

    public boolean isIncludeDebugHeader() {
        return this.includeDebugHeader;
    }

    public void setIncludeDebugHeader(boolean includeDebugHeader) {
        this.includeDebugHeader = includeDebugHeader;
    }

    public int getInitialStreamBufferSize() {
        return this.initialStreamBufferSize;
    }

    public void setInitialStreamBufferSize(int initialStreamBufferSize) {
        this.initialStreamBufferSize = initialStreamBufferSize;
    }


    public String toString() {
        return "FurionProperties{" + "prefix='" + this.prefix + "', " + "stripPrefix=" + this.stripPrefix + ", " + "retryable=" + this.retryable + ", " + "routes=" + this.routes + ", " + "addProxyHeaders=" + this.addProxyHeaders + ", " + "addHostHeader=" + this.addHostHeader + ", " + "ignoredServices=" + this.ignoredServices + ", " + "ignoredPatterns=" + this.ignoredPatterns + ", " + "ignoredHeaders=" + this.ignoredHeaders + ", " + "ignoreSecurityHeaders=" + this.ignoreSecurityHeaders + ", " + "forceOriginalQueryStringEncoding=" + this.forceOriginalQueryStringEncoding + ", " + "', " + "ignoreLocalService=" + this.ignoreLocalService + ", " + "host=" + this.host + ", " + "traceRequestBody=" + this.traceRequestBody + ", " + "removeSemicolonContent=" + this.removeSemicolonContent + ", " + "sensitiveHeaders=" + this.sensitiveHeaders + ", " + "sslHostnameValidationEnabled=" + this.sslHostnameValidationEnabled + "setContentLength=" + this.setContentLength + ", " + "includeDebugHeader=" + this.includeDebugHeader + ", " + "initialStreamBufferSize=" + this.initialStreamBufferSize + ", " + "}";
    }

    public static class HystrixThreadPool {
        private boolean useSeparateThreadPools = false;
        private String threadPoolKeyPrefix = "";

        public HystrixThreadPool() {
        }

        public boolean isUseSeparateThreadPools() {
            return this.useSeparateThreadPools;
        }

        public void setUseSeparateThreadPools(boolean useSeparateThreadPools) {
            this.useSeparateThreadPools = useSeparateThreadPools;
        }

        public String getThreadPoolKeyPrefix() {
            return this.threadPoolKeyPrefix;
        }

        public void setThreadPoolKeyPrefix(String threadPoolKeyPrefix) {
            this.threadPoolKeyPrefix = threadPoolKeyPrefix;
        }
    }

    public static class HystrixSemaphore {
        private int maxSemaphores = 100;

        public HystrixSemaphore() {
        }

        public HystrixSemaphore(int maxSemaphores) {
            this.maxSemaphores = maxSemaphores;
        }

        public int getMaxSemaphores() {
            return this.maxSemaphores;
        }

        public void setMaxSemaphores(int maxSemaphores) {
            this.maxSemaphores = maxSemaphores;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                FurionProperties.HystrixSemaphore that = (FurionProperties.HystrixSemaphore) o;
                return this.maxSemaphores == that.maxSemaphores;
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.maxSemaphores});
        }

        public String toString() {
            StringBuffer sb = new StringBuffer("HystrixSemaphore{");
            sb.append("maxSemaphores=").append(this.maxSemaphores);
            sb.append('}');
            return sb.toString();
        }
    }

    public static class Host {
        private int maxTotalConnections = 200;
        private int maxPerRouteConnections = 20;
        private int socketTimeoutMillis = 10000;
        private int connectTimeoutMillis = 2000;
        private long timeToLive = -1L;
        private TimeUnit timeUnit;

        public Host() {
            this.timeUnit = TimeUnit.MILLISECONDS;
        }

        public Host(int maxTotalConnections, int maxPerRouteConnections, int socketTimeoutMillis, int connectTimeoutMillis, long timeToLive, TimeUnit timeUnit) {
            this.timeUnit = TimeUnit.MILLISECONDS;
            this.maxTotalConnections = maxTotalConnections;
            this.maxPerRouteConnections = maxPerRouteConnections;
            this.socketTimeoutMillis = socketTimeoutMillis;
            this.connectTimeoutMillis = connectTimeoutMillis;
            this.timeToLive = timeToLive;
            this.timeUnit = timeUnit;
        }

        public int getMaxTotalConnections() {
            return this.maxTotalConnections;
        }

        public void setMaxTotalConnections(int maxTotalConnections) {
            this.maxTotalConnections = maxTotalConnections;
        }

        public int getMaxPerRouteConnections() {
            return this.maxPerRouteConnections;
        }

        public void setMaxPerRouteConnections(int maxPerRouteConnections) {
            this.maxPerRouteConnections = maxPerRouteConnections;
        }

        public int getSocketTimeoutMillis() {
            return this.socketTimeoutMillis;
        }

        public void setSocketTimeoutMillis(int socketTimeoutMillis) {
            this.socketTimeoutMillis = socketTimeoutMillis;
        }

        public int getConnectTimeoutMillis() {
            return this.connectTimeoutMillis;
        }

        public void setConnectTimeoutMillis(int connectTimeoutMillis) {
            this.connectTimeoutMillis = connectTimeoutMillis;
        }

        public long getTimeToLive() {
            return this.timeToLive;
        }

        public void setTimeToLive(long timeToLive) {
            this.timeToLive = timeToLive;
        }

        public TimeUnit getTimeUnit() {
            return this.timeUnit;
        }

        public void setTimeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                FurionProperties.Host host = (FurionProperties.Host) o;
                return this.maxTotalConnections == host.maxTotalConnections && this.maxPerRouteConnections == host.maxPerRouteConnections && this.socketTimeoutMillis == host.socketTimeoutMillis && this.connectTimeoutMillis == host.connectTimeoutMillis && this.timeToLive == host.timeToLive && this.timeUnit == host.timeUnit;
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.maxTotalConnections, this.maxPerRouteConnections, this.socketTimeoutMillis, this.connectTimeoutMillis, this.timeToLive, this.timeUnit});
        }

        public String toString() {
            StringBuffer sb = new StringBuffer("Host{");
            sb.append("maxTotalConnections=").append(this.maxTotalConnections);
            sb.append(", maxPerRouteConnections=").append(this.maxPerRouteConnections);
            sb.append(", socketTimeoutMillis=").append(this.socketTimeoutMillis);
            sb.append(", connectTimeoutMillis=").append(this.connectTimeoutMillis);
            sb.append(", timeToLive=").append(this.timeToLive);
            sb.append(", timeUnit=").append(this.timeUnit);
            sb.append('}');
            return sb.toString();
        }
    }

    public class FurionRoute {
        private String id;
        private String path;
        private String serviceId;
        private String url;
        private boolean stripPrefix = true;
        private Boolean retryable;
        private Set<String> sensitiveHeaders = new LinkedHashSet();
        private boolean customSensitiveHeaders = false;

        public FurionRoute(String id, String path, String serviceId, String url, boolean stripPrefix, Boolean retryable, Set<String> sensitiveHeaders) {
            this.id = id;
            this.path = path;
            this.serviceId = serviceId;
            this.url = url;
            this.stripPrefix = stripPrefix;
            this.retryable = retryable;
            this.sensitiveHeaders = sensitiveHeaders;
            this.customSensitiveHeaders = sensitiveHeaders != null;
        }


        public FurionRoute(String path, String location) {
            this.id = this.extractId(path);
            this.path = path;
            this.setLocation(location);
        }


        public void setLocation(String location) {
            if (location == null || !location.startsWith("http:") && !location.startsWith("https:")) {
                this.serviceId = location;
            } else {
                this.url = location;
            }

        }

        private String extractId(String path) {
            path = path.startsWith("/") ? path.substring(1) : path;
            path = path.replace("/*", "").replace("*", "");
            return path;
        }


        public void setSensitiveHeaders(Set<String> headers) {
            this.customSensitiveHeaders = true;
            this.sensitiveHeaders = new LinkedHashSet(headers);
        }

        public boolean isCustomSensitiveHeaders() {
            return this.customSensitiveHeaders;
        }

        public String getId() {
            return this.id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPath() {
            return this.path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getServiceId() {
            return this.serviceId;
        }

        public void setServiceId(String serviceId) {
            this.serviceId = serviceId;
        }

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isStripPrefix() {
            return this.stripPrefix;
        }

        public void setStripPrefix(boolean stripPrefix) {
            this.stripPrefix = stripPrefix;
        }

        public Boolean getRetryable() {
            return this.retryable;
        }

        public void setRetryable(Boolean retryable) {
            this.retryable = retryable;
        }

        public Set<String> getSensitiveHeaders() {
            return this.sensitiveHeaders;
        }

        public void setCustomSensitiveHeaders(boolean customSensitiveHeaders) {
            this.customSensitiveHeaders = customSensitiveHeaders;
        }


        public String toString() {
            return "FurionRoute{" + "id='" + this.id + "', " + "path='" + this.path + "', " + "serviceId='" + this.serviceId + "', " + "url='" + this.url + "', " + "stripPrefix=" + this.stripPrefix + ", " + "retryable=" + this.retryable + ", " + "sensitiveHeaders=" + this.sensitiveHeaders + ", " + "customSensitiveHeaders=" + this.customSensitiveHeaders + ", " + "}";
        }
    }


}

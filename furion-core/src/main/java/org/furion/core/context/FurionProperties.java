package org.furion.core.context;

import org.furion.core.context.properties.BasePropertiesContainer;
import org.furion.core.context.properties.PropertyValueChangeEvent;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class FurionProperties extends BasePropertiesContainer {

    public static final List<String> SECURITY_HEADERS = Arrays.asList("Pragma", "Cache-Control", "X-Frame-Options", "X-Content-Type-Options", "X-XSS-Protection", "Expires");
    private String prefix = "furion";
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


    @Override
    public String toString() {
        return "FurionProperties{" + "prefix='" + this.prefix + "', " + "stripPrefix=" + this.stripPrefix + ", " + "retryable=" + this.retryable + ", " + "routes=" + this.routes + ", " + "addProxyHeaders=" + this.addProxyHeaders + ", " + "addHostHeader=" + this.addHostHeader + ", " + "ignoredServices=" + this.ignoredServices + ", " + "ignoredPatterns=" + this.ignoredPatterns + ", " + "ignoredHeaders=" + this.ignoredHeaders + ", " + "ignoreSecurityHeaders=" + this.ignoreSecurityHeaders + ", " + "forceOriginalQueryStringEncoding=" + this.forceOriginalQueryStringEncoding + ", " + "', " + "ignoreLocalService=" + this.ignoreLocalService + ", " + "traceRequestBody=" + this.traceRequestBody + ", " + "removeSemicolonContent=" + this.removeSemicolonContent + ", " + "sensitiveHeaders=" + this.sensitiveHeaders + ", " + "sslHostnameValidationEnabled=" + this.sslHostnameValidationEnabled + "setContentLength=" + this.setContentLength + ", " + "includeDebugHeader=" + this.includeDebugHeader + ", " + "initialStreamBufferSize=" + this.initialStreamBufferSize + ", " + "}";
    }


    @Override
    public void refresh(List<PropertyValueChangeEvent> refreshDataList) {

    }

    @Override
    public <V> V getPropertyValue(String key, Class<V> tClass) {
        return null;
    }

    public class FurionRoute {
        private String id;
        private String path;
        private String serviceId;

        //go python
        private List<String> ipList;

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

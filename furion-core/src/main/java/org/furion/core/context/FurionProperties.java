package org.furion.core.context;

import lombok.Data;
import org.furion.core.annotation.Ignore;
import org.furion.core.context.properties.BasePropertiesContainer;
import org.furion.core.context.properties.IPropertyValueChangeEvent;
import org.furion.core.utils.UrlMatchUtil;

import java.util.*;

@Data
public class FurionProperties extends BasePropertiesContainer {

    public static final List<String> SECURITY_HEADERS = Arrays.asList("Pragma", "Cache-Control", "X-Frame-Options", "X-Content-Type-Options", "X-XSS-Protection", "Expires");
    private String prefix = "furion";
    private boolean stripPrefix = true;
    private Boolean retryable = false;
    @Ignore
    private Map<String, List<FurionProperties.FurionRoute>> routes = new LinkedHashMap();
    private List<FurionProperties.FurionRoute> routeList = new ArrayList<>();
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

    @Override
    public String toString() {
        return "FurionProperties{" + "prefix='" + this.prefix + "', " + "stripPrefix=" + this.stripPrefix + ", " + "retryable=" + this.retryable + ", " + "routes=" + this.routes + ", " + "addProxyHeaders=" + this.addProxyHeaders + ", " + "addHostHeader=" + this.addHostHeader + ", " + "ignoredServices=" + this.ignoredServices + ", " + "ignoredPatterns=" + this.ignoredPatterns + ", " + "ignoredHeaders=" + this.ignoredHeaders + ", " + "ignoreSecurityHeaders=" + this.ignoreSecurityHeaders + ", " + "forceOriginalQueryStringEncoding=" + this.forceOriginalQueryStringEncoding + ", " + "', " + "ignoreLocalService=" + this.ignoreLocalService + ", " + "traceRequestBody=" + this.traceRequestBody + ", " + "removeSemicolonContent=" + this.removeSemicolonContent + ", " + "sensitiveHeaders=" + this.sensitiveHeaders + ", " + "sslHostnameValidationEnabled=" + this.sslHostnameValidationEnabled + "setContentLength=" + this.setContentLength + ", " + "includeDebugHeader=" + this.includeDebugHeader + ", " + "initialStreamBufferSize=" + this.initialStreamBufferSize + ", " + "}";
    }


    @Override
    public void refresh(List<IPropertyValueChangeEvent> refreshDataList) {
        //routeList --> routes
        FurionGatewayContext.getInstance().getPropertiesManager().initPropertiesObject(this);
        routes = new HashMap<>();
        for(FurionRoute furionRoute:routeList){
            String path = furionRoute.path;
            if(path.indexOf("/",1)>0)
                path = path.substring(0,path.indexOf("/",1));
            if(!routes.containsKey(path)){
                routes.put(path,new ArrayList<>());
            }
            routes.get(path).add(furionRoute);
        }
    }

    @Data
    public static class FurionRoute {
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

        public FurionRoute(){}


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

        @Override
        public String toString() {
            return "FurionRoute{" + "id='" + this.id + "', " + "path='" + this.path + "', " + "serviceId='" + this.serviceId + "', " + "url='" + this.url + "', " + "stripPrefix=" + this.stripPrefix + ", " + "retryable=" + this.retryable + ", " + "sensitiveHeaders=" + this.sensitiveHeaders + ", " + "customSensitiveHeaders=" + this.customSensitiveHeaders + ", " + "}";
        }
    }


}

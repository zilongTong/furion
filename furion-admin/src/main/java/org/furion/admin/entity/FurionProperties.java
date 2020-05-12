package org.furion.admin.entity;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import java.util.*;

@Data
public class FurionProperties{

    public static final List<String> SECURITY_HEADERS = Arrays.asList("Pragma", "Cache-Control", "X-Frame-Options", "X-Content-Type-Options", "X-XSS-Protection", "Expires");
    private String prefix = "furion";
    private boolean stripPrefix = true;
    private Boolean retryable = false;
    private List<FurionProperties.FurionRoute> routeList= new ArrayList<>();
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

    @Data
    public static class FurionRoute {
        private String id;
        private String path;
        private String serviceId;

        //go python
        private List<String> ipList;
        private List<String> notVisitList;

        private String url;
        private boolean stripPrefix = true;
        private Boolean retryable;
        private Set<String> sensitiveHeaders = new LinkedHashSet();
        private boolean customSensitiveHeaders = false;
    }


}

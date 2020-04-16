package org.furion.core.context;



import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Route {

    private String id;
    private String fullPath;
    private String path;
    private String location;
    private String prefix;
    private Boolean retryable;
    private Set<String> sensitiveHeaders;
    private boolean customSensitiveHeaders;
    private boolean prefixStripped;

    public Route(String id, String path, String location, String prefix, Boolean retryable, Set<String> ignoredHeaders) {
        this.sensitiveHeaders = new LinkedHashSet();
        this.prefixStripped = true;
        this.id = id;
        
        this.path = path;
        this.fullPath = prefix + path;
        this.location = location;
        this.retryable = retryable;
        this.sensitiveHeaders = new LinkedHashSet();
        if (ignoredHeaders != null) {
            this.customSensitiveHeaders = true;
            Iterator var7 = ignoredHeaders.iterator();

            while(var7.hasNext()) {
                String header = (String)var7.next();
                this.sensitiveHeaders.add(header.toLowerCase());
            }
        }

    }

    public Route(String id, String path, String location, String prefix, Boolean retryable, Set<String> ignoredHeaders, boolean prefixStripped) {
        this(id, path, location, prefix, retryable, ignoredHeaders);
        this.prefixStripped = prefixStripped;
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

    public String getFullPath() {
        return this.fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
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

    public void setSensitiveHeaders(Set<String> sensitiveHeaders) {
        this.sensitiveHeaders = sensitiveHeaders;
    }

    public void setCustomSensitiveHeaders(boolean customSensitiveHeaders) {
        this.customSensitiveHeaders = customSensitiveHeaders;
    }

    public boolean isPrefixStripped() {
        return this.prefixStripped;
    }

    public void setPrefixStripped(boolean prefixStripped) {
        this.prefixStripped = prefixStripped;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Route that = (Route)o;
            return this.customSensitiveHeaders == that.customSensitiveHeaders && this.prefixStripped == that.prefixStripped && Objects.equals(this.id, that.id) && Objects.equals(this.fullPath, that.fullPath) && Objects.equals(this.path, that.path) && Objects.equals(this.location, that.location) && Objects.equals(this.prefix, that.prefix) && Objects.equals(this.retryable, that.retryable) && Objects.equals(this.sensitiveHeaders, that.sensitiveHeaders);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.fullPath, this.path, this.location, this.prefix, this.retryable, this.sensitiveHeaders, this.customSensitiveHeaders, this.prefixStripped});
    }

    public String toString() {
        return "Route{" + "id='" + this.id + "', " + "fullPath='" + this.fullPath + "', " + "path='" + this.path + "', " + "location='" + this.location + "', " + "prefix='" + this.prefix + "', " + "retryable=" + this.retryable + ", " + "sensitiveHeaders=" + this.sensitiveHeaders + ", " + "customSensitiveHeaders=" + this.customSensitiveHeaders + ", " + "prefixStripped=" + this.prefixStripped + "}";
    }
}

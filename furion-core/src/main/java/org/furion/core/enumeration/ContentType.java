package org.furion.core.enumeration;

public enum ContentType {

    FROM(1, "application/x-www-form-urlencoded"),
    JSON(2, "application/json"),
    //上传文件
    MULTIPART(3, "multipart/form-data"),
    PLAIN(4, "text/plain");

    int code;
    String type;

    ContentType(int code, String type) {
        this.code = code;
        this.type = type;
    }

    public String getType(int code) {
        for (ContentType type : ContentType.values()) {
            if (type.code == code) {
                return type.type;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}

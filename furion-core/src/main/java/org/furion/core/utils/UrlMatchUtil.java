package org.furion.core.utils;

public class UrlMatchUtil {
    /**
     * 存在 /* /**
     * @param target
     * @param regex
     * @return
     */
    public static boolean isMatch(String target,String regex){
        target = target.indexOf("?")>0?target.substring(0,target.indexOf("?")):target;
        if(regex.endsWith("/**")){
            return target.startsWith(regex.substring(0,regex.indexOf("/**")));
        }else if(regex.endsWith("/*")){
            return target.substring(0,target.lastIndexOf("/")).equals(regex.substring(0,regex.indexOf("/*")));
        }else {
            return target.equals(regex);
        }
    }

    public static String transUrl(String target,String regex){
        if(regex.endsWith("/**") || regex.endsWith("/*")){
            return target.substring(regex.substring(0,regex.indexOf("/**")).length());
        }else {
            return target;
        }
    }

    public static void main(String[] args) {
        System.out.println(UrlMatchUtil.isMatch("/abc/def/gg","/abc/def"));
        System.out.println(UrlMatchUtil.isMatch("/abc/def/gg","/abc/**"));
        System.out.println(UrlMatchUtil.transUrl("/abc/def/gg","/abc/**"));
        System.out.println(UrlMatchUtil.transUrl("/abc/def/gg","/abc"));
        System.out.println(UrlMatchUtil.isMatch("/abc/def/gg","/abc/*"));
        System.out.println(UrlMatchUtil.isMatch("/abc/def/gg?a=1&b=2","/abc/def/gg"));
    }
}


package org.furion.admin.endpoint;

import org.furion.admin.entity.FurionProperties;
import org.furion.admin.entity.SystemProperties;
import org.furion.admin.util.JSONUtil;
import org.furion.admin.util.OKHttpUtil;
import org.furion.admin.util.PropertiesUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.*;
import java.util.Properties;

@RestController
@Configuration
@EnableSwagger2
@RequestMapping("/config")
public class ConfigController {

    public static final String CONFIG_ROUTE_PATH = "/config/route";
    public static final String CONFIG_SYSTEM_PATH = "/config/system";
    public static final String CONFIG_FILTER_PATH = "/config/filter";

    @RequestMapping(value = "/route", method = RequestMethod.POST)
    @ResponseBody
    public String refreshRoute(String furionProperties) {
        return OKHttpUtil.post(PropertiesUtil.getProperty("gateway.url") + CONFIG_ROUTE_PATH, furionProperties);
    }

    @RequestMapping(value = "/system", method = RequestMethod.POST)
    @ResponseBody
    public String refreshSystem(String systemProperties) {
        return OKHttpUtil.post(PropertiesUtil.getProperty("gateway.url") + CONFIG_SYSTEM_PATH, systemProperties);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }
        try {
            String fileName = file.getOriginalFilename();
            if(ConfigController.class.getResource("/filter") == null){
                File f = new File(ConfigController.class.getResource("/").getPath()+"/filter");
                f.mkdir();
            }
            String filePath = ConfigController.class.getResource("/filter").getPath() + "/";
            File dest = new File(filePath + fileName);
            file.transferTo(dest);
            return "上传成功";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "上传失败！";
    }

    @RequestMapping(value = "/filter", method = RequestMethod.POST)
    @ResponseBody
    public String addFilter(String fileName) {
        StringBuilder sb = new StringBuilder();
        String filePath = ConfigController.class.getResource("/filter").getPath() + "/";
        String s;
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath + fileName))))){
            while ((s = br.readLine()) != null){
                sb.append(s);
            }
            return OKHttpUtil.post(PropertiesUtil.getProperty("gateway.url") + CONFIG_FILTER_PATH, sb.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}

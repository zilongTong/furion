package org.furion.admin.endpoint;


import org.furion.admin.LoginDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Objects;

@RestController
public class LoginController implements ApplicationContextAware {

    public static final Logger log = LoggerFactory.getLogger(LoginController.class);


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login(HttpServletRequest request, HttpServletResponse response, @RequestBody LoginDTO loginDTO) {

        return Result.successData(loginDTO);
    }


    @RequestMapping(value = "/route", method = RequestMethod.POST)
    public Result route(HttpServletRequest request, HttpServletResponse response, @RequestBody LoginDTO loginDTO) {

        return Result.successData(loginDTO);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }




    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    @ResponseBody
    public String ping() {
        return "pong";
    }

}

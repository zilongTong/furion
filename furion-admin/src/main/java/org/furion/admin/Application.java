package org.furion.admin;


import org.furion.admin.endpoint.LoginController;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


@SpringBootApplication


public class Application {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.refresh();

        LoginController controller = (LoginController) ctx.getBean("loginController");

        System.out.println(controller.ping());
    }


}

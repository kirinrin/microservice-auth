package me.kirinrin.reporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Kirinrin
 */
@RestController
@SpringBootApplication
public class ReporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReporterApplication.class, args);
    }

    @GetMapping("reports")
    public String reports(@RequestHeader(value="tenant_id", defaultValue = "0") String tenantId){
        String user = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
                .getHeader("tenant_id");
        System.out.println(user);
        return "tenantId header = " + tenantId;
    }

    @GetMapping("login")
    public String login(@RequestHeader(value="tenant_id", defaultValue = "0") String tenantId){
        String user = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
                .getHeader("tenant_id");
        System.out.println(user);
        return "login tenantId header = " + tenantId;
    }


    @GetMapping("logout")
    public String logout(@RequestHeader(value="tenant_id", defaultValue = "0") String tenantId){
        String user = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
                .getHeader("tenant_id");
        System.out.println(user);
        return "logout tenantId header = " + tenantId;
    }

}

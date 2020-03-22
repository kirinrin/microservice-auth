package me.kirinrin.reporter;

import com.netflix.client.http.HttpRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
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

    @GetMapping("header")
    public String header(@RequestHeader HttpHeaders headers) {
        headers.forEach( (s, b)-> {
            System.out.println("header = " + s);
            b.forEach( v -> System.out.print("\t" + v));

        });
       return "header";
    }
    @GetMapping("timeout")
    public String timeout(@RequestHeader HttpHeaders headers) {
        try {
            System.out.println("线程等待");
            Thread.sleep(1000*60*3);
        } catch (InterruptedException e) {
            System.out.println("线程打断");
            e.printStackTrace();
        }
        return "timeout";
    }
}

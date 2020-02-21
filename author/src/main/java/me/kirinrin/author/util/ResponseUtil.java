package me.kirinrin.author.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.kirinrin.author.entity.Rest;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * ResponseUtil
 *
 * @author Felordcn
 * @since 11:20 2019/10/28
 **/
public class ResponseUtil {
    private ResponseUtil() {
    }



    public static void responseJsonWriter(HttpServletResponse response, Rest rest) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ObjectMapper objectMapper = new ObjectMapper();
        String resBody = objectMapper.writeValueAsString(rest);
        PrintWriter printWriter = response.getWriter();
        printWriter.print(resBody);
        printWriter.flush();
        printWriter.close();
    }
}

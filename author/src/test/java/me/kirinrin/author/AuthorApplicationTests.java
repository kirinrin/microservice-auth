package me.kirinrin.author;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorApplicationTests {
    @Autowired
    private MockMvc mockMvc;


    @Test
    @WithMockUser(value = "admin", password = "12345")
    public void contextLoads() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/test")).andDo(MockMvcResultHandlers.log());
    }

}

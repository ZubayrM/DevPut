package object.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import object.dto.request.auth.LoginDto;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
class ApiAuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper oM;


    @Test
    @SneakyThrows
    void login() {
        LoginDto dto = new LoginDto();
        dto.setEMail("mod@mail.ru");
        dto.setPassword("111222");

        mvc.perform(post("/api/auth/login")
                .content(oM.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result",is(true)))
                .andExpect(jsonPath("$.user.id", is(1)))
                .andExpect(jsonPath("$.user.name", is("Moderator Name")))
                .andExpect(jsonPath("$.user.email", is("mod@mail.ru")));
    }

    @Test
    void check() {
    }

    @Test
    void restore() {
    }

    @Test
    void updatePassword() {
    }

    @Test
    void register() {
    }

    @Test
    @SneakyThrows
    void captcha() {
        mvc.perform(get("/api/auth/captcha")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.secret").exists())
                .andExpect(jsonPath("$.image").exists());
    }

    @Test
    @SneakyThrows
    void logout() {

    }
}
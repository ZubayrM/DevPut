package object.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import object.config.security.MyUserDetails;
import object.dto.request.auth.LoginDto;
import object.dto.request.auth.RegisterDto;
import object.dto.request.auth.UpdatePasswordDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.TreeMap;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
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
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper oM;

    private static MyUserDetails mod;

    private static MyUserDetails user;


    @BeforeEach
    public void setMvc() {

        mod = new MyUserDetails("mod@mail.ru", "111222", "Moderator Name", 1);
        user = new MyUserDetails("user2@mail.ru", "111444", "User2 Name", 0);

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

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
    @SneakyThrows
    void check() {
        mvc.perform(get("/api/auth/check")
                .with(user(user)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void restore() {
        mvc.perform(post("/api/auth/restore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(oM.writeValueAsString(new TreeMap<String, String>(){{put("email", user.getEmail());}}))
                .with(anonymous()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void updatePassword() {
        mvc.perform(post("/api/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(oM.writeValueAsString(new UpdatePasswordDto("1","1","1234","666666"))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void register() {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(oM.writeValueAsString(new RegisterDto("test@mail.ru", "123456", "testName", "1234", "secret"))))
                .andDo(print())
                .andExpect(status().isOk());
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
        mvc.perform(get("/api/auth/logout")
                .with(user(user)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
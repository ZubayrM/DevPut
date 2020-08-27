package object.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import object.config.security.MyUserDetails;
import object.dto.request.post.ModerationPostDto;
import object.model.enums.ModerationStatus;
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

import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
class ApiModControllerTest {

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

        mod = new MyUserDetails("mod@mail.ru", "111222", "Зубайр", 1);
        user = new MyUserDetails("user2@mail.ru", "111444", "Курбан", 0);

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @SneakyThrows
    void moderation() {
        mvc.perform(post("/api/moderation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(oM.writeValueAsString(new ModerationPostDto(2, ModerationStatus.ACCEPTED.toString())))
                .with(user(mod)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getSettings() {
        mvc.perform(get("/api/settings")
                .with(user(mod)))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @SneakyThrows
    void setSettings() {
        Map<String, Boolean> globalSettings = new TreeMap<String, Boolean>(){{
            put("MULTIUSER_MODE", true);
            put("POST_PREMODERATION", false);
            put("STATISTICS_IS_PUBLIC", false);
        }};


        mvc.perform(put("/api/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(oM.writeValueAsString(globalSettings))
                .with(user(mod)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
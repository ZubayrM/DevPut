package object.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import object.config.security.MyUserDetails;
import object.dto.request.post.ModerationPostDto;
import object.dto.request.post.RequestCommentDto;
import object.dto.request.user.MyProfileDto;
import object.model.enums.ModerationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.TreeMap;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
class ApiGeneralControllerTest {

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
    void getInfo() {
        mvc.perform(get("/api/init"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("DevPut")))
                .andExpect(jsonPath("$.phone", is("+7 938 203-24-99")))
                .andExpect(jsonPath("$.email", is("zubayr_@live.com")));
    }

    @Test
    @SneakyThrows
    void getCalendar() {
        mvc.perform(get("/api/calendar")
                .param("year", "2019"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.years[0]", is("2019")));
    }

    @Test
    @SneakyThrows
    void profileMy() {
        mvc.perform(post("/api/profile/my")
                .contentType(MediaType.APPLICATION_JSON)
                .content(oM.writeValueAsString(new MyProfileDto(user.getEmail(), "Test name", null, null, null)))
                .with(user(user)))
                .andDo(print())
                .andExpect(status().isOk());
    }



    @Test
    @SneakyThrows
    void myStatistics() {
        mvc.perform(get("/api/statistic/my")
                .with(user(user)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void allStatistics() {
        mvc.perform(get("/api/statistics/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likesCount", is(2)));
    }

    @Test
    @SneakyThrows
    void addImage() {

        BufferedImage image = new BufferedImage(50,50, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteArrayOutputStream);
        MockMultipartFile firstFile = new MockMultipartFile("data", "filename.png", "image/png", byteArrayOutputStream.toByteArray());

        mvc.perform(multipart("/api/image")
                .file(firstFile)
                .characterEncoding("UTF-8")
                .contentType(MediaType.IMAGE_PNG)
                .with(user(user)))
                .andDo(print())
                .andExpect(status().isOk());
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

    @Test
    @SneakyThrows
    void addComment() {
        mvc.perform(post("/api/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(oM.writeValueAsString(new RequestCommentDto(null , 1, "новый коммент")))
                //.with(authentication(auth)))
                .with(user(user)))
                //.with(user("user2@mail.ru").password("111444")))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @SneakyThrows
    void getTags() {
        mvc.perform(get("/api/tag")
                .param("query" , "testTag"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
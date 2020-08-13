package object.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import object.config.security.MyUserDetails;
import object.dto.request.post.NewPostDto;
import object.dto.request.post.VotesDto;
import object.services.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
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
class ApiPostControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Mock
    private UsersService usersService;

    private MockHttpSession session;

    @Autowired
    private ObjectMapper oM;

    @Autowired
    private WebApplicationContext context;

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
    void getAllPosts() {
        mvc.perform(get("/api/post")
                .param("offset", "0")
                .param("limit", "10")
                .param("mode", "EARLY"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getPostsBySearch() {
        mvc.perform(get("/api/post/search")
                .param("offset", "0")
                .param("limit", "10")
                .param("query", "NEW"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts[0].id",is(1)));
    }

    @Test
    @SneakyThrows
    void getPostById() {
        mvc.perform(get("/api/post/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @SneakyThrows
    void getPostsByDate() {
        mvc.perform(get("/api/post/byDate")
                .param("offset", "0")
                .param("limit", "10")
                .param("date", "2019-07-16"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getPostsByTag() {
        mvc.perform(get("/api/post/byTag")
                .param("offset", "0")
                .param("limit", "10")
                .param("tag", "google"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getPostsModeration() {
        mvc.perform(get("/api/post/moderation")
                .param("offset", "0")
                .param("limit", "10")
                .param("status", "ACCEPTED")
                .with(user(mod)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getMyPosts() {
        mvc.perform(get("/api/post/my")
                .param("offset", "0")
                .param("limit", "10")
                .param("status", "published")
                .with(user(user)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void addPost() {

        NewPostDto dto = new NewPostDto();
        dto.setTimestamp(1590869185L);
        dto.setActive(1);
        dto.setTitle("Test post");
        dto.setText("Тестовый текст для тестирования");
        dto.setTags(new String[]{"test", "jUnit"});

        mvc.perform(post("/api/post")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(user))
                .content(oM.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void update() {
        NewPostDto dto = new NewPostDto();
        dto.setTimestamp(1590869185L);
        dto.setActive(1);
        dto.setTitle("Test post test post");
        dto.setText("Тестовый текст для тестирования");
        dto.setTags(new String[]{"test", "jUnit"});

        mvc.perform(post("/api/post/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(oM.writeValueAsString(dto))
                .with(user(user)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void like() {
        mvc.perform(post("/api/post/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(oM.writeValueAsString(new VotesDto(1)))
                .with(user(user)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void dislike() {
        mvc.perform(post("/api/post/dislike")
                .contentType(MediaType.APPLICATION_JSON)
                .content(oM.writeValueAsString(new VotesDto(2)))
                //.content("{'post_id':2}")
                .with(user(user)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
package object.controllers.api;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(value = {"/delete.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/insert.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(value = {"/delete.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
class ApiPostControllerTest {

    @Autowired
    private MockMvc mvc;

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
                .andExpect(jsonPath("$.posts[0].id",is(2)));
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
                .param("tag", "testTag"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getPostsModeration() {
        mvc.perform(get("/api/post/moderation")
                .param("offset", "0")
                .param("limit", "10")
                .param("status", "NEW"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
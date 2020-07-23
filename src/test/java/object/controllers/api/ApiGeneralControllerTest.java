package object.controllers.api;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
class ApiGeneralControllerTest {

    @Autowired
    private MockMvc mvc;

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
    void profileMy() {
    }



    @Test
    void myStatistics() {
    }

    @Test
    @SneakyThrows
    void allStatistics() {
        mvc.perform(get("/api/statistics/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liceCount", is(1)));
    }

    @Test
    void addImage() {
    }


    @Test
    void moderation() {
    }

    @Test
    void getSettings() {
    }

    @Test
    void setSettings() {
    }

    @Test
    @SneakyThrows
    void addComment() {
        mvc.perform(post("/api/comment")
                //.header("Authentication", "KluchOtBaldi")
                .param("parent_id" , "1")
                .param("post_id", "1")
                .param("text", "new test comment"))
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
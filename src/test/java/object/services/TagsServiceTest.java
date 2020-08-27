package object.services;

import object.dto.response.tag.TagsDto;
import object.repositories.Tag2PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class TagsServiceTest {

    @MockBean
    private Tag2PostRepository tag2PostRepository;

    @MockBean
    private TagsService tagsService;

    @Test
    void getTagByQuery() {
        String tag = "Java";


        Mockito.doReturn(new TagsDto()).when(tagsService).getTagByQuery(tag);
        Mockito.doReturn(new TagsDto()).when(tagsService).getTagByQuery(null);

        TagsDto tagByQuery = tagsService.getTagByQuery(tag);
        assertNotNull(tagByQuery);

        Mockito.verify(tagsService, Mockito.times(1)).getTagByQuery(tag);

    }
}
package object.services;

import object.dto.response.ResultDto;
import object.repositories.PostVotesRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class PostVotesServiceTest {

    @MockBean
    private PostVotesService postVotesService;

    @MockBean
    private PostVotesRepository postVotesRepository;

    @Test
    void like() {
        int like = 1;
        Mockito.doReturn(new ResultDto()).when(postVotesService).like(like);

        ResultDto resultDto = postVotesService.like(like);
        Mockito.verify(postVotesService,Mockito.times(1)).like(like);
    }

    @Test
    void disLike() {
        int dislike = -1;
        Mockito.doReturn(new ResultDto()).when(postVotesService).disLike(dislike);

        ResultDto resultDto = postVotesService.like(dislike);
        Mockito.verify(postVotesService,Mockito.times(1)).like(dislike);
    }
}
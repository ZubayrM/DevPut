package object.services;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import object.dto.response.ResultDto;
import object.model.PostVotes;
import object.model.Posts;
import object.model.Users;
import object.repositories.PostVotesRepository;
import object.repositories.PostsRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@AllArgsConstructor
public class PostVotesService {

    private PostVotesRepository postVotesRepository;
    private PostsRepository postsRepository;
    private UsersService usersService;


    public ResultDto like(Integer postId) {
        return getResultVotes(postId, 1);
    }

    public ResultDto disLike(Integer postId ) {
        return getResultVotes(postId, -1);
    }

    private ResultDto getResultVotes(Integer postId, Integer value) {

        Optional<Posts> post = postsRepository.findById(postId);

        Users user = usersService.getUser();

        if (post.isPresent()) {
            Optional<List<PostVotes>> listPostVotes = postVotesRepository.getByPostIdAndUserId(post.get(), user.getId());
            if (!listPostVotes.isPresent()) {
                savePostVotes(value, post.get(), user);
                return new ResultDto(true);
            } else {
                PostVotes postVotes = listPostVotes.get().get(0);
                if (!postVotes.getValue().equals(0) && !postVotes.getValue().equals(value)) return new ResultDto(false);
                else if (postVotes.getValue().equals(value)) postVotes.setValue(0);
                else postVotes.setValue(value);
                postVotesRepository.save(postVotes);
                return new ResultDto(true);
            }
        }
        return new ResultDto(false);
    }

    private void savePostVotes(Integer value, Posts post, Users user) {
        PostVotes pVotes = new PostVotes();
        pVotes.setPost(post);
        pVotes.setUserId(user.getId());
        pVotes.setTime(new Date());
        pVotes.setValue(value);
        postVotesRepository.save(pVotes);
    }


}

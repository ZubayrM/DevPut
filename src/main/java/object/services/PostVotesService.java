package object.services;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import object.dto.response.ResultDto;
import object.dto.response.post.ListPostResponseDto;
import object.dto.response.post.PostAllCommentsAndAllTagsDto;
import object.dto.response.post.PostLDCVDto;
import object.model.PostVotes;
import object.model.Posts;
import object.model.Users;
import object.repositories.PostVotesRepository;
import object.repositories.PostsRepository;
import object.repositories.UsersRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.awt.print.Pageable;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@AllArgsConstructor
public class PostVotesService {

    private PostVotesRepository postVotesRepository;
    private PostsRepository postsRepository;
    private UsersRepository usersRepository;
    private UsersService usersService;

    public ListPostResponseDto getCountVotes(ListPostResponseDto<PostLDCVDto> dto) {
        dto.getPosts().stream().forEach(responseDto -> {
            responseDto.setLikeCount(postVotesRepository.countByPostIdAndValue(responseDto.getId(), 1));
            responseDto.setDislikeCount(postVotesRepository.countByPostIdAndValue(responseDto.getId(), -1));
        });
        return dto;
    }

    public PostAllCommentsAndAllTagsDto getCountVotes(PostAllCommentsAndAllTagsDto dto) {

        dto.setLikeCount(postVotesRepository.countByPostIdAndValue(dto.getId(), 1));
        dto.setDislikeCount(postVotesRepository.countByPostIdAndValue(dto.getId(), -1));

        return dto;
    }

    public ResultDto like(Integer postId, HttpServletRequest request) {
        return getResultVotes(postId, request, 1);
    }

    public ResultDto disLike(Integer postId, HttpServletRequest request) {
        return getResultVotes(postId, request, -1);
    }

    private ResultDto getResultVotes(Integer postId, HttpServletRequest request, Integer value) {

        Optional<Posts> post = postsRepository.findById(postId);

        Users user = usersService.getUser();

        if (post.isPresent()) {
            Optional<List<PostVotes>> listPostVotes = postVotesRepository.getByPostIdAndUserId(post.get(), user.getId());
            if (!listPostVotes.isPresent()) {
                return createPostVotes(value, post.get(), user);
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

    private ResultDto createPostVotes(Integer value, Posts post, Users user) {
        PostVotes pVotes = new PostVotes();
        pVotes.setPost(post);
        pVotes.setUserId(user.getId());
        pVotes.setTime(new Date());
        pVotes.setValue(value);
        postVotesRepository.save(pVotes);
        return new ResultDto(true);
    }


}

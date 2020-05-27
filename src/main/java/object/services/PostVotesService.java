package object.services;

import object.dto.response.ResultDto;
import object.dto.response.post.PostAllCommentsAndAllTagsDto;
import object.dto.response.post.ListPostResponseDto;
import object.dto.response.post.PostLDCVDto;
import object.model.PostVotes;
import object.model.Posts;
import object.model.Users;
import object.repositories.PostVotesRepository;
import object.repositories.PostsRepository;
import object.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class PostVotesService {
    @Autowired
    private PostVotesRepository postVotesRepository;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private UsersRepository usersRepository;

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
        String userEmail = request.getHeader("авада кедавра");
        Optional<Users> user = usersRepository.findByEmail(userEmail);
        if (post.isPresent() && user.isPresent()){
            Optional<PostVotes> postVotes = postVotesRepository.findByPostAndUserId(post.get(), user.get().getId());
            if (!postVotes.isPresent()){
                PostVotes pVotes = new PostVotes();
                pVotes.setPost(post.get());
                pVotes.setUserId(user.get().getId());
                pVotes.setValue(value);
                postVotesRepository.save(pVotes);
                return new ResultDto(true);
            } else if (!postVotes.get().getValue().equals(0) && !postVotes.get().getValue().equals(value)) return new ResultDto(false);
            else if (postVotes.get().getValue().equals(value)){
                postVotes.get().setValue(0);
            }
            else postVotes.get().setValue(value);
            postVotesRepository.save(postVotes.get());
            return new ResultDto(false);
        }
        return new ResultDto(false);
    }


}

package object.services;

import object.dto.response.PostAllCommentsAndAllTagsDto;
import object.dto.response.PostLDCVDto;
import object.model.Tags;
import object.repositories.PostsRepository;
import object.dto.response.ListPostResponseDto;
import object.dto.response.UserResponseDto;
import object.model.Posts;
import object.model.enums.Mode;
import object.model.enums.ModerationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostsService<T> {


    @Autowired
    private PostsRepository postsRepository;

    public ListPostResponseDto getListPostResponseDtoByMode(Integer offset, Integer limit, Mode mode, Integer isActive){
        return createListPostResponseDto(getPostsByMode(offset,limit,mode,isActive));
    }

    public ListPostResponseDto getListPostResponseDtoBySearch(Integer offset, Integer limit, String query){
        Optional<List<Posts>> optionalPostsList = postsRepository.findAllByIsActiveAndModerationStatusAndTimeBeforeAndTitleContaining(
                1,
                ModerationStatus.ACCEPTED,
                new Date(),
                query,
                PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "time")));
        return optionalPostsList
                .map(this::createListPostResponseDto)
                .orElseGet(() -> createListPostResponseDto(getPostsByMode(offset, limit, Mode.EARLY, 1)));
    }

    public PostAllCommentsAndAllTagsDto getPostAllCommentsAndAllTagsDto(Integer id) {
        Posts post = postsRepository.findById(id).get();
        PostAllCommentsAndAllTagsDto dto =(PostAllCommentsAndAllTagsDto) createPostDto(post);
        dto.setComments(post.getPostCommentsList());
        dto.setTags(post.getTagList().stream().map(Tags::getName).collect(Collectors.toList()));
        return dto;
    }

    public ListPostResponseDto getListPostResponseDtoByDate(Integer offset, Integer limit, Date date) {
        return createListPostResponseDto(postsRepository.findAllByIsActiveAndModerationStatusAndTime(
                1,
                ModerationStatus.ACCEPTED,
                date,
                PageRequest.of(offset,limit, Sort.by(Sort.Direction.DESC, "time")))
        );

    }

    public ListPostResponseDto getListPostResponseDtoByTag(Integer offset, Integer limit, String tag) {
        Optional<List<Posts>> optionalTags = postsRepository.findAllByIsActiveAndModerationStatusAndTimeBeforeAndTagListContaining(
                1,
                ModerationStatus.ACCEPTED,
                new Date(),
                tag,
                PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "time")));

        return optionalTags
                .map(this::createListPostResponseDto)
                .orElseGet(() -> createListPostResponseDto(getPostsByMode(offset, limit, Mode.EARLY, 1)));
    }

    ////////////////////////// private methods //////////////////////////////////////////////////

    private PostLDCVDto createPostDto (Posts post){
        PostLDCVDto dto = new  PostLDCVDto();
        dto.setId(post.getId());
        dto.setTime(post.getTime());
        dto.setUserResponseDto(new UserResponseDto(post.getAuthor().getId(), post.getAuthor().getName()));
        dto.setTitle(post.getTitle());
        dto.setAnnounce(post.getText().substring(0, 15));
        dto.setViewCount(post.getViewCount());
        dto.setLikeCount((int)post.getPostVotesList().stream().filter(votes -> votes.getValue() > 0).count());
        dto.setDislikeCount((int)post.getPostVotesList().stream().filter(votes -> votes.getValue() < 0).count());
        dto.setCommentCount(post.getPostCommentsList().size());
        return dto;
    }

    private ListPostResponseDto createListPostResponseDto (List<Posts> posts){
        List<PostLDCVDto> listResponseDto = new ArrayList<>();
        for (Posts post : posts){
            listResponseDto.add(createPostDto(post));
        }
        return new ListPostResponseDto((int)postsRepository.count(), listResponseDto);
    }

    private List<Posts> getPostsByMode(Integer offset, Integer limit, Mode mode, Integer isActive){
        List<Posts> posts;
        Sort sort = null;
        switch (mode){
            case BEST: sort = Sort.by(Sort.Direction.DESC, "postVotesList");
                break;
            case EARLY: sort = Sort.by(Sort.Direction.ASC, "time");
                break;
            case RECENT: sort = Sort.by(Sort.Direction.DESC, "time");
                break;
            case POPULAR:  sort = Sort.by(Sort.Direction.DESC, "postCommentsList");
                break;
        }
        posts = postsRepository.findAllByIsActiveAndModerationStatusAndTimeBefore(
                isActive,
                ModerationStatus.ACCEPTED,
                new Date(),
                PageRequest.of(offset, limit));
        return posts;
    }

}
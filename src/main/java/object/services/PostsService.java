package object.services;

import object.dto.response.*;
import object.dto.response.post.ListPostResponseDto;
import object.dto.response.post.PostAllCommentsAndAllTagsDto;
import object.dto.response.post.PostDto;
import object.dto.response.post.PostLDCVDto;
import object.model.Tags;
import object.repositories.PostsRepository;
import object.model.Posts;
import object.model.enums.Mode;
import object.model.enums.ModerationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostsService<T> {


    @Autowired
    private PostsRepository postsRepository;

    public ListPostResponseDto<PostLDCVDto> getListPostResponseDtoByMode(Integer offset, Integer limit, Mode mode){
        return createListPostResponseDto(getPostsByMode(offset,limit,mode));
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
                .orElseGet(() -> createListPostResponseDto(getPostsByMode(offset, limit, Mode.EARLY)));
    }

    public PostAllCommentsAndAllTagsDto getPostAllCommentsAndAllTagsDto(Integer id) {
        Posts post = postsRepository.findById(id).get();
        PostAllCommentsAndAllTagsDto dto = createPostAllCommentsAndAllTagsDto(post);
        dto.setComments(post.getPostCommentsList());
        dto.setTags(post.getTagList().stream().map(Tags::getName).collect(Collectors.toList()));
        return dto;
    }

    public ListPostResponseDto<PostLDCVDto> getListPostResponseDtoByDate(Integer offset, Integer limit, String date) {

        long d = Long.parseLong(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(d);
        Date newDate = calendar.getTime();

        return createListPostResponseDto(postsRepository.findAllByIsActiveAndModerationStatusAndTime(
                1,
                ModerationStatus.ACCEPTED,
                newDate,
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
                .orElseGet(() -> createListPostResponseDto(getPostsByMode(offset, limit, Mode.EARLY)));
    }

    public ListPostResponseDto<PostDto> getPostDtoModeration(Integer offset, Integer limit, ModerationStatus status) {
        Optional<List<Posts>> listPostModeration = postsRepository
                .findAllByIsActiveAndModerationStatus(
                        1,
                        status,
                        PageRequest.of(offset, limit, Sort.by(Sort.Direction.ASC, "time")));
        return listPostModeration
                .map(this::createListPostDto).get();
    }



    ////////////////////////// private methods //////////////////////////////////////////////////

    private PostDto createPostDto(Posts post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTime(post.getTime());
        dto.setUserResponseDto(new UserResponseDto(post.getAuthor().getId(), post.getAuthor().getName()));
        dto.setTitle(post.getTitle());
        dto.setAnnounce(post.getText().substring(0, 15));
        return dto;
    }

    private ListPostResponseDto<PostDto> createListPostDto(List<Posts> posts) {
        List<PostDto> postsList = new ArrayList<>();
        for (Posts post: posts) {
            postsList.add(createPostDto(post));
        }
        return new ListPostResponseDto<>(postsList.size(), postsList);
    }

    private PostLDCVDto createPostLDCVDto(Posts post){
        PostLDCVDto dto = new PostLDCVDto();
        dto.setId(post.getId());
        dto.setTime(post.getTime());
        dto.setUserResponseDto(new UserResponseDto(post.getAuthor().getId(), post.getAuthor().getName()));
        dto.setTitle(post.getTitle());
        dto.setAnnounce(post.getText().substring(0, 3));
        dto.setViewCount(post.getViewCount());
        dto.setLikeCount((int)post.getPostVotesList().stream().filter(votes -> votes.getValue() > 0).count());
        dto.setDislikeCount((int)post.getPostVotesList().stream().filter(votes -> votes.getValue() < 0).count());
        dto.setCommentCount(post.getPostCommentsList().size());
        return dto;
    }

    private PostAllCommentsAndAllTagsDto createPostAllCommentsAndAllTagsDto(Posts post){
        PostAllCommentsAndAllTagsDto dto = new PostAllCommentsAndAllTagsDto();
        dto.setId(post.getId());
        dto.setTime(post.getTime());
        dto.setUserResponseDto(new UserResponseDto(post.getAuthor().getId(), post.getAuthor().getName()));
        dto.setTitle(post.getTitle());
        dto.setAnnounce(post.getText().substring(0, 3));
        dto.setViewCount(post.getViewCount());
        dto.setLikeCount((int)post.getPostVotesList().stream().filter(votes -> votes.getValue() > 0).count());
        dto.setDislikeCount((int)post.getPostVotesList().stream().filter(votes -> votes.getValue() < 0).count());
        dto.setCommentCount(post.getPostCommentsList().size());
        dto.setComments(post.getPostCommentsList());
        dto.setTags(post.getTagList().stream().map(Tags::getName).collect(Collectors.toList()));
        return dto;
    }

    private ListPostResponseDto<PostLDCVDto> createListPostResponseDto (List<Posts> posts){
        List<PostLDCVDto> listResponseDto = new ArrayList<>();
        for (Posts post : posts){
            listResponseDto.add(createPostLDCVDto(post));
        }
        return new ListPostResponseDto<>(listResponseDto.size(), listResponseDto);
    }

    private List<Posts> getPostsByMode(Integer offset, Integer limit, Mode mode){
        List<Posts> posts;
        Sort sort = null;
        switch (mode){
            case BEST: sort = Sort.by(Sort.Direction.DESC, "time"); //"postVotesList");
                break;
            case EARLY: sort = Sort.by(Sort.Direction.ASC, "time");
                break;
            case RECENT: sort = Sort.by(Sort.Direction.DESC, "time");
                break;
            case POPULAR:  sort = Sort.by(Sort.Direction.DESC,"time");//"postCommentsList");
                break;
        }
        posts = postsRepository.findAll(PageRequest.of(offset, limit, sort));
        return posts;
    }

}
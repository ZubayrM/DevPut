package object.services;

import lombok.AllArgsConstructor;
import object.dto.response.PostAllCommentsAndAllTagsDto;
import object.dto.response.PostLDCVDto;
import object.model.Tags;
import object.repositories.PostsRepository;
import object.dto.response.ListPostResponseDto;
import object.dto.response.UserResponseDto;
import object.model.Posts;
import object.model.enums.Mode;
import object.model.enums.ModerationStatus;
import object.repositories.TagsRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class PostsService {


    private PostsRepository postsRepository;
    private TagsRepository tagsRepository;

    public ListPostResponseDto getListPost(Integer offset, Integer limit, Mode mode, Integer isActive){
        return createListPostResponseDto(getPostsByMode(offset,limit,mode,isActive));
    }

    public ListPostResponseDto getListPost(Integer offset, Integer limit, String query){
        return createListPostResponseDto(getPostsByQuery(offset, limit, query, 1));
    }

    private ListPostResponseDto createListPostResponseDto (List<Posts> posts){
        List<PostLDCVDto> listResponseDto = new ArrayList<>();
        for (Posts post : posts){

            PostLDCVDto response = new  PostLDCVDto();
            response.setId(post.getId());
            response.setTime(post.getTime());
            response.setTitle(post.getTitle());
            response.setAnnounce(post.getText().substring(0, 15) + "...");// от балды беру первые слова
            response.setUserResponseDto(
                    new UserResponseDto(
                            post.getAuthor().getId(),
                            post.getAuthor().getName()));
            listResponseDto.add(response);
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
                PageRequest.of(offset, limit, sort));
        return posts;
    }

    private List<Posts> getPostsByQuery(Integer offset, Integer limit, String query, Integer isActive){

        Tags tags = tagsRepository.findByName(query).orElse(null);

        return tags != null ?
                postsRepository.findAllByIsActiveAndModerationStatusAndTimeBeforeAndTagList(
                        isActive,
                        ModerationStatus.ACCEPTED,
                        new Date(),
                        query,
                        PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "time"))):

                postsRepository.findAllByIsActiveAndModerationStatusAndTimeBefore(
                        isActive,
                        ModerationStatus.ACCEPTED,
                        new Date(),
                        PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "time")));
    }

    public PostAllCommentsAndAllTagsDto getPostById(Integer id) {
        Posts post = postsRepository.findById(id).get();
        PostAllCommentsAndAllTagsDto dto = new PostAllCommentsAndAllTagsDto();
        dto.setId(post.getId());
        dto.setTime(post.getTime());
        dto.setUserResponseDto(new UserResponseDto(post.getAuthor().getId(), post.getAuthor().getName()));
        dto.setTitle(post.getTitle());
        dto.setAnnounce(post.getText().substring(0, 15));
        dto.setViewCount(post.getViewCount());

        return dto;
    }
}
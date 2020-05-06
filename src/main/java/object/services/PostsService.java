package object.services;

import object.dto.response.PostLDCVDto;
import object.repositories.PostsRepository;
import object.dto.response.ListPostResponseDto;
import object.dto.response.UserResponseDto;
import object.model.Posts;
import object.model.enums.Mode;
import object.model.enums.ModerationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PostsService {

    @Autowired
    private PostsRepository postsRepository;

    public ListPostResponseDto getListPost(Integer offset, Integer limit, Mode mode){
        return createListPostResponseDto(getPostsByMode(offset,limit,mode));
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

    private List<Posts> getPostsByMode(Integer offset, Integer limit, Mode mode){
        List<Posts> posts;

        Pageable pageable = null;
        switch (mode){
            case BEST: pageable = PageRequest.of(offset, limit, Sort.by(Sort.Direction.ASC, "postVotesList"));
                break;
            case EARLY: pageable = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC));
                break;
            case RECENT: pageable = PageRequest.of(offset, limit, Sort.by(Sort.Direction.ASC));
                break;
            case POPULAR:  pageable = PageRequest.of(offset, limit, Sort.by(Sort.Direction.ASC, "postCommentsList"));
                break;
        }
        posts = postsRepository.findByIsActiveAndModerationStatusAndTimeBefore(1,ModerationStatus.ACCEPTED, new Date(), pageable);
        return posts;
    }
}
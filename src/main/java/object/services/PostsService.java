package object.services;

import object.Repositories.PostsRepository;
import object.dto.respose.ListPostResponseDto;
import object.dto.respose.PostDto;
import object.dto.respose.PostLDCVDto;
import object.dto.respose.UserDto;
import object.model.Posts;
import object.model.enums.Mode;
import object.model.enums.ModerationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostsService {

    @Autowired
    PostsRepository postsRepository;

    public ListPostResponseDto getListPost(Mode mode){

        List<Posts> postsList = postsRepository.findByMode(mode,1, ModerationStatus.ACCEPTED);
        PostDto postLDCVDto;
        ListPostResponseDto listPostResponseDto = new ListPostResponseDto();

        for (Posts request: postsList){
            postLDCVDto = new PostDto(request.getId(), request.getTime(), new UserDto(),request.getTitle(), request.getTitle());
            listPostResponseDto.getPosts().add(postLDCVDto);
        }

        return ListPostResponseDto.builder()
                .count((int)postsRepository.count())
                .build();
    }

    public Integer countPosts(){
        return (int) postsRepository.count();
    }

}

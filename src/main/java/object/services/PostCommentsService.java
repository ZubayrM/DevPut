package object.services;

import object.dto.response.ListPostResponseDto;
import object.repositories.PostCommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostCommentsService {

    @Autowired
    private PostCommentsRepository postCommentsRepository;

    public ListPostResponseDto getCountComment(ListPostResponseDto dto){
        dto.getPosts().forEach(post-> {
            post.setCommentCount(postCommentsRepository.countByPostId(post.getId()));
        });
        return dto;
    }

}

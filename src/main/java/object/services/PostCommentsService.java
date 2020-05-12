package object.services;

import lombok.AllArgsConstructor;
import object.dto.response.ListPostResponseDto;
import object.dto.response.PostAllCommentsAndAllTagsDto;
import object.dto.response.PostLDCVDto;
import object.repositories.PostCommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PostCommentsService {

    private PostCommentsRepository postCommentsRepository;

    public ListPostResponseDto getCountComment(ListPostResponseDto<PostLDCVDto> dto){
        dto.getPosts().forEach(post-> {
            post.setCommentCount(postCommentsRepository.countByPostId(post.getId()));
        });
        return dto;
    }

    public PostAllCommentsAndAllTagsDto getAllComment(PostAllCommentsAndAllTagsDto dto){

        dto.setCommentCount(postCommentsRepository.countByPostId(dto.getId()));

        dto.setComments(postCommentsRepository.findAllByPostId(dto.getId()));

        return dto;
    }

}

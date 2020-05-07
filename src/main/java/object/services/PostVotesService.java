package object.services;

import object.dto.response.PostAllCommentsAndAllTagsDto;
import object.dto.response.ListPostResponseDto;
import object.repositories.PostVotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostVotesService {
    @Autowired
    private PostVotesRepository postVotesRepository;

    public ListPostResponseDto getCountVotes(ListPostResponseDto dto) {
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
}

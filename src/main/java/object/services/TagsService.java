package object.services;

import lombok.AllArgsConstructor;
import object.dto.response.tag.TagDto;
import object.dto.response.tag.TagsDto;
import object.model.Tag;
import object.repositories.Tag2PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TagsService {

    private Tag2PostRepository tag2PostRepository;



    public TagsDto getTagByQuery(String query) {
        TagsDto dto = new TagsDto();
        List<Object[]> list;

        if (query != null) {
            list = tag2PostRepository.getAllByQuery(query);
        } else {
            list = tag2PostRepository.getAll();
        }
        list.forEach(t ->  dto.getTags().add(new TagDto(String.valueOf(t[0]), Double.valueOf(String.valueOf(t[1])))));
        return dto;
    }

    @Deprecated
    private Double getWight(Tag tag) {
        Integer countTag = tag2PostRepository.countByTag(tag.getId());
        Integer countPost = tag2PostRepository.countTag2Post();

        return  (countTag.doubleValue() / countPost.doubleValue()) * 2;
    }
}

package object.services;

import lombok.AllArgsConstructor;
import object.dto.response.tag.ParamResultDto;
import object.dto.response.tag.TagsDto;
import object.model.Tags;
import object.repositories.PostsRepository;
import object.repositories.Tag2PostRepository;
import object.repositories.TagsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TagsService {

    private TagsRepository tagsRepository;
    private Tag2PostRepository tag2PostRepository;
    private PostsRepository postsRepository;


    public TagsDto getTagByQuery(String query) {
        TagsDto dto = new TagsDto();
        List<Tags> list;

        if (query != null) {
            list = tagsRepository.findAllByName(query);
        } else {
            list = tagsRepository.findAll();
        }
        for (Tags tag : list) {
            dto.getTags().add(new ParamResultDto(tag.getName(), getWight(tag)));
        }

        return dto;

    }

    private Double getWight(Tags tag) {
//        Double countTag = tag2PostRepository.countByTag(tag.getId()).doubleValue(); // count не работает
        Double countTag =(double) tag2PostRepository.countByTag(tag.getId()).size(); // пока так
        Integer countPost = postsRepository.getAllPosts().size();

        return  (countTag / countPost.doubleValue()) * 2;
    }
}

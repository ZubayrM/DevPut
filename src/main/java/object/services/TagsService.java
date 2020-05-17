package object.services;

import lombok.AllArgsConstructor;
import object.dto.response.post.PostAllCommentsAndAllTagsDto;
import object.model.Tag2Post;
import object.model.Tags;
import object.repositories.Tag2PostRepository;
import object.repositories.TagsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TagsService {

    private TagsRepository tagsRepository;
    private Tag2PostRepository tag2PostRepository;

    public PostAllCommentsAndAllTagsDto getAllTags(PostAllCommentsAndAllTagsDto dto) {
        List<Tag2Post> tagIds = tag2PostRepository.findAllByPostId(dto.getId());

        List<Integer> ids = new ArrayList<>();

        tagIds.forEach(tag -> ids.add(tag.getTagId()));

        List<Tags> list = (List) tagsRepository.findAllById(ids);

        List<String> tags = new ArrayList<>();

        list.forEach(t-> tags.add(t.getName()));

        dto.setTags(tags);

        return dto;
    }
}

package object.services;

import lombok.SneakyThrows;
import object.dto.response.UserResponseDto;
import object.dto.response.post.*;
import object.dto.response.resultPostComment.ErrorCommentDto;
import object.dto.response.resultPostComment.OkCommentDto;
import object.dto.response.resultPostComment.ResultPostCommentDto;
import object.dto.response.resultPost.ErrorPostDto;
import object.dto.response.resultPost.OkPostDto;
import object.dto.response.resultPost.ParamError;
import object.dto.response.resultPost.ResultPostDto;
import object.model.PostComments;
import object.model.Posts;
import object.model.Tags;
import object.model.enums.Mode;
import object.model.enums.ModerationStatus;
import object.repositories.PostCommentsRepository;
import object.repositories.PostsRepository;
import object.repositories.TagsRepository;
import object.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostsService<T> {

    @Autowired
    private PostCommentsRepository postCommentsRepository;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private TagsRepository tagsRepository;

    @Autowired
    private UsersRepository usersRepository;

    private static final SimpleDateFormat TIME_NEW_POST = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat YEAR = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat MONTH = new SimpleDateFormat("MM");
    private static final SimpleDateFormat DAY = new SimpleDateFormat("dd");

    public ListPostResponseDto<PostLDCVDto> getListPostResponseDtoByMode(Integer offset, Integer limit, Mode mode){
        return createListPostResponseDto(getPostsByMode(offset,limit,mode));
    }

    public ListPostResponseDto getListPostResponseDtoBySearch(Integer offset, Integer limit, String query){
        Optional<List<Posts>> optionalPostsList = postsRepository.findBySearch(
                query,
                PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "time"))
        );

        return optionalPostsList
                .map(this::createListPostResponseDto)
                .orElseGet(() -> createListPostResponseDto(getPostsByMode(offset, limit, Mode.EARLY)));
    }

    public PostAllCommentsAndAllTagsDto getPostAllCommentsAndAllTagsDto(Integer id) {
        Posts post = postsRepository.findById(id).get();
        return createPostAllCommentsAndAllTagsDto(post);
    }


    public ListPostResponseDto<PostLDCVDto> getListPostResponseDtoByDate(Integer offset, Integer limit, String date) {
        return createListPostResponseDto(postsRepository.findByDate(
                getTime(YEAR, date),
                getTime(MONTH, date),
                getTime(DAY, date),
                PageRequest.of(offset,limit, Sort.by(Sort.Direction.DESC, "time")))
        );

    }


    public ListPostResponseDto getListPostResponseDtoByTag(Integer offset, Integer limit, String tag) {
        Optional<List<Posts>> optionalTags = postsRepository.findByTag(
                tag,
                PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "time")));

        return createListPostResponseDto(optionalTags.get()
                .stream()
                .filter(o-> o.getTagList().contains(tagsRepository.findByName(tag).get()))
                .collect(Collectors.toList()));
//        return createListPostResponseDto(optionalTags.get().stream().filter(posts -> posts.getTagList().forEach(tags -> tags.getName().equals(tag))).collect(Collectors.toList()));//не пошло(
//        return optionalTags
//                .map(this::createListPostResponseDto)
//                .orElseGet(() -> createListPostResponseDto(getPostsByMode(offset, limit, Mode.EARLY)));
    }

    public ListPostResponseDto<PostDto> getPostDtoModeration(Integer offset, Integer limit, ModerationStatus status) {
        Optional<List<Posts>> listPostModeration = postsRepository
                .findByModerationStatus(
                        status,
                        PageRequest.of(offset, limit, Sort.by(Sort.Direction.ASC, "time")));
        return listPostModeration
                .map(this::createListPostDto).get();
    }

    public ListPostResponseDto getMyListPost(Integer offset, Integer limit, String status) {
        Integer active;
        ModerationStatus moderationStatus;

        switch (status){
            case "inactive": active = 0; moderationStatus = ModerationStatus.NEW;
            break;
            case "pending": active = 1; moderationStatus = ModerationStatus.NEW;
            break;
            case "declined": active = 1; moderationStatus = ModerationStatus.DECLINED;
            break;
            default: //published
                active = 1; moderationStatus = ModerationStatus.ACCEPTED;
        }
        Optional<List<Posts>> postsList = postsRepository
                .findAllMyPosts(active, moderationStatus, PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "time")));
        return postsList.map(this::createListPostResponseDto).get();

    }

    @SneakyThrows
    public ResultPostDto addPost(String time, Integer active, String title, String text, String tags) {
        Posts post = new Posts();
        post.setAuthor(usersRepository.findById(1)); // временно
        post.setTime(TIME_NEW_POST.parse(time));
        post.setIsActive(active);
        post.setTitle(title);
        post.setText(text);
        post.setTagList(generateTagList(tags));
        post.setModerationStatus(ModerationStatus.NEW);
        post.setViewCount(0);
        Posts result = postsRepository.save(post);
        if (result != null) return new OkPostDto();
        else return new ErrorPostDto();
    }

    @SneakyThrows
    public ResultPostDto update(String time, Integer active, String title, String text, String tags, Integer id) {
        if (title.length() > 15 && text.length() > 15 ) {
            Posts post = postsRepository.findById(id).get();
            post.setTime(TIME_NEW_POST.parse(time));
            post.setIsActive(active);
            post.setTitle(title);
            post.setText(text);
            post.setTagList(generateTagList(tags));
            postsRepository.save(post);
            return new OkPostDto();
        } else {
            ParamError error = new ParamError(title.length() < 15 ? "Заголовок слишком короткий" : "",
                    text.length() < 15 ? "Текст публикации слишком короткий" : "");
            ErrorPostDto dto = new ErrorPostDto();
            dto.setErrors(error);
            return dto;
    }

    }

    public ResultPostCommentDto addComment(Integer parentId, Integer postId, String text) {
        if (parentId != null && postId != null && text.length() < 1){
            PostComments p = new PostComments();
            p.setParentId(parentId);
            p.setPost(postsRepository.findById(parentId).get());
            p.setText(text);
            p.setTime(new Date());

            PostComments res = postCommentsRepository.save(p);
            return new OkCommentDto(res.getId());
        } else if (parentId == null && postId != null && text.length() < 1){
            PostComments p = new PostComments();
            p.setPost(postsRepository.findById(parentId).get());
            p.setText(text);
            p.setTime(new Date());

            PostComments res =postCommentsRepository.save(p);
            return new OkCommentDto(res.getId());
        } else return new  ErrorCommentDto();

    }

    public void moderationPost(Integer postId, ModerationStatus status, Integer moderatorId) {
        Posts post = postsRepository.findById(postId).get();
        post.setModerationId(moderatorId);
        post.setModerationStatus(status);
        postsRepository.save(post);
    }


    @SneakyThrows
    public CalendarDto getCalendar(String year) {
        return generateCalendarDto(postsRepository.getAllPosts(), year);
    }


    ////////////////////////// private methods //////////////////////////////////////////////////

    private PostDto createPostDto(Posts post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTime(createTime(post.getTime()));
        dto.setUser(new UserResponseDto(post.getAuthor().getId(), post.getAuthor().getName()));
        dto.setTitle(post.getTitle());
        dto.setAnnounce(post.getText().substring(0, 15));
        return dto;
    }

    private ListPostResponseDto<PostDto> createListPostDto(List<Posts> posts) {
        List<PostDto> postsList = new ArrayList<>();
        for (Posts post: posts) {
            postsList.add(createPostDto(post));
        }
        return new ListPostResponseDto<>(postsRepository.getAllPosts().size(), postsList);
    }

    private PostLDCVDto createPostLDCVDto(Posts post){
        PostLDCVDto dto = new PostLDCVDto();
        dto.setId(post.getId());
        dto.setTime(createTime(post.getTime()));
        dto.setUser(new UserResponseDto(post.getAuthor().getId(), post.getAuthor().getName()));
        dto.setTitle(post.getTitle());
        dto.setAnnounce(post.getText().substring(0, 15));
        dto.setViewCount(post.getViewCount());
        dto.setLikeCount((int)post.getPostVotesList().stream().filter(votes -> votes.getValue() > 0).count());
        dto.setDislikeCount((int)post.getPostVotesList().stream().filter(votes -> votes.getValue() < 0).count());
        dto.setCommentCount(post.getPostCommentsList().size());
        return dto;
    }

    private PostAllCommentsAndAllTagsDto createPostAllCommentsAndAllTagsDto(Posts post){
        PostAllCommentsAndAllTagsDto dto = new PostAllCommentsAndAllTagsDto();
        dto.setId(post.getId());
        dto.setTime(createTime(post.getTime()));
        dto.setUser(new UserResponseDto(post.getAuthor().getId(), post.getAuthor().getName()));
        dto.setTitle(post.getTitle());
        dto.setAnnounce(post.getText().substring(0, 15));
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
        return new ListPostResponseDto<>(postsRepository.getAllPosts().size(), listResponseDto);
    }

    private List<Posts> getPostsByMode(Integer offset, Integer limit, Mode mode){
        List<Posts> posts;
        Sort sort = null;
        switch (mode){
            case EARLY: sort = Sort.by(Sort.Direction.ASC, "time");
                break;
            case RECENT: sort = Sort.by(Sort.Direction.DESC, "time");
                break;
            default: sort = Sort.by(Sort.Direction.ASC, "id");
        }
        posts = postsRepository.findAll(PageRequest.of(offset, limit, sort));


        if (mode == Mode.BEST){
            posts.sort(Comparator.comparing((p) -> p.getPostVotesList().size()));
        }

        else if (mode == Mode.POPULAR){
            posts.sort(Comparator.comparing((p)-> p.getPostVotesList().size()));
        }
        return posts;
    }

    private String createTime(Date time) {
        Date today = new Date();

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(time);

        Calendar todayCalendar = new GregorianCalendar();
        todayCalendar.setTime(today);

        SimpleDateFormat format;

        if (calendar.get(Calendar.DAY_OF_YEAR) == todayCalendar.get(Calendar.DAY_OF_YEAR) )
            format = new SimpleDateFormat("HH:mm");

        else if (calendar.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH))
            format = new SimpleDateFormat("EEEE HH:mm");

        else
            format = new SimpleDateFormat("dd.MM.yyyy");

        return format.format(time);

    }

    @SneakyThrows
    private Integer getTime(SimpleDateFormat format, String date){
        Date time = formatter.parse(date);
        return Integer.valueOf(format.format(time));
    }

    private Set<Tags> generateTagList(String tags) {
        Set<Tags> tagsSet = new HashSet<>();
        String[] result = tags.split(", ");
        for (String tag: result){
            if (tagsRepository.findByName(tag).isPresent()){
                tagsSet.add(tagsRepository.findByName(tag).get());
            }
            else {
                Tags newTag = new Tags();
                newTag.setName(tag);
                tagsSet.add(tagsRepository.save(newTag));
            }
        }
        return tagsSet;
    }
    private CalendarDto generateCalendarDto(List<Posts> list, String year) {
        CalendarDto dto = new CalendarDto();
        for (Posts p : list){
            String y = YEAR.format(p.getTime()).trim();

            dto.getYears().add(y);

            if (y.equals(year) ){
                if (dto.getPosts().containsKey(y)){
                    dto.getPosts().put(y,  dto.getPosts().get(y) + 1);
                } else
                    dto.getPosts().put(y, 1);
            }
        }
        return dto;
    }



}
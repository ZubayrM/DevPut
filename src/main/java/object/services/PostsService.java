package object.services;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import object.component.ImagePath;
import object.dto.response.*;
import object.dto.response.post.*;
import object.dto.response.resultPost.ErrorPostDto;
import object.dto.response.resultPost.ParamError;
import object.model.*;
import object.model.enums.Mode;
import object.model.enums.ModerationStatus;
import object.repositories.*;
import org.jsoup.Jsoup;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class PostsService<T> {

    private PostsRepository postsRepository;
    private TagsRepository tagsRepository;
    private UsersService usersService;
    private Tag2PostRepository tag2PostRepository;
    private UsersRepository usersRepository;
    private ImagePath imagePath;

    private static final SimpleDateFormat TIME_2_DATE = new SimpleDateFormat("hh:mm, dd.MM.yyyy");
    private static final SimpleDateFormat DATE_2_TIME = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat YEAR = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat MONTH = new SimpleDateFormat("MM");
    private static final SimpleDateFormat DAY = new SimpleDateFormat("dd");

    public ListPostResponseDto<PostFullDto> getListPostResponseDtoByMode(Integer offset, Integer limit, Mode mode){
        return createListPostResponseDto(getPostsByMode(offset,limit,mode));
    }

    public ListPostResponseDto getListPostResponseDtoBySearch(Integer offset, Integer limit, String query){
        Optional<List<Post>> optionalPostsList = postsRepository.findBySearch(
                query,
                PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "time"))
        );
        return optionalPostsList
                .map(this::createListPostResponseDto)
                .orElseGet(() -> createListPostResponseDto(getPostsByMode(offset, limit, Mode.EARLY)));
    }

    public PostAllCommentsAndAllTagsDto getPostAllCommentsAndAllTagsDto(Integer id) {
        Post post = postsRepository.findById(id).orElse(null);
        if (post != null) {
            post.setViewCount(post.getViewCount() + 1);
            return createPostAllCommentsAndAllTagsDto(new PostAllCommentsAndAllTagsDto(), postsRepository.save(post), TIME_2_DATE);
        }
        else return null;
    }


    public ListPostResponseDto<PostFullDto> getListPostResponseDtoByDate(Integer offset, Integer limit, String date) {
        return createListPostResponseDto(postsRepository.findByDate(
                getTime(YEAR, date),
                getTime(MONTH, date),
                getTime(DAY, date),
                PageRequest.of(offset,limit, Sort.by(Sort.Direction.DESC, "time")))
        );

    }


    public ListPostResponseDto getListPostResponseDtoByTag(Integer offset, Integer limit, String tag) {
        Optional<List<Post>> optionalTags = postsRepository.findByTag(
                tag,
                PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "time")));

        return createListPostResponseDto(optionalTags.get()
                .stream()
                .filter(o-> o.getTagList().contains(tagsRepository.findByName(tag).get()))
                .collect(Collectors.toList()));
    }

    public ListPostResponseDto<PostAndAuthorDto> getPostDtoModeration(Integer offset, Integer limit, ModerationStatus status) {
        Optional<List<Post>> listPostModeration = postsRepository
                .findByModerationStatus(
                        status,
                        PageRequest.of(offset, limit, Sort.by(Sort.Direction.ASC, "time")));
        return listPostModeration
                .map(this::createListPostDto).orElse(new ListPostResponseDto<>());
    }

    public ListPostResponseDto getMyListPost(Integer offset, Integer limit, String status) {
        int active;
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

        User user = usersService.getUser();
        log.info(user);
        List<Post> allPosts = postsRepository.getAllPosts(active, moderationStatus, user, PageRequest.of(offset, limit));

        return createListPostDto(allPosts);

    }


    @SneakyThrows
    public ResultDto addPost(String time, Integer active, String title, String text, String[] tags) {
        Post post = new Post();
        post.setAuthor(usersService.getUser());
        post.setTime(validDate(DATE_2_TIME.parse(time)));
        post.setIsActive(active);
        post.setTitle(title);
        post.setText(text);
        saveTag(tags);

        if (usersService.getUser().getIsModerator() == 0)
            post.setModerationStatus(ModerationStatus.NEW);
        else {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
            post.setModerationId(usersService.getUser().getId());
        }

        post.setViewCount(0);
        Post result = postsRepository.save(post);

        if (result != null) {
            saveTag(tags);
            saveTag2Post(tags, result);
            return new ResultDto(true);
        }
        else return new ErrorPostDto();
    }

    private void saveTag2Post(String[] tags, Post posts) {
        for (String tag: tags) {
            Tag2Post t2p = new Tag2Post();
            t2p.setTagId(tagsRepository.findByName(tag).get().getId());
            t2p.setPostId(posts.getId());
            tag2PostRepository.save(t2p);
        }
    }


    private Tag saveTag(String tag) {
        Optional<Tag> t = tagsRepository.findByName(tag);
        if (t.isPresent()) return t.get();
        Tag newTag = new Tag();
        newTag.setName(tag);
        return tagsRepository.save(newTag);
    }


    private Set<Tag> saveTag(String[] tags) {
        Set<Tag> tagsSet = new HashSet<>();
        Tag newTag = null;
        for (String tag: tags) {
            newTag = saveTag(tag);
            tagsSet.add(newTag);
        }
        return tagsSet;
    }

    private Date validDate(Date date) {
        Date today = new Date();
        if (date.after(today)) return date;
        else return today;
    }

    @SneakyThrows
    public ResultDto update(String time, Integer active, String title, String text, String[] tags, Integer id) {
        if (title.length() > 15 && text.length() > 15 ) {
            Post post = postsRepository.findById(id).get();
            post.setTime(DATE_2_TIME.parse(time));
            post.setIsActive(active);
            post.setTitle(title);
            post.setText(text);

            for (String tag: tags)
            post.setTagList(generateTagList(tag));

            postsRepository.save(post);
            return new ResultDto(true);
        } else {
            ParamError error = new ParamError(title.length() < 15 ? "Заголовок слишком короткий" : "",
                    text.length() < 15 ? "Текст публикации слишком короткий" : "");
            ErrorPostDto dto = new ErrorPostDto();
            dto.setErrors(error);
            return dto;
        }
    }



    private<T extends PostAndAuthorDto> T createPostDto(T dto, Post p, SimpleDateFormat format){
        dto.setId(p.getId());
        dto.setTime(dateToString(p.getTime(), format));
        dto.setUser(new UserMinDto(p.getAuthor().getId(), p.getAuthor().getName()));
        dto.setTitle(p.getTitle());
        String textPost = Jsoup.parse(p.getText()).text();
        dto.setAnnounce(textPost.length() > 15 ? textPost.substring(0,15) : textPost);
        return dto;
    }

    private ListPostResponseDto<PostAndAuthorDto> createListPostDto(List<Post> posts) {
        List<PostAndAuthorDto> postsList = new ArrayList<>();
        for (Post post: posts) {
            postsList.add(createPostDto(new PostAndAuthorDto(), post, null));
        }
        return new ListPostResponseDto<>(posts.size(), postsList);
    }

    private<T extends PostFullDto> T createPostFullDto(T dto, Post post){
        T newDto = createPostDto(dto, post, null);

        newDto.setViewCount(post.getViewCount());
        newDto.setLikeCount((int)post.getPostVotesList().stream().filter(votes -> votes.getValue() > 0).count());
        newDto.setDislikeCount((int)post.getPostVotesList().stream().filter(votes -> votes.getValue() < 0).count());
        newDto.setCommentCount(post.getPostCommentsList().size());
        return dto;
    }

    private <T extends PostAllCommentsAndAllTagsDto> T createPostAllCommentsAndAllTagsDto(T dto, Post post, SimpleDateFormat format){
        T newDto = createPostFullDto(dto, post);
        List<CommentDto> postCommentsList  = new ArrayList<>();
        for (PostComments pC : post.getPostCommentsList()) {

            User u = usersRepository.findById(pC.getUserId()).get();

            postCommentsList.add(CommentDto.builder()
                    .id(pC.getId())
                    .time(dateToString(pC.getTime(), format))
                    .text(pC.getText())
                    .user(new UserPhotoDto(u.getId(), u.getName(), u.getPhoto()))
                    .build()
            );

        }
        newDto.setComments(postCommentsList);

        List<String> tagsList = (post.getTagList().stream().map(Tag::getName).collect(Collectors.toList()));
        newDto.setTags(tagsList);

        newDto.setText(post.getText());
        return newDto;
    }

    private ListPostResponseDto<PostFullDto> createListPostResponseDto (List<Post> posts){

        List<PostFullDto> listResponseDto = new ArrayList<>();
        for (Post post : posts){
            listResponseDto.add(createPostFullDto(new PostFullDto(),post));
        }
        return new ListPostResponseDto<>(posts.size(), listResponseDto);//
    }

    private List<Post> getPostsByMode(Integer offset, Integer limit, Mode mode){
        List<Post> posts;
        Sort sort = null;
        switch (mode){
            case EARLY: sort = Sort.by(Sort.Direction.ASC, "time");
                break;
            case RECENT: sort = Sort.by(Sort.Direction.DESC, "time");
                break;
            case POPULAR: sort = Sort.by(Sort.Direction.DESC, "postCommentsList.size");
                break;
            default: sort = Sort.by(Sort.Direction.ASC, "time");
        }


        posts = postsRepository.findAll(PageRequest.of(offset/limit, limit, sort));



        posts.stream().filter( p -> p.getPostVotesList().stream().anyMatch(s -> s.getValue() > 0)).count();

        if (mode == Mode.BEST){
            posts = posts.stream().sorted((p1, p2) -> Integer.compare((int) p1.getPostVotesList().stream().filter(s -> s.getValue() > 0).count(), (int) p2.getPostVotesList().stream().filter(s -> s.getValue() > 0).count())).collect(Collectors.toList());
            Collections.reverse(posts);
        }


        log.info(posts.size());

        return posts;
    }


    private String dateToString(Date date, SimpleDateFormat simpleDateFormat){
        LocalDateTime timeTodo = LocalDateTime.now();
        LocalDateTime time = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        String format;

        if (time.getDayOfYear() == timeTodo.getDayOfYear())
            format = "hh:mm";
        else if (time.getDayOfYear() == timeTodo.minusDays(1).getDayOfYear())
            format = "'вчера' hh:mm";
        else if (time.getDayOfYear() >= timeTodo.minusDays(3).getDayOfYear())
            format = "'" + time.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()) + "' hh:mm";
        else format = null;

        log.info(format);

        if (format != null) simpleDateFormat = new SimpleDateFormat(format);
        else if (simpleDateFormat == null) simpleDateFormat = DATE_2_TIME;

        return simpleDateFormat.format(date);
    }

    @SneakyThrows
    private Integer getTime(SimpleDateFormat format, String date){
        Date time = DATE_FORMAT.parse(date);
        return Integer.valueOf(format.format(time));
    }

    @Deprecated
    private Set<Tag> generateTagList(String tags) {
        Set<Tag> tagsSet = new HashSet<>();
        String[] result = tags.split(", ");
        for (String tag: result){
            if (tagsRepository.findByName(tag).isPresent()){
                tagsSet.add(tagsRepository.findByName(tag).get());
            }
            else {
                Tag newTag = new Tag();
                newTag.setName(tag);
                tagsSet.add(tagsRepository.save(newTag));
            }
        }
        return tagsSet;
    }

}
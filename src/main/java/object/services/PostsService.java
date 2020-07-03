package object.services;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import object.dto.response.ResultDto;
import object.dto.response.StatisticsDto;
import object.dto.response.UserResponseDto;
import object.dto.response.post.*;
import object.dto.response.resultPost.ErrorPostDto;
import object.dto.response.resultPost.ParamError;
import object.dto.response.resultPostComment.ErrorCommentDto;
import object.dto.response.resultPostComment.OkCommentDto;
import object.dto.response.resultPostComment.ResultPostCommentDto;
import object.model.*;
import object.model.enums.Mode;
import object.model.enums.ModerationStatus;
import object.repositories.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class PostsService<T> {

    private PostCommentsRepository postCommentsRepository;
    private PostsRepository postsRepository;
    private TagsRepository tagsRepository;
    private UsersRepository usersRepository;
    private PostVotesRepository postVotesRepository;
    private UsersService usersService;
    private Tag2PostRepository tag2PostRepository;


    private static final SimpleDateFormat TIME_NEW_POST = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
    private static final SimpleDateFormat FIRST_PUBLICATION = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
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
        log.info(post.toString() + " получен");
        return createPostAllCommentsAndAllTagsDto(new PostAllCommentsAndAllTagsDto(), post);
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

        Users user = usersService.getUser();

//        Optional<List<Posts>> postsList = postsRepository
//                .getMyPosts( active, moderationStatus, PageRequest.of(offset,limit));
        List<Posts> allPosts = postsRepository.getAllPosts();
        List<Posts> postsList = allPosts.stream()
                .filter(o -> o.getAuthor().getId().compareTo(user.getId()) == 0)
                .filter(o -> o.getIsActive().compareTo(active) == 0)
                .filter(o -> o.getModerationStatus().compareTo(moderationStatus) == 0)
                .collect(Collectors.toList());
        return createListPostDto(postsList);
//        return postsList.map(this::createListPostResponseDto).get();

    }


    @SneakyThrows
    public ResultDto addPost(String time, Integer active, String title, String text, String[] tags) {
        Posts post = new Posts();
        post.setAuthor(usersService.getUser());
        post.setTime(validDate(TIME_NEW_POST.parse(time)));
        post.setIsActive(active);
        post.setTitle(title);
        post.setText(text);
        //post.setTagList(saveTag(tags));
        saveTag(tags);
        post.setModerationStatus(ModerationStatus.NEW);
        post.setViewCount(0);
        Posts result = postsRepository.save(post);

        if (result != null) {
            saveTag(tags);
            saveTag2Post(tags, result);
            return new ResultDto(true);
        }
        else return new ErrorPostDto();
    }

    private void saveTag2Post(String[] tags, Posts posts) {
        for (String tag: tags) {
            Tag2Post t2p = new Tag2Post();
            t2p.setTagId(tagsRepository.findByName(tag).get().getId());
            t2p.setPostId(posts.getId());
            tag2PostRepository.save(t2p);
        }
    }


    private Tags saveTag(String tag) {
        Optional<Tags> t = tagsRepository.findByName(tag);
        if (t.isPresent()) return t.get();
        Tags newTag = new Tags();
        newTag.setName(tag);
        return tagsRepository.save(newTag);
    }


    private Set<Tags> saveTag(String[] tags) {
        Set<Tags> tagsSet = new HashSet<>();
        Tags newTag = null;
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
            Posts post = postsRepository.findById(id).get();
            post.setTime(TIME_NEW_POST.parse(time));
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
        if (year.isEmpty()){
            return generateCalendarDto(postsRepository.getAllPosts(), year);
        }
        else return generateCalendarDto(postsRepository.getAllPosts());
    }


    ////////////////////////// private methods //////////////////////////////////////////////////

//    private PostDto createPostDto(Posts post) {
//        PostDto dto = new PostDto();
//        dto.setId(post.getId());
//        dto.setTime(createTime(post.getTime()));
//        dto.setUser(new UserResponseDto(post.getAuthor().getId(), post.getAuthor().getName()));
//        dto.setTitle(post.getTitle());
//        dto.setAnnounce(post.getText().substring(0, 15));
//        return dto;
//    }

    private<T extends PostDto> T createPostDto(T dto, Posts p){
        dto.setId(p.getId());
        dto.setTime(createTime(p.getTime()));
        dto.setUser(new UserResponseDto(p.getAuthor().getId(), p.getAuthor().getName()));
        dto.setTitle(p.getTitle());
        dto.setAnnounce(p.getText().substring(0, 15));
        return dto;
    }

    private ListPostResponseDto<PostDto> createListPostDto(List<Posts> posts) {
        List<PostDto> postsList = new ArrayList<>();
        for (Posts post: posts) {
            postsList.add(createPostDto(new PostDto(), post));
        }
        return new ListPostResponseDto<>(postsRepository.getAllPosts().size(), postsList);
    }

    private<T extends PostLDCVDto> T createPostLDCVDto(T dto, Posts post){
//        PostLDCVDto dto = new PostLDCVDto();
//        dto.setId(post.getId());
//        dto.setTime(createTime(post.getTime()));
//        dto.setUser(new UserResponseDto(post.getAuthor().getId(), post.getAuthor().getName()));
//        dto.setTitle(post.getTitle());
//        dto.setAnnounce(post.getText().substring(0, 15));
        T newDto = createPostDto(dto, post);

        newDto.setViewCount(post.getViewCount());
        newDto.setLikeCount((int)post.getPostVotesList().stream().filter(votes -> votes.getValue() > 0).count());
        newDto.setDislikeCount((int)post.getPostVotesList().stream().filter(votes -> votes.getValue() < 0).count());
        newDto.setCommentCount(post.getPostCommentsList().size());
        log.info("\n view count " + newDto.getViewCount() +
                "\n like count " + newDto.getLikeCount() +
                "\n dislike count " + newDto.getDislikeCount() +
                "\n comment count " + newDto.getCommentCount());
        return dto;
    }

    private <T extends PostAllCommentsAndAllTagsDto> T createPostAllCommentsAndAllTagsDto(T dto, Posts post){
//        PostAllCommentsAndAllTagsDto dto = new PostAllCommentsAndAllTagsDto();
//        dto.setId(post.getId());
//        dto.setTime(createTime(post.getTime()));
//        dto.setUser(new UserResponseDto(post.getAuthor().getId(), post.getAuthor().getName()));
//        dto.setTitle(post.getTitle());
//        dto.setAnnounce(post.getText().substring(0, 15));
//        dto.setViewCount(post.getViewCount());
//        dto.setLikeCount((int)post.getPostVotesList().stream().filter(votes -> votes.getValue() > 0).count());
//        dto.setDislikeCount((int)post.getPostVotesList().stream().filter(votes -> votes.getValue() < 0).count());
//        dto.setCommentCount(post.getPostCommentsList().size());
        T newDto = createPostLDCVDto(dto, post);

        List<PostComments> postCommentsList = post.getPostCommentsList();
        newDto.setComments(postCommentsList == null ? new ArrayList<PostComments>() : postCommentsList);

        List<String> tagsList = (post.getTagList().stream().map(Tags::getName).collect(Collectors.toList()));
        newDto.setTags(tagsList == null ? new ArrayList<String>() : tagsList);

        log.info("\ncomments:" + newDto.getComments().size() + ", \t tags: " + newDto.getTags().size());
        return newDto;
    }

    private ListPostResponseDto<PostLDCVDto> createListPostResponseDto (List<Posts> posts){

        List<PostLDCVDto> listResponseDto = new ArrayList<>();
        for (Posts post : posts){
            listResponseDto.add(createPostLDCVDto(new PostLDCVDto(),post));
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
        Date time = DATE_FORMAT.parse(date);
        return Integer.valueOf(format.format(time));
    }

    @Deprecated
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
                String time = DATE_FORMAT.format(p.getTime());
                if (dto.getPosts().containsKey(y)){
                    dto.getPosts().put(time,  dto.getPosts().get(y) + 1);
                } else
                    dto.getPosts().put(time, 1);
            }
        }
        return dto;
    }

    private CalendarDto generateCalendarDto(List<Posts> list) {
        CalendarDto dto = new CalendarDto();
        for (Posts p : list){
            String y = YEAR.format(p.getTime()).trim();

            dto.getYears().add(y);


            String time = DATE_FORMAT.format(p.getTime());
            if (dto.getPosts().containsKey(y)){
                dto.getPosts().put(time,  dto.getPosts().get(y) + 1);
            } else
                dto.getPosts().put(time, 1);
        }
        return dto;
    }



    public StatisticsDto myStatistics(HttpServletRequest request) {
        String userEmail = request.getHeader("абра кадабра");

        Users u = usersRepository.findByEmail(userEmail).orElse(null);
        Integer postCount = postsRepository.countByAuthor(u);
        Integer likesCount = postVotesRepository.countByUserIdAndValue(u.getId(), 1);
        Integer dislikesCount = postVotesRepository.countByUserIdAndValue(u.getId(), -1);
        Integer viewsCount = generateViewsCount(postsRepository.findByAuthor(u));
        String firstPublication = FIRST_PUBLICATION.format(postsRepository.findFirstByTimeAndAuthor(new Date(),u).getTime());
        return new StatisticsDto(postCount, likesCount, dislikesCount, viewsCount, firstPublication);
    }

    private Integer generateViewsCount(Optional<List<Posts>> byAuthor) {
        List<Posts> l = byAuthor.get();
        //Integer count = l.stream().s
        return null;
    }

    public StatisticsDto allStatistic() {
        Integer postCount = postsRepository.getAllPosts().size();
        Integer likesCount = postVotesRepository.countByValue(1);
        Integer dislikesCount = postVotesRepository.countByValue(-1);
        Integer viewsCount = postsRepository.countByViewCount();
        Posts p = postsRepository.findFirstByTime();
        String firstPublication = FIRST_PUBLICATION.format(p.getTime());
        return new StatisticsDto(postCount, likesCount, dislikesCount, viewsCount, firstPublication);
    }
}
package object.services;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import object.dto.response.ResultDto;
import object.dto.response.StatisticsDto;
import object.dto.response.UserMinDto;
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
import org.jsoup.Jsoup;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
    private PostVotesRepository postVotesRepository;
    private UsersService usersService;
    private Tag2PostRepository tag2PostRepository;


    private static final SimpleDateFormat TIME_NEW_POST = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
    private static final SimpleDateFormat FIRST_PUBLICATION = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat YEAR = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat MONTH = new SimpleDateFormat("MM");
    private static final SimpleDateFormat DAY = new SimpleDateFormat("dd");

    public ListPostResponseDto<PostFullDto> getListPostResponseDtoByMode(Integer offset, Integer limit, Mode mode){
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
        return createPostAllCommentsAndAllTagsDto(new PostAllCommentsAndAllTagsDto(), post, TIME_NEW_POST);
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
        Optional<List<Posts>> optionalTags = postsRepository.findByTag(
                tag,
                PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "time")));

        return createListPostResponseDto(optionalTags.get()
                .stream()
                .filter(o-> o.getTagList().contains(tagsRepository.findByName(tag).get()))
                .collect(Collectors.toList()));
    }

    public ListPostResponseDto<PostAndAuthorDto> getPostDtoModeration(Integer offset, Integer limit, ModerationStatus status) {
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
        List<Posts> allPosts = postsRepository.getAllPosts(active, moderationStatus, user, PageRequest.of(offset, limit));

        return createListPostDto(allPosts);

    }


    @SneakyThrows
    public ResultDto addPost(String time, Integer active, String title, String text, String[] tags) {
        Posts post = new Posts();
        post.setAuthor(usersService.getUser());
        post.setTime(validDate(FIRST_PUBLICATION.parse(time)));
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
            post.setTime(FIRST_PUBLICATION.parse(time));
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
        PostComments pC = new PostComments();
        pC.setUserId(usersService.getUser().getId());
        pC.setPost(postsRepository.findById(postId).get());
        if (text.length() > 0) pC.setText(text);
        else return new  ErrorCommentDto();
        pC.setTime(new Date());
        pC.setParentId(parentId);
        PostComments result = postCommentsRepository.save(pC);
        return new OkCommentDto(result.getId());
    }

    public void moderationPost(Integer postId, String status) {
        Posts post = postsRepository.findById(postId).get();
        post.setModerationId(usersService.getUser().getId());
        post.setModerationStatus(status.equalsIgnoreCase("ACCEPT") ? ModerationStatus.ACCEPTED : ModerationStatus.DECLINED);
        postsRepository.save(post);
    }


    @SneakyThrows
    public CalendarDto getCalendar(String year) {
        if (year.isEmpty()){
            return generateCalendarDto(postsRepository.getAllPosts(), year);
        }
        else return generateCalendarDto(postsRepository.getAllPosts());
    }



    private<T extends PostAndAuthorDto> T createPostDto(T dto, Posts p, SimpleDateFormat format){
        dto.setId(p.getId());
        dto.setTime(createTime(p.getTime(), format));
        dto.setUser(new UserMinDto(p.getAuthor().getId(), p.getAuthor().getName()));
        dto.setTitle(p.getTitle());
        dto.setAnnounce(Jsoup.parse(p.getText()).text().substring(0,15));
        return dto;
    }

    private ListPostResponseDto<PostAndAuthorDto> createListPostDto(List<Posts> posts) {
        List<PostAndAuthorDto> postsList = new ArrayList<>();
        for (Posts post: posts) {
            postsList.add(createPostDto(new PostAndAuthorDto(), post, null));
        }
        return new ListPostResponseDto<>(postsRepository.getAllPosts().size(), postsList);
    }

    private<T extends PostFullDto> T createPostFullDto(T dto, Posts post){
        T newDto = createPostDto(dto, post, null);

        newDto.setViewCount(post.getViewCount());
        newDto.setLikeCount((int)post.getPostVotesList().stream().filter(votes -> votes.getValue() > 0).count());
        newDto.setDislikeCount((int)post.getPostVotesList().stream().filter(votes -> votes.getValue() < 0).count());
        newDto.setCommentCount(post.getPostCommentsList().size());
        return dto;
    }

    private <T extends PostAllCommentsAndAllTagsDto> T createPostAllCommentsAndAllTagsDto(T dto, Posts post, SimpleDateFormat format){
        T newDto = createPostFullDto(dto, post);

        List<PostComments> postCommentsList = post.getPostCommentsList();
        newDto.setComments(postCommentsList == null ? new ArrayList<>() : postCommentsList);

        List<String> tagsList = (post.getTagList().stream().map(Tags::getName).collect(Collectors.toList()));
        newDto.setTags(tagsList == null ? new ArrayList<>() : tagsList);

        newDto.setText(post.getText());
        newDto.setTime(createTime(post.getTime(), format));
        return newDto;
    }

    private ListPostResponseDto<PostFullDto> createListPostResponseDto (List<Posts> posts){

        List<PostFullDto> listResponseDto = new ArrayList<>();
        for (Posts post : posts){
            listResponseDto.add(createPostFullDto(new PostFullDto(),post));
        }
        return new ListPostResponseDto<>(postsRepository.countPosts(), listResponseDto);//
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

    private String createTime(Date time, SimpleDateFormat timeFormat) {
        Date today = new Date();

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(time);

        Calendar todayCalendar = new GregorianCalendar();
        todayCalendar.setTime(today);

        if (timeFormat == null) {
            String format;
            if (calendar.get(Calendar.DAY_OF_YEAR) == todayCalendar.get(Calendar.DAY_OF_YEAR))
                format = "HH:mm";
            else if (calendar.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH))
                format = "EEEE HH:mm";
            else
                format = "dd.MM.yyyy";
            timeFormat = new SimpleDateFormat(format);

        }
        return timeFormat.format(time);
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
                if (dto.getPosts().containsKey(time)){
                    dto.getPosts().put(time,  dto.getPosts().get(time) + 1);
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
            if (dto.getPosts().containsKey(time)){
                dto.getPosts().put(time,  dto.getPosts().get(time) + 1);
            } else
                dto.getPosts().put(time, 1);
        }
        return dto;
    }



    public StatisticsDto myStatistics() {

        Users u = usersService.getUser();
        Integer postCount = postsRepository.countByAuthor(u.getId());
        Integer likesCount = postVotesRepository.countByUserIdAndValue(u.getId(), 1);
        Integer dislikesCount = postVotesRepository.countByUserIdAndValue(u.getId(), -1);
        Integer viewsCount = postsRepository.countViews(u.getId());
        String firstPublication = FIRST_PUBLICATION.format(postsRepository.findFirstByTimeAndAuthor(u.getId()).getTime());
        return new StatisticsDto(postCount, likesCount, dislikesCount, viewsCount, firstPublication);
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
package object.services;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import object.dto.response.CaptchaDto;
import object.dto.response.StatisticsDto;
import object.dto.response.post.CalendarDto;
import object.dto.response.resultPostComment.ErrorCommentDto;
import object.dto.response.resultPostComment.OkCommentDto;
import object.dto.response.resultPostComment.ResultPostCommentDto;
import object.model.GlobalSettings;
import object.model.PostComments;
import object.model.Post;
import object.model.User;
import object.model.enums.ModerationStatus;
import object.repositories.*;
import object.services.Component.CaptchaCodeService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@AllArgsConstructor
public class GeneralService {

    private GlobalSettingsRepository globalSettingsRepository;
    private CaptchaCodeService captchaCode;
    private UsersService usersService;
    private PostsRepository postsRepository;
    private PostVotesRepository postVotesRepository;
    private PostCommentsRepository postCommentsRepository;


    private static final SimpleDateFormat DATE_2_TIME = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat YEAR = new SimpleDateFormat("yyyy");

    public CaptchaDto getCaptcha(){
        return captchaCode.getCaptchaDto();
    }

    public Map<String, String> getSetting(){
        Iterable<GlobalSettings> globalSettings = globalSettingsRepository.findAll();
        Map<String, String> settings = new HashMap<>();
        globalSettings.forEach(gS -> settings.put(gS.getName(), gS.getValue()));
        return settings;
    }

    public void saveSettings(Map<String, Boolean> globalSetting) {
        for (Map.Entry<String, Boolean> setting : globalSetting.entrySet()){
            GlobalSettings byCode = globalSettingsRepository.findByCode(setting.getKey());
            if (byCode == null){
                byCode = new GlobalSettings();
                byCode.setName(setting.getKey());
                byCode.setCode(setting.getKey());
            }
            byCode.setValue(setting.getValue().toString());
            globalSettingsRepository.save(byCode);
        }
    }

    public StatisticsDto myStatistics() {
        User u = usersService.getUser();
        Post p = postsRepository.findFirstByTimeToAuthor(u.getId());

        String time;
        if (p != null)
            time = String.valueOf(p.getTime().getTime()/1000);
        else time = "ноль";



        return StatisticsDto.builder()
                .postsCount(Optional.ofNullable(postsRepository.countByAuthor(u.getId())).orElse(0))
                .dislikesCount(Optional.ofNullable(postVotesRepository.countByUserIdAndValue(u.getId(), 1)).orElse(0))
                .likesCount(Optional.ofNullable(postVotesRepository.countByUserIdAndValue(u.getId(), -1)).orElse(0))
                .viewsCount(Optional.ofNullable(postsRepository.countViews(u.getId())).orElse(0))
                .firstPublication(time)
                .build();
    }


    public StatisticsDto allStatistic() {
        Post p = postsRepository.findFirstByTime();

        String time;
        if (p != null)
            time = String.valueOf(p.getTime().getTime()/1000);
        else time = "0";

        return StatisticsDto.builder()
                .postsCount(Optional.of(postsRepository.countPosts()).orElse(0))
                .dislikesCount(Optional.of(postVotesRepository.countByValue(-1)).orElse(0))
                .likesCount(Optional.of(postVotesRepository.countByValue(1)).orElse(0))
                .viewsCount(Optional.of(postsRepository.countByViewCount()).orElse(0))
                .firstPublication(time)
                .build();
    }

    @SneakyThrows
    public CalendarDto getCalendar(String year) {
        if (year.isEmpty()){
            return generateCalendarDto(postsRepository.getAllPosts(), year);
        }
        else return generateCalendarDto(postsRepository.getAllPosts());
    }

    private CalendarDto generateCalendarDto(List<Post> list, String year) {
        CalendarDto dto = new CalendarDto();
        for (Post p : list){
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

    private CalendarDto generateCalendarDto(List<Post> list) {
        CalendarDto dto = new CalendarDto();
        for (Post p : list){
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

    public ResultPostCommentDto addComment(Integer parentId, Integer postId, String text) {
        PostComments pC = new PostComments();
        pC.setUserId(usersService.getUser().getId());
        pC.setPost(postsRepository.findById(postId).get());
        if (text.length() > 0) pC.setText(text);
        else return new ErrorCommentDto();
        pC.setTime( new Date());
        pC.setParentId(parentId);
        PostComments result = postCommentsRepository.save(pC);
        return new OkCommentDto(result.getId());
    }

    public void moderationPost(Integer postId, String status) {
        Post post = postsRepository.findById(postId).get();
        post.setModerationId(usersService.getUser().getId());
        post.setModerationStatus(status.equalsIgnoreCase("ACCEPT") ? ModerationStatus.ACCEPTED : ModerationStatus.DECLINED);
        postsRepository.save(post);
    }
}

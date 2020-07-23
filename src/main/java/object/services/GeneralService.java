package object.services;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import object.component.ImagePath;
import object.dto.response.CaptchaDto;
import object.dto.response.StatisticsDto;
import object.dto.response.post.CalendarDto;
import object.dto.response.resultPostComment.ErrorCommentDto;
import object.dto.response.resultPostComment.OkCommentDto;
import object.dto.response.resultPostComment.ResultPostCommentDto;
import object.model.GlobalSettings;
import object.model.PostComments;
import object.model.Posts;
import object.model.Users;
import object.model.enums.ModerationStatus;
import object.repositories.*;
import object.services.Component.CaptchaCode;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class GeneralService {

    private GlobalSettingsRepository globalSettingsRepository;
    private CaptchaCode captchaCode;
    private UsersService usersService;
    private PostsRepository postsRepository;
    private PostVotesRepository postVotesRepository;
    private PostCommentsRepository postCommentsRepository;


    private static final SimpleDateFormat DATE_2_TIME = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat YEAR = new SimpleDateFormat("yyyy");

    public CaptchaDto getCaptcha(){
        captchaCode.deleteOld();
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
        Users u = usersService.getUser();
        Integer postCount = postsRepository.countByAuthor(u.getId());
        Integer likesCount = postVotesRepository.countByUserIdAndValue(u.getId(), 1);
        Integer dislikesCount = postVotesRepository.countByUserIdAndValue(u.getId(), -1);
        Integer viewsCount = postsRepository.countViews(u.getId());
        String firstPublication = DATE_2_TIME.format(postsRepository.findFirstByTimeAndAuthor(u.getId()).getTime());
        return new StatisticsDto(postCount, likesCount, dislikesCount, viewsCount, firstPublication);
    }


    public StatisticsDto allStatistic() {
        Integer postCount = postsRepository.getAllPosts().size();
        Integer likesCount = postVotesRepository.countByValue(1);
        Integer dislikesCount = postVotesRepository.countByValue(-1);
        Integer viewsCount = postsRepository.countByViewCount();
        Posts p = postsRepository.findFirstByTime();
        String firstPublication = DATE_2_TIME.format(p.getTime());
        return new StatisticsDto(postCount, likesCount, dislikesCount, viewsCount, firstPublication);
    }

    @SneakyThrows
    public CalendarDto getCalendar(String year) {
        if (year.isEmpty()){
            return generateCalendarDto(postsRepository.getAllPosts(), year);
        }
        else return generateCalendarDto(postsRepository.getAllPosts());
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
        Posts post = postsRepository.findById(postId).get();
        post.setModerationId(usersService.getUser().getId());
        post.setModerationStatus(status.equalsIgnoreCase("ACCEPT") ? ModerationStatus.ACCEPTED : ModerationStatus.DECLINED);
        postsRepository.save(post);
    }
}

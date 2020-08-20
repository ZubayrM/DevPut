package object.services;

import lombok.AllArgsConstructor;
import object.model.GlobalSettings;
import object.model.Post;
import object.model.enums.ModerationStatus;
import object.repositories.GlobalSettingsRepository;
import object.repositories.PostsRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class ModService {
    PostsRepository postsRepository;
    UsersService usersService;
    GlobalSettingsRepository globalSettingsRepository;

    public void moderationPost(Integer postId, String status) {
        Post post = postsRepository.findById(postId).get();
        post.setModerationId(usersService.getUser().getId());
        post.setModerationStatus(status.equalsIgnoreCase("ACCEPT") ? ModerationStatus.ACCEPTED : ModerationStatus.DECLINED);
        postsRepository.save(post);
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
}

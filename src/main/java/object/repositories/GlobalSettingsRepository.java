package object.repositories;

import object.model.GlobalSettings;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GlobalSettingsRepository extends CrudRepository<GlobalSettings, Integer> {

    @Query(value = "FROM GlobalSettings")
    List<GlobalSettings> findAll();


    @Query(value = "FROM GlobalSettings gS WHERE gS.code = :code")
    GlobalSettings findByCode(String code);
}

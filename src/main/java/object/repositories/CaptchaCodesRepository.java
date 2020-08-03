package object.repositories;

import object.model.CaptchaCodes;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;


@Repository
public interface CaptchaCodesRepository extends CrudRepository<CaptchaCodes, Integer> {

    @Query("from CaptchaCodes cc where cc.code = :captcha")
    CaptchaCodes findByCode(String captcha);


    @Query(value = "DELETE FROM CaptchaCodes WHERE time < :date")
    void deleteByTimeBefore(Date date);
}

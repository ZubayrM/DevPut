package object.repositories;

import object.model.CaptchaCodes;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;


@Repository
public interface CaptchaCodesRepository extends CrudRepository<CaptchaCodes, Integer> {

    @Query("from CaptchaCodes cc where cc.code = :captcha")
    CaptchaCodes findByCode(String captcha);

    @Modifying
    @Query(value = "DELETE FROM captcha_codes WHERE time < ?1", nativeQuery = true)
    void deleteByTimeBefore(Date date);
}

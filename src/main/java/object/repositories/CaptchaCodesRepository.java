package object.repositories;

import object.model.CaptchaCodes;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptchaCodesRepository extends CrudRepository<CaptchaCodes, Integer> {

    @Query("from CaptchaCodes cc where cc.code = :captcha")
    CaptchaCodes findByCode(String captcha);
}

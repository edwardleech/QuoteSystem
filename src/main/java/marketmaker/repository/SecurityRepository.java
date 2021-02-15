package marketmaker.repository;

import marketmaker.data.Security;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Security Repository for CRUD operation on security
 */
public interface SecurityRepository extends CrudRepository<Security, Long> {

    List<Security> findBySecurityId(int securityId);

    Security findById(long id);
}

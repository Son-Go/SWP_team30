package gde.gde_website.users.repository;

import gde.gde_website.users.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface which extends JpaRepository.
 * It used for working with PostgreSQL database
 *
 * @Author: Artemii Gorelov, Egor Grishin
 */
@Repository
public interface UsersRepository extends JpaRepository<UserEntity, Long> {
    /**
     *
     * @param username username to find in database
     * @return returns UserEntity which can be present or not
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     *
     * @param email - email of user to find in database
     * @return UserEntity which can be present or not
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     *
     * @param username to be checked for existence in database
     * @return yes - if user with such username exist in database, no otherwise
     */
    boolean existsByUsername(String username);

    /**
     *
     * @param email to be checked for existence in database
     * @return yes - if user with such email exist in database, no otherwise
     */
    boolean existsByEmail(String email);
}

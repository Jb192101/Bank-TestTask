package org.test.task.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.test.task.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.cards WHERE u.id = :id")
    Optional<User> findByIdWithCards(@Param("id") Long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.cards WHERE u.username = :username")
    Optional<User> findByUsernameWithCards(@Param("username") String username);
}

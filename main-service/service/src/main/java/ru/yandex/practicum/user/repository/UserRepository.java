package ru.yandex.practicum.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.user.model.User;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u")
    Page<User> getAllUsers(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.id IN :ids")
    Page<User> getUsersByIds(@Param("ids") List<Long> ids, Pageable pageable);
}

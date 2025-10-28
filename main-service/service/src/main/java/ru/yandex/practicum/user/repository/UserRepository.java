package ru.yandex.practicum.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.model.User;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);

    @Query("SELECT new ru.yandex.practicum.user.dto.UserDto(u.id, u.email, u.name) " +
            "FROM User u " +
            "WHERE (:ids is null or u.id in :ids) " +
            "ORDER BY u.id asc")
    List<UserDto> getUsersByListId(@Param("ids") List<Integer> ids);

    @Query("SELECT new ru.yandex.practicum.user.dto.UserDto(u.id, u.email, u.name) " +
            "FROM User u " +
            "ORDER BY u.id asc")
    List<UserDto> getUsersByOffset(Pageable pageable);
}

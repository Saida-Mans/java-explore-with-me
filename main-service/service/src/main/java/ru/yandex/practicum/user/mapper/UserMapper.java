package ru.yandex.practicum.user.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.user.NewUserDto;
import ru.yandex.practicum.user.NewUserRequest;
import ru.yandex.practicum.user.UserShortDto;
import ru.yandex.practicum.user.model.NewUserCreate;
import ru.yandex.practicum.user.model.User;
import java.util.List;

@AllArgsConstructor
@Component
public class UserMapper {

    public static User mapToUser(NewUserRequest newUserRequest) {
     User user = new User();
     user.setEmail(newUserRequest.getEmail());
     user.setName(newUserRequest.getName());
     return user;
    }

    public static NewUserCreate mapToNewUserCreate(List<Long> ids, int from, int size) {
        NewUserCreate newUserCreate = new NewUserCreate(ids, from, size);
        return  newUserCreate;
    }

    public static UserShortDto toShortDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserShortDto(user.getId(), user.getName());
    }

    public static NewUserDto mapToNewUserDto(User user) {
        NewUserDto dto = new NewUserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}

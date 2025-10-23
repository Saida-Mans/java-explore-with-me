package ru.yandex.practicum.user.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.user.dto.NewUserRequest;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.model.User;

@AllArgsConstructor
@Component
public class UserMapper {

    public static User userWriteDtoToUser(NewUserRequest userWriteDto) {
        User user = new User();
        user.setEmail(userWriteDto.getEmail());
        user.setName(userWriteDto.getName());
        return user;
    }

    public static UserDto userToUserReadDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }
}
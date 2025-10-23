package ru.yandex.practicum.user.service;

import ru.yandex.practicum.user.dto.NewUserRequest;
import ru.yandex.practicum.user.dto.UserDto;
import java.util.List;

public interface UserService {

  UserDto saveUser(NewUserRequest userWriteDto);

  List<UserDto> getUsersByListId(List<Integer> ids);

  List<UserDto> getUsersByOffset(Integer from, Integer size);

  void deleteUser(Integer id);
}

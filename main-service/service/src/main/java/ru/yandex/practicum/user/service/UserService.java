package ru.yandex.practicum.user.service;

import ru.yandex.practicum.user.NewUserDto;
import ru.yandex.practicum.user.NewUserRequest;
import ru.yandex.practicum.user.model.NewUserCreate;
import java.util.List;

public interface UserService {
  NewUserDto create(NewUserRequest newUserRequest);

  List<NewUserDto> getUsers(NewUserCreate newUserCreate);

  void deleteUserById(Long userId);
}

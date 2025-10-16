package ru.yandex.practicum.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.user.NewUserDto;
import ru.yandex.practicum.user.NewUserRequest;
import ru.yandex.practicum.user.mapper.UserMapper;
import ru.yandex.practicum.user.model.NewUserCreate;
import ru.yandex.practicum.user.service.UserService;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@AllArgsConstructor
public class AdminUserController {

    private final UserService userService;

 @PostMapping
 @ResponseStatus(HttpStatus.CREATED)
 public NewUserDto create(@RequestBody NewUserRequest newUserRequest) {
     return userService.create(newUserRequest);
 }

 @GetMapping
 @ResponseStatus(HttpStatus.OK)
 public List<NewUserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size) {
     NewUserCreate newUsercreate = UserMapper.mapToNewUserCreate(ids, from, size);
     return userService.getUsers(newUsercreate);
 }

 @DeleteMapping("/{userId}")
 @ResponseStatus(HttpStatus.NO_CONTENT)
 public void deleteUserById(@PathVariable Long userId) {
     userService.deleteUserById(userId);
 }
}

package ru.yandex.practicum.user.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.user.NewUserDto;
import ru.yandex.practicum.user.NewUserRequest;
import ru.yandex.practicum.user.mapper.UserMapper;
import ru.yandex.practicum.user.model.NewUserCreate;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.repository.UserRepository;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServicelmpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    public NewUserDto create(NewUserRequest newUserRequest) {
        User user = UserMapper.mapToUser(newUserRequest);
        User savedUser = userRepository.save(user);
        return UserMapper.mapToNewUserDto(savedUser);
    }

    public List<NewUserDto> getUsers(NewUserCreate newUserCreate) {
        Pageable pageable = PageRequest.of(newUserCreate.getFrom() / newUserCreate.getSize(), newUserCreate.getSize());
        List<User> users;
        if (newUserCreate.getIds() == null || newUserCreate.getIds().isEmpty()) {
          users = userRepository.getAllUsers(pageable).getContent();
        } else {
            users = userRepository.getUsersByIds(newUserCreate.getIds(), pageable).getContent();
        }
        return users.stream()
                .map(UserMapper::mapToNewUserDto)
                .toList();
    }

    @Transactional
    public void deleteUserById(Long userId) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        userRepository.deleteById(userId);
    }
}

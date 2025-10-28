package ru.yandex.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.user.dto.NewUserRequest;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.mapper.UserMapper;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.repository.UserRepository;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServicelmpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto saveUser(NewUserRequest userWriteDto) {
        if (userRepository.existsByEmail(userWriteDto.getEmail())) {
            throw new ValidationException("Пользователь с таким Email уже есть !", HttpStatus.CONFLICT);
        }
        User user = UserMapper.userWriteDtoToUser(userWriteDto);
        userRepository.save(user);
        return UserMapper.userToUserReadDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByListId(List<Integer> ids) {
        return userRepository.getUsersByListId(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByOffset(Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.getUsersByOffset(pageable);
    }

    @Override
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователя с таким id нет");
        }
        userRepository.deleteById(id);
    }
}
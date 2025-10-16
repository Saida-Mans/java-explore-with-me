package ru.yandex.practicum.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class NewUserCreate {
    private List<Long> ids;
    private int from;
    private int size;
}

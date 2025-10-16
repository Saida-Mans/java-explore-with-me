package ru.yandex.practicum.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewCompilation {
    private Boolean pinned;
    private int from;
    private int size;
}

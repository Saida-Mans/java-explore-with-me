package ru.yandex.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEndpointHitDto {
    @NotBlank(message = "Отсутствует наименовние приложения !")
    private String app;

    @NotBlank(message = "Отсутствует uri !")
    private String uri;

    @NotBlank(message = "Отсутствует ip запрашивающего пользователя !")
    private String ip;

    @NotNull(message = "Отсутствует дата и время запроса !")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}

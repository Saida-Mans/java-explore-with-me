package ru.yandex.practicum;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateEndpointHitDto {
    @NotBlank
    private String app;

    @NotBlank
    private String uri;

    @NotBlank
    private String ip;
}

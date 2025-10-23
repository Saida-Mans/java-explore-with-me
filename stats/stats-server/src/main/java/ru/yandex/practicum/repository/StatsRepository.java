package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.ViewStatsDto;
import ru.yandex.practicum.module.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("""
            SELECT new ru.yandex.practicum.ViewStatsDto(e.app, e.uri, COUNT(DISTINCT e.ip))
            FROM EndpointHit e
            WHERE e.created BETWEEN :start AND :end
              AND e.uri IN :uris
            GROUP BY e.app, e.uri
            ORDER BY COUNT(DISTINCT e.ip) DESC
            """)
    List<ViewStatsDto> getUniqueIpStats(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end,
                                        @Param("uris") List<String> uris);

    @Query("""
            SELECT new ru.yandex.practicum.ViewStatsDto(e.app, e.uri, COUNT(e.ip))
            FROM EndpointHit e
            WHERE e.created BETWEEN :start AND :end
              AND e.uri IN :uris
            GROUP BY e.app, e.uri
            ORDER BY COUNT(e.ip) DESC
            """)
    List<ViewStatsDto> getAllIpStats(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end,
                                     @Param("uris") List<String> uris);

    @Query("""
            SELECT new ru.yandex.practicum.ViewStatsDto(e.app, e.uri, COUNT(DISTINCT e.ip))
            FROM EndpointHit e
            WHERE e.created BETWEEN :start AND :end
            GROUP BY e.app, e.uri
            ORDER BY COUNT(DISTINCT e.ip) DESC
            """)
    List<ViewStatsDto> getUniqueIpStatsWithoutUris(@Param("start") LocalDateTime start,
                                                   @Param("end") LocalDateTime end);

    @Query("""
            SELECT new ru.yandex.practicum.ViewStatsDto(e.app, e.uri, COUNT(e.ip))
            FROM EndpointHit e
            WHERE e.created BETWEEN :start AND :end
            GROUP BY e.app, e.uri
            ORDER BY COUNT(e.ip) DESC
            """)
    List<ViewStatsDto> getAllIpStatsWithoutUris(@Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end);
}

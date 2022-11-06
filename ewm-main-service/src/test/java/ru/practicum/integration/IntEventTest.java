package ru.practicum.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.*;
import ru.practicum.model.Event;
import ru.practicum.services.CategorySrv;
import ru.practicum.services.EventSrv;
import ru.practicum.services.UserSrv;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test_ewm",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class IntEventTest {

    private final EntityManager em;
    private final UserSrv userSrv;
    private final EventSrv eventSrv;
    private final CategorySrv categorySrv;
    private static NewCategoryDto newCategoryDto;
    private static NewEventDto eventDto;
    private static NewUserDto userDto;

    @BeforeEach
    @Sql({"/schema.sql"})
    public void setUp() {
        newCategoryDto = new NewCategoryDto("Выставка");
        userDto = new NewUserDto("Name", "Email@email.ru");
        eventDto = new NewEventDto("title",
                "annotation",
                "description",
                1,
                LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                true,
                new LocationDto(54.5, 80.9),
                10,
                true);
    }

    @AfterEach
    @Sql({"/test/resources/clean.sql"})
    void clean() {
    }

    @Test
    void saveEvent() {
        CategoryDto categoryDto = categorySrv.createCategory(newCategoryDto);
        UserDto user = userSrv.createUser(userDto);
        eventDto.setCategory(categoryDto.getId());
        eventSrv.addEvent(user.getId(), eventDto);
        TypedQuery<Event> query = em.createQuery("Select e from Event e where e.title = :title", Event.class);
        Event event = query.setParameter("title", eventDto.getTitle()).getSingleResult();
        assertThat(event.getId(), notNullValue());
        assertThat(event.getLocation(), notNullValue());
        System.out.println(event);
    }

}

package ru.practicum.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.*;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.model.Comment;
import ru.practicum.services.CategorySrv;
import ru.practicum.services.EventSrv;
import ru.practicum.services.UserSrv;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class IntCommentTest {
    private final UserSrv service;
    private final EventSrv eventSrv;
    private final CategorySrv categorySrv;
    private static NewCategoryDto newCategoryDto;
    private static NewEventDto eventDto;
    private static NewUserDto userDto;
    private static NewCommentDto commentDto;

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
        commentDto = new NewCommentDto("new Comment");
    }

    @Test
    void saveComment() {
        CategoryDto categoryDto = categorySrv.createCategory(newCategoryDto);
        UserDto user = service.createUser(userDto);
        eventDto.setCategory(categoryDto.getId());
        EventFullDto event = eventSrv.addEvent(user.getId(), eventDto);

        CommentDto comment = eventSrv.addComment(user.getId(), event.getId(), commentDto);

        assertThat(comment.getId(), notNullValue());
        assertThat(comment.getText(), equalTo(commentDto.getText()));
    }

    @Test
    void deleteComment() {
        CategoryDto categoryDto = categorySrv.createCategory(newCategoryDto);
        UserDto user = service.createUser(userDto);
        eventDto.setCategory(categoryDto.getId());
        EventFullDto event = eventSrv.addEvent(user.getId(), eventDto);

        CommentDto comment = eventSrv.addComment(user.getId(), event.getId(), commentDto);
        eventSrv.deleteComment(user.getId(), event.getId(), comment.getId());

        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> eventSrv.findCommentById(comment.getId()));
        Assertions.assertEquals(exception.getMessage(), "Comment with id=" + comment.getId() + " was not found.");
    }

    @Test
    void updateComment() {
        CategoryDto categoryDto = categorySrv.createCategory(newCategoryDto);
        UserDto user = service.createUser(userDto);
        eventDto.setCategory(categoryDto.getId());
        EventFullDto event = eventSrv.addEvent(user.getId(), eventDto);

        CommentDto comment = eventSrv.addComment(user.getId(), event.getId(), commentDto);
        eventSrv.updateComment(user.getId(), event.getId(), comment.getId(), "Update comment");
        Comment c = eventSrv.findCommentById(comment.getId());
        assertThat(c.getText(), equalTo("Update comment"));
    }

    @Test
    void likeComment() {
        CategoryDto categoryDto = categorySrv.createCategory(newCategoryDto);
        UserDto user = service.createUser(userDto);
        eventDto.setCategory(categoryDto.getId());
        EventFullDto event = eventSrv.addEvent(user.getId(), eventDto);
        CommentDto comment = eventSrv.addComment(user.getId(), event.getId(), commentDto);
        assertThat(comment.getRating(), equalTo(0));

        NewUserDto newUserDto = new NewUserDto("New Name", "NewEmail@email.ru");
        UserDto newUser = service.createUser(newUserDto);
        eventSrv.likeComment(newUser.getId(), event.getId(), comment.getId());
        Comment c = eventSrv.findCommentById(comment.getId());
        assertThat(c.getRating().size(), equalTo(1));

        eventSrv.likeComment(user.getId(), event.getId(), comment.getId());
        c = eventSrv.findCommentById(comment.getId());
        assertThat(c.getRating().size(), equalTo(2));
    }

    @Test
    void repeatLike() {
        CategoryDto categoryDto = categorySrv.createCategory(newCategoryDto);
        UserDto user = service.createUser(userDto);
        eventDto.setCategory(categoryDto.getId());
        EventFullDto event = eventSrv.addEvent(user.getId(), eventDto);
        CommentDto comment = eventSrv.addComment(user.getId(), event.getId(), commentDto);
        assertThat(comment.getRating(), equalTo(0));

        NewUserDto newUserDto = new NewUserDto("New Name", "NewEmail@email.ru");
        UserDto newUser = service.createUser(newUserDto);
        eventSrv.likeComment(newUser.getId(), event.getId(), comment.getId());
        Comment c = eventSrv.findCommentById(comment.getId());
        assertThat(c.getRating().size(), equalTo(1));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> eventSrv.likeComment(newUser.getId(), event.getId(), comment.getId()));
        Assertions.assertEquals(exception.getMessage(), "The user has already liked the comment");

    }

    @Test
    void removeLike() {
        CategoryDto categoryDto = categorySrv.createCategory(newCategoryDto);
        UserDto user = service.createUser(userDto);
        eventDto.setCategory(categoryDto.getId());
        EventFullDto event = eventSrv.addEvent(user.getId(), eventDto);
        CommentDto comment = eventSrv.addComment(user.getId(), event.getId(), commentDto);
        assertThat(comment.getRating(), equalTo(0));

        NewUserDto newUserDto = new NewUserDto("New Name", "NewEmail@email.ru");
        UserDto newUser = service.createUser(newUserDto);
        eventSrv.likeComment(newUser.getId(), event.getId(), comment.getId());
        Comment c = eventSrv.findCommentById(comment.getId());
        assertThat(c.getRating().size(), equalTo(1));

        eventSrv.removeLike(newUser.getId(), event.getId(), comment.getId());
        c = eventSrv.findCommentById(comment.getId());
        assertThat(c.getRating().size(), equalTo(0));
    }

    @Test
    void errorDeletingLike() {
        CategoryDto categoryDto = categorySrv.createCategory(newCategoryDto);
        UserDto user = service.createUser(userDto);
        eventDto.setCategory(categoryDto.getId());
        EventFullDto event = eventSrv.addEvent(user.getId(), eventDto);
        CommentDto comment = eventSrv.addComment(user.getId(), event.getId(), commentDto);
        assertThat(comment.getRating(), equalTo(0));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> eventSrv.removeLike(user.getId(), event.getId(), comment.getId()));
        Assertions.assertEquals(exception.getMessage(), "The user " + user.getId() + " did not like the comment " + comment.getId());
    }

    @Test
    void getComments() {
        CategoryDto categoryDto = categorySrv.createCategory(newCategoryDto);
        UserDto user = service.createUser(userDto);
        eventDto.setCategory(categoryDto.getId());
        EventFullDto event = eventSrv.addEvent(user.getId(), eventDto);
        NewUserDto newUserDto = new NewUserDto("New Name", "NewEmail@email.ru");
        UserDto newUser = service.createUser(newUserDto);


        CommentDto comment = eventSrv.addComment(user.getId(), event.getId(), commentDto);
        CommentDto comment2 = eventSrv.addComment(user.getId(), event.getId(), new NewCommentDto("Comment 2"));
        CommentDto comment3 = eventSrv.addComment(user.getId(), event.getId(), new NewCommentDto("Comment 3"));

        eventSrv.likeComment(user.getId(), event.getId(), comment2.getId());
        eventSrv.likeComment(newUser.getId(), event.getId(), comment2.getId());

        List<CommentDto> comments = eventSrv.getComments(user.getId(), event.getId(), "NEW_DATE", 0, 10);
        assertThat(comments.size(), equalTo(3));
        assertThat(comments.get(0), equalTo(comment));

        comments = eventSrv.getComments(user.getId(), event.getId(), "OLD_DATE", 0, 10);
        assertThat(comments.get(0), equalTo(comment3));

        comments = eventSrv.getComments(user.getId(), event.getId(), "RATING", 0, 10);
        assertThat(comments.get(0).getId(), equalTo(comment2.getId()));
    }
}

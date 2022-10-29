package ru.practicum.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.model.Category;
import ru.practicum.services.AdminService;
import ru.practicum.services.CategorySrv;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test_ewm",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class IntCatTest {

    private final EntityManager em;
    private final AdminService service;
    private final CategorySrv categorySrv;
    private static CategoryDto categoryDto;
    private static NewCategoryDto newCategoryDto;

    @BeforeEach
    @Sql({"/schema.sql"})
    public void setUp() {
        categoryDto = new CategoryDto(1, "Концерты");
        newCategoryDto = new NewCategoryDto("Выставка");
    }

    @AfterEach
    @Sql({"/test/resources/clean.sql"})
    void clean() {
    }


    @Test
    void saveCategory() {
        categorySrv.createCategory(newCategoryDto);
        TypedQuery<Category> query = em.createQuery("Select c from Category c where c.name = :name", Category.class);
        Category category = query.setParameter("name", newCategoryDto.getName()).getSingleResult();
        assertThat(category.getId(), notNullValue());
    }

    @Test
    void saveCategorySameName() {
        categorySrv.createCategory(newCategoryDto);
        TypedQuery<Category> query = em.createQuery("Select c from Category c where c.name = :name", Category.class);
        Category category = query.setParameter("name", newCategoryDto.getName()).getSingleResult();
        assertThat(category.getId(), notNullValue());
        final ConflictException exception = Assertions.assertThrows(
                ConflictException.class,
                () -> categorySrv.createCategory(newCategoryDto));
        Assertions.assertEquals(exception.getMessage(), "could not execute statement; SQL [n/a]; constraint Выставка; " +
                "nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement");
    }

}

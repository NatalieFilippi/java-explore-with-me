package ru.practicum.datajpa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.dao.CategoryRepository;
import ru.practicum.model.Category;

@DataJpaTest
public class CategoryTest {

    @Autowired
    CategoryRepository repository;
    @Autowired
    private TestEntityManager em;

    private static Category category;

    @BeforeEach
    void beforeEach() {
        category = new Category();
        category.setName("Концерты");
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void saveCategory() {
        repository.save(category);
        Assertions.assertNotNull(category.getId());
        Assertions.assertEquals(category.getName(), "Концерты");
    }
}

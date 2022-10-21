package ru.practicum.datajpa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.dao.CategoryRepository;
import ru.practicum.model.Category;

import java.util.List;

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
        Category category2 = new Category();
        category2.setName("Концерты");
        repository.save(category2);
        List<Category> categoryList = repository.findAll();
    }
}

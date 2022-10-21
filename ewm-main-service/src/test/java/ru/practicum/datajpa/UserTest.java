package ru.practicum.datajpa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.dao.UserRepository;
import ru.practicum.model.User;

@DataJpaTest
public class UserTest {
    @Autowired
    private UserRepository repository;
    @Autowired
    private TestEntityManager em;
    private static User user;

    @BeforeEach
    void beforeEach() {
        user = new User();
        user.setName("Peter");
        user.setEmail("peter@ya.ru");
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void saveUser() {
        repository.save(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(user.getName(), "Peter");
    }

    @Test
    void getUser() {
        repository.save(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(user.getName(), "Peter");
        Page<User> users = repository.findAll(Pageable.unpaged());
        Assertions.assertEquals(users.getSize(), 1);
    }

    @Test
    void deleteUser() {
        repository.save(user);
        Page<User> users = repository.findAll(Pageable.unpaged());
        Assertions.assertEquals(users.getSize(), 1);
        repository.deleteById(users.get().findFirst().get().getId());
        users = repository.findAll(Pageable.unpaged());
        Assertions.assertEquals(users.getSize(), 0);
    }
}

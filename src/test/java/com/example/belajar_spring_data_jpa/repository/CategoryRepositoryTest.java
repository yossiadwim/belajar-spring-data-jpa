package com.example.belajar_spring_data_jpa.repository;

import com.example.belajar_spring_data_jpa.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void insert() {
        Category category = new Category();
        category.setName("Computer");
        categoryRepository.save(category);

        assertNotNull(category.getId());
    }

    @Test
    void update() {
        Category category = categoryRepository.findById(1L).get();
        category.setName("Laptop");
        categoryRepository.save(category);

        category = categoryRepository.findById(1L).orElse(null);
        assertNotNull(category);
        assertEquals("Laptop", category.getName());
    }

    @Test
    void queryMethod() {
        Optional<Category> category = categoryRepository.findFirstByNameEquals("Laptop");
        assertNotNull(category);
        assertEquals("Laptop", category.get().getName());

        List<Category> categories = categoryRepository.findAllByNameLike("%Laptop%");
        assertEquals(1, categoryRepository.findAllByNameLike("%Laptop%").size());
        assertEquals("Laptop", categories.get(0).getName());
    }

    @Test
    void audit() {
        Category category = new Category();
        category.setName("Mobile Phone");
        categoryRepository.save(category);

        assertNotNull(category.getId());
        assertNotNull(category.getCreatedDate());
        assertNotNull(category.getLastModifiedDate());
    }

    @Test
    void example(){
        Category category = new Category();
        category.setName("Laptop");

        Example<Category> example = Example.of(category);

        List<Category> categories = categoryRepository.findAll(example);
        assertEquals(1, categories.size());
    }

    @Test
    void example2(){
        Category category = new Category();
        category.setName("Laptop");
        category.setId(1L);

        Example<Category> example = Example.of(category);

        List<Category> categories = categoryRepository.findAll(example);
        assertEquals(1, categories.size());
    }

    @Test
    void exampleMatcher() {
        Category category = new Category();
        category.setName("laptop");

        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withIgnoreNullValues().withIgnoreCase();
        Example<Category> example = Example.of(category, exampleMatcher);

        List<Category> categories = categoryRepository.findAll(example);
        assertEquals(1, categories.size());
    }
}
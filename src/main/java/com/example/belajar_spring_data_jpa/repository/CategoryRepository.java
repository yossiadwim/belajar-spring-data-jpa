package com.example.belajar_spring_data_jpa.repository;


import com.example.belajar_spring_data_jpa.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}

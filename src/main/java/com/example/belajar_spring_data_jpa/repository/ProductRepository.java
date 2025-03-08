package com.example.belajar_spring_data_jpa.repository;

import com.example.belajar_spring_data_jpa.entity.Category;
import com.example.belajar_spring_data_jpa.entity.Product;
import com.example.belajar_spring_data_jpa.model.SimpleProduct;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {


    @Transactional
    int deleteByName(String name);

    boolean existsByName(String name);

    List<Product> findAllByCategory_Name(String name);

    List<Product> findAllByCategory_Name(String name, Sort sort);

    Page<Product> findAllByCategory_Name(String name, Pageable pageable);

    Long countByCategory_Name(String name);

    List<Product> searchProductUsingName(@Param("name") String name, Pageable pageable);

    @Query(value = "select p from Product p where p.name like :name or p.category.name like :name",
            countQuery = "select count(p) from Product p where p.name like :name or p.category.name like :name")
    Page<Product> searchProduct(@Param("name") String name, Pageable pageable);


    @Transactional
    @Modifying
    @Query(value = "delete from Product p where p.name = :name")
    int deleteProductUsingName(@Param("name") String name);


    @Transactional
    @Modifying
    @Query(value = "update Product p set p.price = 0 where p.id = :id")
    int updateProductUsingName(@Param("id") Long id);


    Stream<Product> streamAllByCategory(Category category,Sort sort);

    Slice<Product> findAllByCategory(Category category, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findFirstByIdEquals(Long id);

    <T>List<T> findAllByNameLike(String name, Class<T> aClass);



}

package com.example.belajar_spring_data_jpa.repository;

import com.example.belajar_spring_data_jpa.entity.Category;
import com.example.belajar_spring_data_jpa.entity.Product;
import com.example.belajar_spring_data_jpa.model.ProductPrice;
import com.example.belajar_spring_data_jpa.model.SimpleProduct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.support.TransactionOperations;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ProductRepositoryTest {


    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TransactionOperations transactionOperations;

    @Test
    void createProducts() {
        Category category = categoryRepository.findById(1L).orElse(null);
        assertNotNull(category);

        {
            Product product = new Product();
            product.setName("iPhone 11");
            product.setPrice(10_000_000L);
            product.setCategory(category);
            productRepository.save(product);
        }

        {
            Product product = new Product();
            product.setName("iPhone 12");
            product.setPrice(12_000_000L);
            product.setCategory(category);
            productRepository.save(product);
        }
    }

    @Test
    void findByCategoryName() {
        List<Product> products = productRepository.findAllByCategory_Name("Laptop");
        assertEquals(2,products.size());
        assertEquals("iPhone 11",products.get(0).getName());
        assertEquals("iPhone 12",products.get(1).getName());
    }

    @Test
    void sort() {
        Sort sort = Sort.by(Sort.Order.desc("id"));
        List<Product> products = productRepository.findAllByCategory_Name("Laptop",sort);
        assertEquals(2,products.size());
        assertEquals("iPhone 12",products.get(0).getName());
        assertEquals("iPhone 11",products.get(1).getName());

    }

    @Test
    void pageable() {

        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Order.desc("id")));
        Page<Product> products = productRepository.findAllByCategory_Name("Laptop",pageable);
        assertEquals(1,products.getContent().size());
        assertEquals(0, products.getNumber());
        assertEquals(2, products.getTotalElements());
        assertEquals(2, products.getTotalPages());
        assertEquals("iPhone 12",products.getContent().get(0).getName());

        pageable = PageRequest.of(1, 1, Sort.by(Sort.Order.desc("id")));
        products = productRepository.findAllByCategory_Name("Laptop",pageable);
        assertEquals(1,products.getContent().size());
        assertEquals(1, products.getNumber());
        assertEquals(2, products.getTotalElements());
        assertEquals(2, products.getTotalPages());
        assertEquals("iPhone 11",products.getContent().get(0).getName());


    }

    @Test
    void testCount() {
        long count = productRepository.count();
        assertEquals(2, count);

        count = productRepository.countByCategory_Name("Laptop");
        assertEquals(2, count);

        count = productRepository.countByCategory_Name("nothing");
        assertEquals(0, count);
    }

    @Test
    void testExist() {
        boolean exists = productRepository.existsByName("iPhone 11");
        assertTrue(exists);

        exists = productRepository.existsByName("nothing");
        assertFalse(exists);
    }

    @Test
    void testDelete() {
            Category category = categoryRepository.findById(1L).orElse(null);
            assertNotNull(category);

            Product product = new Product();
            product.setName("iPhone 13");
            product.setPrice(15_000_000L);
            product.setCategory(category);
            productRepository.save(product);

            int delete = productRepository.deleteByName("iPhone 13");
            assertEquals(1,delete);

            delete = productRepository.deleteByName("iPhone 13");
            assertEquals(0,delete);
    }

    @Test
    void namedQuery() {
        Pageable pageable = PageRequest.of(0, 1);
        List<Product> products = productRepository.searchProductUsingName("iPhone 11", pageable);
        assertEquals(1,products.size());
        assertEquals("iPhone 11",products.get(0).getName());
    }

    @Test
    void searchProduct() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Order.desc("id")));
        Page<Product> products = productRepository.searchProduct("%iPhone%", pageable);
        assertEquals(1,products.getContent().size());
        assertEquals(0, products.getNumber());
        assertEquals(2, products.getTotalElements());
        assertEquals(2, products.getTotalPages());

        products = productRepository.searchProduct("%Laptop%", pageable);
        assertEquals(0, products.getNumber());
        assertEquals(1,products.getContent().size());
        assertEquals(2, products.getTotalElements());
        assertEquals(2, products.getTotalPages());
    }

    @Test
    void modifying() {


        int total = productRepository.deleteProductUsingName("nothing");
        assertEquals(0, total);

        total = productRepository.updateProductUsingName(1L);
        assertEquals(1, total);

        Product product = productRepository.findById(1L).orElse(null);
        assertNotNull(product);
        assertEquals(0L, product.getPrice());
    }

    @Test
    void testStream() {

        transactionOperations.executeWithoutResult(transactionStatus -> {
            Category category = categoryRepository.findById(1L).orElse(null);
            assertNotNull(category);

            Sort sort = Sort.by(Sort.Order.asc("id"));
            Stream<Product> productStream = productRepository.streamAllByCategory(category, sort);
            productStream.forEach(product -> {
                System.out.println(product.getId() + " : " + product.getName());
            });
        });
    }

    @Test
    void testSlice() {

        Pageable pageable = PageRequest.of(0,1);
        Category category = categoryRepository.findById(1L).orElse(null);
        assertNotNull(category);

        Slice<Product> slice = productRepository.findAllByCategory(category,pageable);

        while (slice.hasNext()) {
            slice = productRepository.findAllByCategory(category,slice.nextPageable());

        }
    }


    @Test
    void lock1() {
        transactionOperations.executeWithoutResult(transactionStatus -> {
            try{
                Product product = productRepository.findFirstByIdEquals(1L).orElse(null);
                assertNotNull(product);

                product.setPrice(30_000_000L);
                Thread.sleep(20000);
                productRepository.save(product);
            }catch (InterruptedException e){
                throw new RuntimeException(e);
            }
        });
    }


    @Test
    void lock2() {
        transactionOperations.executeWithoutResult(transactionStatus -> {
                Product product = productRepository.findFirstByIdEquals(1L).orElse(null);
                assertNotNull(product);
                product.setPrice(10_000_000L);
                productRepository.save(product);
        });
    }

    @Test
    void specification() {
        Specification<Product> specification = ((root, criteria, criteriaBuilder) -> {
            return criteria.where(
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("name"), "iPhone 11"),
                            criteriaBuilder.equal(root.get("name"), "iPhone 12")
                    )
            ).getRestriction();
        });
        List<Product> products = productRepository.findAll(specification);
        assertEquals(2, products.size());
    }

    @Test
    void testProjection() {
        List<SimpleProduct> simpleProducts = productRepository.findAllByNameLike("%iPhone%", SimpleProduct.class);
        assertEquals(2, simpleProducts.size());

        List<ProductPrice> productsPrices = productRepository.findAllByNameLike("%iPhone%", ProductPrice.class);
        assertEquals(2, productsPrices.size());

    }
}
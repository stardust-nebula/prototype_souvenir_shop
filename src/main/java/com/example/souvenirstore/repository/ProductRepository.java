package com.example.souvenirstore.repository;

import com.example.souvenirstore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Modifying
    @Query("update Product p set p.name =?2 where p.id =?1")
    @Transactional(rollbackFor = Exception.class)
    void changeProductName(long id, String newProductName);

    @Modifying
    @Query("update Product p set p.description =?2 where p.id =?1")
    @Transactional(rollbackFor = Exception.class)
    void changeProductDescription(long id, String newProductDescription);

    @Modifying
    @Query("update Product p set p.price =?2 where p.id =?1")
    @Transactional(rollbackFor = Exception.class)
    void changeProductPrice(long id, double newProductPrice);

    @Modifying
    @Query("update Product p set p.statusIsEnabled =?2 where p.id =?1")
    @Transactional(rollbackFor = Exception.class)
    void changeProductAvailabilityStatus(long id, boolean newProductStatus);

    boolean existsProductByName(String name);

    List<Product> getProductsByNameIsContaining(String productName);

    List<Product> getProductsByPrice(double price);

    List<Product> getProductsByStatusIsEnabled(boolean status);

    Optional<Product> getProductById(long productId);

}

package com.stockflow.inventory.repository;

import com.stockflow.inventory.entity.NewProductRequest;
import com.stockflow.inventory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewProductRequestRepository extends JpaRepository<NewProductRequest, Long> {
    List<NewProductRequest> findAllByOrderByCreatedAtDesc();
    List<NewProductRequest> findByStaffOrderByCreatedAtDesc(User staff);
}

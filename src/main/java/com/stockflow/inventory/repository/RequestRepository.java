package com.stockflow.inventory.repository;

import com.stockflow.inventory.entity.Request;
import com.stockflow.inventory.entity.User;
import com.stockflow.inventory.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("""
        SELECT r FROM Request r
        JOIN FETCH r.staff s
        JOIN FETCH r.product p
        WHERE (:status IS NULL OR r.status = :status)
          AND (:search IS NULL OR :search = ''
               OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')))
        ORDER BY r.requestDate DESC
        """)
    Page<Request> findAllWithFilters(@Param("status") RequestStatus status,
                                     @Param("search") String search,
                                     Pageable pageable);

    List<Request> findByStaffOrderByRequestDateDesc(User staff);

    long countByStatus(RequestStatus status);
}

package com.example.fitness.Repository;

import com.example.fitness.Entity.Category;
import com.example.fitness.Entity.Location;
import com.example.fitness.Entity.Status;
import com.example.fitness.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepo extends JpaRepository<Location, Long> {
    List<Location> findByStatus(String status);
    List<Location> findByNameContainingIgnoreCase(String name);
    Page<Location> findByStatus(Status status, Pageable pageable);
    Page<Location> findByNameContainingIgnoreCase(String name, Pageable pageable);
    List<Location> findByUser(User user);
    List<Location> findByBookedBy(User user);
    Page<Location> findByUser(User user, Pageable pageable);
    List<Location> findByCategory(Category category);
    Page<Location> findByCategoryAndStatusAndNameContainingIgnoreCase(Category category, Status status, String name, Pageable pageable);
    Page<Location> findByCategoryAndNameContainingIgnoreCase(Category category, String name, Pageable pageable);
    Page<Location> findByStatusAndNameContainingIgnoreCase(Status status, String name, Pageable pageable);
}
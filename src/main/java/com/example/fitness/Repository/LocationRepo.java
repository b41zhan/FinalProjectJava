package com.example.fitness.Repository;

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
}
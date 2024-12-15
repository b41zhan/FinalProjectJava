//package com.example.fitness.Service;
//
//import com.example.fitness.Entity.Location;
//import com.example.fitness.Entity.Status;
//import com.example.fitness.Entity.User;
//import com.example.fitness.Repository.LocationRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class LocationService {
//
//    private final LocationRepo locationRepo;
//    @Autowired
//    public LocationService(LocationRepo locationRepo) {
//        this.locationRepo = locationRepo;
//    }
//
//    public void save(Location location) {
//        locationRepo.save(location);
//    }
//
//
//    public List<Location> findAll() {
//        return locationRepo.findAll();
//    }
//
//    public List<Location> findByStatus(String status) {
//        return locationRepo.findByStatus(status);
//    }
//
//    public List<Location> findByNameContaining(String name) {
//        return locationRepo.findByNameContainingIgnoreCase(name);
//    }
//
//    public Location findById(Long id) {
//        return locationRepo.findById(id).orElse(null);
//    }
//
//
//
//    public Page<Location> findAll(Pageable pageable) {
//        return locationRepo.findAll(pageable);
//    }
//
//    public Page<Location> findByStatus(Status status, Pageable pageable) {
//        return locationRepo.findByStatus(status, pageable);
//    }
//
//    public Page<Location> findByNameContaining(String name, Pageable pageable) {
//        return locationRepo.findByNameContainingIgnoreCase(name, pageable);
//    }
//
//
//
//    public void deleteById(Long id) {
//        locationRepo.deleteById(id);
//    }
//
//
//    public List<Location> findByUser(User user) {
//        return locationRepo.findByUser(user);
//    }
//}

package com.example.fitness.Service;

import com.example.fitness.Entity.Location;
import com.example.fitness.Entity.Status;
import com.example.fitness.Entity.User;
import com.example.fitness.Repository.LocationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepo locationRepo;

    @Autowired
    public LocationService(LocationRepo locationRepo) {
        this.locationRepo = locationRepo;
    }

    public void save(Location location) {
        locationRepo.save(location);
    }

    public List<Location> findAll() {
        return locationRepo.findAll();
    }

    public List<Location> findByStatus(String status) {
        return locationRepo.findByStatus(status);
    }

    public List<Location> findByNameContaining(String name) {
        return locationRepo.findByNameContainingIgnoreCase(name);
    }

    public Location findById(Long id) {
        return locationRepo.findById(id).orElse(null);
    }

    public Page<Location> findAll(Pageable pageable) {
        return locationRepo.findAll(pageable);
    }

    public Page<Location> findByStatus(Status status, Pageable pageable) {
        return locationRepo.findByStatus(status, pageable);
    }

    public Page<Location> findByNameContaining(String name, Pageable pageable) {
        return locationRepo.findByNameContainingIgnoreCase(name, pageable);
    }

    public void deleteById(Long id) {
        locationRepo.deleteById(id);
    }

    public List<Location> findByUser(User user) {
        return locationRepo.findByUser(user);
    }

    public List<Location> findByBookedBy(User user) {
        return locationRepo.findByBookedBy(user);
    }
}



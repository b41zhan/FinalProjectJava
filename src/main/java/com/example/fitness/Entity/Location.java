//package com.example.fitness.Entity;
//
//import jakarta.persistence.*;
//import java.time.LocalDate;
//
//@Entity
//@Table(name = "locations")
//public class Location {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String name; // Название
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private Category category;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private Status status = Status.FREE; // Статус (Забронирован, Свободный)
//
//    @Column(nullable = false)
//    private String address; // Адрес
//
//    @Column(nullable = false, columnDefinition = "TEXT")
//    private String description; // Описание
//
//    @Column(nullable = false)
//    private String amenities; // Удобства
//
//    @Column(nullable = false)
//    private LocalDate date; // Время бронирования
//
//    @Column(nullable = false)
//    private String photo; // Фото (URL или путь)
//
//    @Column(nullable = false)
//    private double rating = 5.0; // Рейтинг по умолчанию
//
//    // Getters and Setters
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//
//    public String getName() { return name; }
//    public void setName(String name) { this.name = name; }
//
//    public Category getCategory() { return category; }
//    public void setCategory(Category category) { this.category = category; }
//
//    public Status getStatus() { return status; }
//    public void setStatus(Status status) { this.status = status; }
//
//    public String getAddress() { return address; }
//    public void setAddress(String address) { this.address = address; }
//
//    public String getDescription() { return description; }
//    public void setDescription(String description) { this.description = description; }
//
//    public String getAmenities() { return amenities; }
//    public void setAmenities(String amenities) { this.amenities = amenities; }
//
//    public LocalDate getDate() { return date; }
//    public void setDate(LocalDate date) { this.date = date; }
//
//    public String getPhoto() { return photo; }
//    public void setPhoto(String photo) { this.photo = photo; }
//
//    public double getRating() { return rating; }
//    public void setRating(double rating) { this.rating = rating; }
//}


package com.example.fitness.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Название

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.FREE; // Статус (Забронирован, Свободный)

    @Column(nullable = false)
    private String address; // Адрес

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; // Описание

    @Column(nullable = false)
    private String amenities; // Удобства

    @Column(nullable = false)
    private LocalDate date; // Время бронирования

    @Column(nullable = false)
    private String photo; // Фото (URL или путь)

    @Column(nullable = false)
    private double rating = 5.0; // Рейтинг по умолчанию

    @ManyToOne
    @JoinColumn(name = "booked_by")
    private User bookedBy;

    public User getBookedBy() {
        return bookedBy;
    }

    public void setBookedBy(User bookedBy) {
        this.bookedBy = bookedBy;
    }

    // Добавляем связь с User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Пользователь, который забронировал площадку

    // Getters и Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}

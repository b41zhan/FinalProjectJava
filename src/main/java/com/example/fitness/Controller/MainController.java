package com.example.fitness.Controller;


import com.example.fitness.Entity.*;
import com.example.fitness.Service.Implementation.UserServiceImpl;
import com.example.fitness.Service.LocationService;
import com.example.fitness.Service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class MainController {
    private final UserServiceImpl userService;
    private final LocationService locationService;
    private final PhotoService photoService;

    @Autowired
    public MainController(UserServiceImpl userService,
                          LocationService locationService,
                          PhotoService photoService) {
        this.userService = userService;
        this.locationService = locationService;
        this.photoService = photoService;
    }

    @GetMapping("/user")
    public String userPage(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(currentUser.getUsername());
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "userPage";
    }

    // Страница всех площадок
    @GetMapping("/user/locations")
    public String viewAllLocations(Model model) {
        List<Location> locations = locationService.findAll();
        model.addAttribute("locations", locations);
        return "viewUserLocations";
    }

    // Подробная информация о площадке
    @GetMapping("/user/locations/{id}")
    public String viewUserLocationDetails(@PathVariable Long id, Model model) {
        Location location = locationService.findById(id);
        if (location == null) {
            return "redirect:/user/locations";
        }
        model.addAttribute("location", location);
        return "locationDetails";
    }

    // Забронировать площадку
    @PostMapping("/user/locations/book/{id}")
    public String bookLocation(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails currentUser,
                               Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(currentUser.getUsername());
        if (user == null) {
            return "redirect:/login";
        }

        Location location = locationService.findById(id);
        if (location == null) {
            model.addAttribute("error", "Площадка не найдена.");
            return "redirect:/user/locations";
        }

        if (location.getStatus() == Status.BOOKED) {
            model.addAttribute("error", "Эта площадка уже забронирована.");
            return "redirect:/user/locations";
        }

        location.setStatus(Status.BOOKED);
        location.setUser(user);
        locationService.save(location);

        return "redirect:/user/booked-locations";
    }

    @PostMapping("/user/booked-locations/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(currentUser.getUsername());
        if (user == null) {
            return "redirect:/login";
        }

        // Найти площадку
        Location location = locationService.findById(id);
        if (location != null && location.getUser() != null && location.getUser().equals(user)) {
            location.setStatus(Status.FREE); // Устанавливаем статус "FREE"
            location.setUser(null);         // Убираем связь с пользователем
            locationService.save(location); // Сохраняем изменения
        }

        return "redirect:/user/booked-locations";
    }

    // Забронированные площадки
    @GetMapping("/user/booked-locations")
    public String viewBookedLocations(Model model,
                                      @AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(currentUser.getUsername());
        if (user == null) {
            return "redirect:/login";
        }

        List<Location> bookedLocations = locationService.findByUser(user);
        model.addAttribute("locations", bookedLocations);
        return "viewBookedLocations";
    }


    @PostMapping("/user/upload-photo")
    public String uploadUserPhoto(@RequestParam("photo") MultipartFile photo,
                                  @AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(currentUser.getUsername());
        if (user == null) {
            return "redirect:/login";
        }

        try {
            String filePath = photoService.savePhoto(photo, "profile");
            user.setProfilePhoto(filePath);
            userService.saveUserWithPhoto(user);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/user?error=photo";
        }

        return "redirect:/user";
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(currentUser.getUsername());
        if (user == null) {
            return "redirect:/login";
        }

        if ("ROLE_ADMIN".equals(user.getRole())) {
            return "redirect:/admin";
        } else if ("ROLE_USER".equals(user.getRole())) {
            return "redirect:/user";
        }

        return "redirect:/login";
    }

    @GetMapping("/admin")
    public String adminPage(@AuthenticationPrincipal UserDetails currentUser) {
        String email = currentUser.getUsername();
        User admin = userService.findByUsername(email);
        if (admin == null || !"ROLE_ADMIN".equals(admin.getRole())) {
            return "redirect:/login";
        }
        try {
            return "adminPage";
        }
        catch(Exception e){
            return "redirect:/login";
        }
    }


    @GetMapping("/admin/users")
    public String viewUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "viewUsers";
    }

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/locations")
    public String viewLocations(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        int pageSize = 3;
        Page<Location> locationPage;

        if (status != null && !status.isEmpty()) {
            try {
                Status enumStatus = Status.valueOf(status.toUpperCase()); // Преобразуем строку в Enum
                locationPage = locationService.findByStatus(enumStatus, PageRequest.of(page, pageSize));
            } catch (IllegalArgumentException e) {
                // Если статус невалиден, возвращаем пустую страницу
                model.addAttribute("locations", List.of());
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", 0);
                model.addAttribute("search", search);
                model.addAttribute("status", status);
                return "viewLocations";
            }
        } else if (search != null && !search.isEmpty()) {
            locationPage = locationService.findByNameContaining(search, PageRequest.of(page, pageSize));
        } else {
            locationPage = locationService.findAll(PageRequest.of(page, pageSize));
        }

        model.addAttribute("locations", locationPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", locationPage.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("status", status);

        return "viewLocations";
    }



    // Подробная информация о площадке
    @GetMapping("/admin/locations/{id}")
    public String viewLocationDetails(@PathVariable Long id, Model model) {
        Location location = locationService.findById(id);
        if (location == null) {
            return "redirect:/admin/locations";
        }
        model.addAttribute("location", location);
        return "locationDetails";
    }

    // Добавление новой площадки
    @GetMapping("/admin/locations/add")
    public String addLocationForm(Model model) {
        model.addAttribute("location", new Location());
        model.addAttribute("categories", Category.values());
        model.addAttribute("statuses", Status.values());
        return "addLocation";
    }

    @PostMapping("/admin/locations/add")
    public String addLocation(@ModelAttribute Location location) {
        locationService.save(location);
        return "redirect:/admin/locations";
    }

    @PostMapping("/admin/locations/delete/{id}")
    public String deleteLocation(@PathVariable Long id) {
        locationService.deleteById(id);
        return "redirect:/admin/locations";
    }

}
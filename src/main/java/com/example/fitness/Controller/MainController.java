package com.example.fitness.Controller;

import com.example.fitness.Entity.*;
import com.example.fitness.Service.EmailService;
import com.example.fitness.Service.Implementation.UserServiceImpl;
import com.example.fitness.Service.LocationService;
import com.example.fitness.Service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final EmailService emailService;

    @Autowired
    public MainController(UserServiceImpl userService,
                          LocationService locationService,
                          PhotoService photoService,
                          EmailService emailService) {
        this.userService = userService;
        this.locationService = locationService;
        this.photoService = photoService;
        this.emailService = emailService;
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

    @GetMapping("/user/locations")
    public String viewAllLocations(
            Model model,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "3") int size) {

        if (page < 0) {
            return "redirect:/user/locations?page=0&size=" + size + "&search=" + (search != null ? search : "")
                    + "&status=" + (status != null ? status : "") + "&category=" + (category != null ? category : "");
        }

        Page<Location> locationPage;
        Pageable pageable = PageRequest.of(page, size);

        try {
            if (category != null && !category.isEmpty() && status != null && !status.isEmpty()) {
                Category enumCategory = Category.valueOf(category.toUpperCase());
                Status enumStatus = Status.valueOf(status.toUpperCase());
                locationPage = locationService.findByCategoryAndStatusAndName(enumCategory, enumStatus, search, pageable);
            } else if (category != null && !category.isEmpty()) {
                Category enumCategory = Category.valueOf(category.toUpperCase());
                locationPage = locationService.findByCategoryAndName(enumCategory, search, pageable);
            } else if (status != null && !status.isEmpty()) {
                Status enumStatus = Status.valueOf(status.toUpperCase());
                locationPage = locationService.findByStatusAndName(enumStatus, search, pageable);
            } else {
                locationPage = locationService.findByName(search, pageable);
            }
        } catch (IllegalArgumentException e) {
            locationPage = Page.empty();
        }

        if (page >= locationPage.getTotalPages() && locationPage.getTotalPages() > 0) {
            return "redirect:/user/locations?page=" + (locationPage.getTotalPages() - 1) + "&size=" + size
                    + "&search=" + (search != null ? search : "") + "&status=" + (status != null ? status : "")
                    + "&category=" + (category != null ? category : "");
        }

        model.addAttribute("locations", locationPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", locationPage.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("category", category);

        return "viewUserLocations";
    }

    @GetMapping("/user/locations/{id}")
    public String viewUserLocationDetails(@PathVariable Long id, Model model) {
        Location location = locationService.findById(id);
        if (location == null) {
            return "redirect:/user/locations";
        }
        model.addAttribute("location", location);
        return "locationDetailsUser";
    }

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

        Location location = locationService.findById(id);
        if (location != null && location.getUser() != null && location.getUser().equals(user)) {
            location.setStatus(Status.FREE);
            location.setUser(null);
            locationService.save(location);
        }

        return "redirect:/user/booked-locations";
    }

    @GetMapping("/user/booked-locations")
    public String viewBookedLocations(
            Model model,
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "3") int size) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(currentUser.getUsername());
        if (user == null) {
            return "redirect:/login";
        }

        if (page < 0) {
            return "redirect:/user/booked-locations?page=0&size=" + size;
        }

        Page<Location> bookedLocationsPage = locationService.findByUser(user, PageRequest.of(page, size));

        if (page >= bookedLocationsPage.getTotalPages() && bookedLocationsPage.getTotalPages() > 0) {
            return "redirect:/user/booked-locations?page=" + (bookedLocationsPage.getTotalPages() - 1) + "&size=" + size;
        }

        model.addAttribute("locations", bookedLocationsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookedLocationsPage.getTotalPages());

        return "viewBookedLocations";
    }

    @GetMapping("/user/booked-locations/{id}")
    public String viewBookedLocationDetails(@PathVariable Long id, Model model) {
        Location location = locationService.findById(id);
        if (location == null) {
            return "redirect:/user/booked-locations";
        }
        model.addAttribute("location", location);
        return "locationDetails";
    }


    @GetMapping("/user/filter-locations")
    public String filterLocationsPage() {
        return "filterLocations";
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

    @GetMapping("/user/changePassword")
    public String changePasswordPage(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(currentUser.getUsername());
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("email", user.getEmail());
        return "changePassword";
    }

    @PostMapping("/user/sendCode")
    public String sendPasswordResetCode(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(currentUser.getUsername());
        if (user == null) {
            return "redirect:/login";
        }
        String code = String.valueOf((int) (Math.random() * 1000000));
        emailService.sendEmail(user.getEmail(), "Password Reset Code", "Your password reset code is: " + code);
        userService.savePasswordResetCode(user, code);
        model.addAttribute("email", user.getEmail());
        model.addAttribute("message", "Verification code sent to your email.");
        return "changePassword";
    }

    @PostMapping("/user/changePassword")
    public String changePasswordUser(@RequestParam("email") String email,
                                 @RequestParam("code") String code,
                                 @RequestParam("newPassword") String newPassword,
                                 Model model) {
        User user = userService.findByEmail(email);
        if (user == null || !userService.isResetCodeValid(user, code)) {
            model.addAttribute("error", "Invalid code or email.");
            return "changePassword";
        }
        userService.updatePassword(user, newPassword);
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
                Status enumStatus = Status.valueOf(status.toUpperCase());
                locationPage = locationService.findByStatus(enumStatus, PageRequest.of(page, pageSize));
            } catch (IllegalArgumentException e) {
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

    @GetMapping("/admin/locations/{id}")
    public String viewLocationDetails(@PathVariable Long id, Model model) {
        Location location = locationService.findById(id);
        if (location == null) {
            return "redirect:/admin/locations";
        }
        model.addAttribute("location", location);
        return "locationDetails";
    }

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

    @GetMapping("/reset-password")
    public String changePasswordPage(Model model) {
        System.out.println("GET request to /reset-password received");
        model.addAttribute("email", "");
        return "resetPassword";
    }

    @PostMapping("/sendCode")
    public String sendPasswordResetCode(@RequestParam("email") String email, Model model) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "Email not found.");
            return "resetPassword";
        }
        String code = String.valueOf((int) (Math.random() * 1000000));
        emailService.sendEmail(user.getEmail(), "Password Reset Code", "Your password reset code is: " + code);
        userService.savePasswordResetCode(user, code);

        model.addAttribute("message", "Verification code sent to your email.");
        model.addAttribute("email", email);
        return "resetPassword";
    }

    @PostMapping("/reset-password")
    public String changePassword(@RequestParam("email") String email,
                                 @RequestParam("code") String code,
                                 @RequestParam("newPassword") String newPassword,
                                 Model model) {
        User user = userService.findUserByEmail(email);
        if (user == null || !userService.isResetCodeValid(user, code)) {
            model.addAttribute("error", "Invalid code or email.");
            return "resetPassword";
        }

        userService.updatePassword(user, newPassword);
        SecurityContextHolder.clearContext();

        model.addAttribute("message", "Your password has been reset successfully! Please log in with your new password.");
        return "redirect:/login";
    }
}
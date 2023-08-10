package com.souvik.ems.misc;

import com.souvik.ems.model.User;
import com.souvik.ems.model.Role;
import com.souvik.ems.service.RoleService;
import com.souvik.ems.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.*;

@AllArgsConstructor
@Component
public class DataLoader implements ApplicationRunner {

    private UserService userService;
    private RoleService roleService;
    private PasswordEncoder passwordEncoder;

    void createRoles() {
        Optional<Role> isRolePresent_USER = roleService.findByName("USER");
        if (isRolePresent_USER.isEmpty()) {
            Role user = new Role();
            user.setName("USER");
            roleService.save(user);
        }
        Optional<Role> isRolePresent_ADMIN = roleService.findByName("ADMIN");
        if (isRolePresent_ADMIN.isEmpty()) {
            Role admin = new Role();
            admin.setName("ADMIN");
            roleService.save(admin);
        }
        createDefaultUsers();
    }

    void createDefaultUsers() {
        Optional<User> DEFAULT_USER = userService.findByEmail("user@example.com");
        User default_USER = DEFAULT_USER.orElseGet(User::new);
        default_USER.setFirstName("Normal");
        default_USER.setLastName("User");
        default_USER.setEmail("user@example.com");
        default_USER.setPassword("$2a$10$os1EIh6FDFsCenAYN64PKuzQsgzAFAK0sCcyVOQ40Zr4J/K8MC5ia");
        default_USER.setSalary(40000.0);
        if (default_USER.getRoles() == null || !default_USER.getRoles().contains("USER")) {
            default_USER.setRoles(new ArrayList<>());
            userService.saveUser(default_USER);
        }
        Role ROLE_USER = roleService.findByName("USER").get();
        List<Role> user_roles = default_USER.getRoles();
        user_roles.add(ROLE_USER);
        default_USER.setRoles(user_roles);
        userService.saveUser(default_USER);

        Optional<User> DEFAULT_ADMIN = userService.findByEmail("admin@example.com");
        User default_ADMIN = DEFAULT_ADMIN.orElseGet(User::new);
        default_ADMIN.setFirstName("Admin");
        default_ADMIN.setLastName("User");
        default_ADMIN.setEmail("admin@example.com");
        default_ADMIN.setPassword("$2a$10$GNuGG/.Zz8xYWK0OzHIlL.Vv1tLwV1qUVS4aGrgMTubuyX4HtwT.i");
        default_ADMIN.setSalary(60000.0);
        if (default_ADMIN.getRoles() == null || !default_ADMIN.getRoles().contains("ADMIN")) {
            default_ADMIN.setRoles(new ArrayList<>());
            userService.saveUser(default_ADMIN);
        }
        Role ROLE_ADMIN = roleService.findByName("ADMIN").get();
        List<Role> admin_roles = default_ADMIN.getRoles();
        admin_roles.add(ROLE_ADMIN);
        default_ADMIN.setRoles(admin_roles);
        userService.saveUser(default_ADMIN);

        createRandomUsers();
    }

    void createRandomUsers() {

        long USERS_TO_BE_ADDED = 50L - userService.getUserCount().longValue();
        if (USERS_TO_BE_ADDED <= 0) return;

        String[] _firstName =  new String[]{ "Adam", "Alex", "Aaron", "Alia", "Ben", "Carl", "Dan", "David", "Edward", "Fred", "Frank", "George", "Hal", "Hank", "Ike", "John", "Jack", "Joe", "Katie", "Larry", "Monte", "Matthew", "Mark", "Mary", "Nathan", "Otto", "Paul", "Peter", "Priya", "Roger", "Roger", "Steve", "Thomas", "Tim", "Ty", "Victor", "Walter"};
        String[] _lastName = new String[]{ "Anderson", "Ashwoon", "Aikin", "Bateman", "Bongard", "Bowers", "Boyd", "Cannon", "Cast", "Deitz", "Dewalt", "Ebner", "Frick", "Hancock", "Haworth", "Hesch", "Hoffman", "Kassing", "Knutson", "Lawless", "Lawicki", "Mccord", "McCormack", "Miller", "Myers", "Nugent", "Ortiz", "Orwig", "Ory", "Paiser", "Pak", "Pettigrew", "Quinn", "Quizoz", "Ramachandran", "Resnick", "Sagar", "Schickowski", "Schiebel", "Sellon", "Severson", "Shaffer", "Solberg", "Soloman", "Sonderling", "Soukup", "Soulis", "Stahl", "Sweeney", "Tandy", "Trebil", "Trusela", "Trussel", "Turco", "Uddin", "Uflan", "Ulrich", "Upson", "Vader", "Vail", "Valente", "Van Zandt", "Vanderpoel", "Ventotla", "Vogal", "Wagle", "Wagner", "Wakefield", "Weinstein", "Weiss", "Woo", "Yang", "Yates", "Yocum", "Zeaser", "Zeller", "Ziegler", "Bauer", "Baxster", "Casal", "Cataldi", "Caswell", "Celedon", "Chambers", "Chapman", "Christensen", "Darnell", "Davidson", "Davis", "DeLorenzo", "Dinkins", "Doran", "Dugelman", "Dugan", "Duffman", "Eastman", "Ferro", "Ferry", "Fletcher", "Fietzer", "Hylan", "Hydinger", "Illingsworth", "Ingram", "Irwin", "Jagtap", "Jenson", "Johnson", "Johnsen", "Jones", "Jurgenson", "Kalleg", "Kaskel", "Keller", "Leisinger", "LePage", "Lewis", "Linde", "Lulloff", "Maki", "Martin", "McGinnis", "Mills", "Moody", "Moore", "Napier", "Nelson", "Norquist", "Nuttle", "Olson", "Ostrander", "Reamer", "Reardon", "Reyes", "Rice", "Ripka", "Roberts", "Rogers", "Root", "Sandstrom", "Sawyer", "Schlicht", "Schmitt", "Schwager", "Schutz", "Schuster", "Tapia", "Thompson", "Tiernan", "Tisler" };
        String[] _type = new String[]{"USER", "ADMIN"};
        String[] _passwords = new String[]{"$2a$10$os1EIh6FDFsCenAYN64PKuzQsgzAFAK0sCcyVOQ40Zr4J/K8MC5ia", "$2a$10$GNuGG/.Zz8xYWK0OzHIlL.Vv1tLwV1qUVS4aGrgMTubuyX4HtwT.i"};

        for (int i = 0; i < USERS_TO_BE_ADDED; i++) {
            User user = new User();
            user.setFirstName(_firstName[new Random().nextInt(_firstName.length)]);
            user.setLastName(_lastName[new Random().nextInt(_lastName.length)]);
            StringBuilder email = new StringBuilder();
            email.append(user.getFirstName());
            email.append(user.getLastName());
            email.append("@example.com");
            while (userService.existsByEmail(email.toString())) {
                email.insert(2, String.valueOf(new Random().nextInt(10)));
            }
            user.setEmail(email.toString().toLowerCase());
            String userType = _type[new Random().nextInt(2)];
            user.setPassword(userType.equals("USER" ) ? _passwords[0] : _passwords[1]);
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            user.setSalary(Double.valueOf(decimalFormat.format(new Random().nextDouble(40000, 70000))));
            Role role = roleService.findByName(userType).get();
            List<Role> roles = new ArrayList<>();
            roles.add(role);
            user.setRoles(roles);
            userService.saveUser(user);
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createRoles();
    }

}

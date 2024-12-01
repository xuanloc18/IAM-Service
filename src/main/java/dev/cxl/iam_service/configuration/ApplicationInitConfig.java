package dev.cxl.iam_service.configuration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import dev.cxl.iam_service.entity.User;
import dev.cxl.iam_service.respository.RoleRepository;
import dev.cxl.iam_service.respository.UserRespository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ApplicationInitConfig {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @Bean
    @Transactional
    ApplicationRunner applicationRunner(UserRespository userRespository) {
        return args -> { // args đại diện cho các câu lệnh
            if (userRespository.findByUserMail("admin@gmail.com").isEmpty()) {
                Set<String> roles = new HashSet<>();
                roles.add("ADMIN");
                User user = User.builder()
                        .userMail("admin@gmail.com")
                        .passWord(passwordEncoder.encode("admin"))
                        .build();
                userRespository.save(user);
                log.warn("admin user has been reated with default password:admin,please change it");
            }
        };
    }
}

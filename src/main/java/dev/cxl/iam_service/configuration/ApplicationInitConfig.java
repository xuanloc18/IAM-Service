package dev.cxl.iam_service.configuration;

import dev.cxl.iam_service.entity.Role;
import dev.cxl.iam_service.entity.User;
import dev.cxl.iam_service.respository.RoleRepository;
import dev.cxl.iam_service.respository.UserRespository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
@Slf4j
@Configuration
public class ApplicationInitConfig {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleRepository roleRepository;

    @Bean
    @Transactional
    ApplicationRunner applicationRunner (UserRespository userRespository){
       return  args -> {//args đại diện cho các câu lệnh
           if(userRespository.findByUserMail("admin@gmail.com").isEmpty()){
              Set<Role> roles=new HashSet<>();
              var role=roleRepository.findById(dev.cxl.iam_service.enums.Role.ADMIN.name()).orElseThrow(()->new RuntimeException(""));
              roles.add(role);
               User user= User.builder()
                       .userMail("admin@gmail.com")
                       .passWord(passwordEncoder.encode("admin"))
                       .roles(roles)
                        .build();
               userRespository.save(user);
               log.warn("admin user has been reated with default password:admin,please change it");
           }
       };
    }

}

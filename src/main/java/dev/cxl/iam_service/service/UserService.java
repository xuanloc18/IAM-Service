package dev.cxl.iam_service.service;


import dev.cxl.iam_service.dto.request.UserCreationRequest;
import dev.cxl.iam_service.dto.request.UserRepalcePass;
import dev.cxl.iam_service.dto.request.UserUpdateRequest;
import dev.cxl.iam_service.dto.response.UserResponse;
import dev.cxl.iam_service.entity.Role;
import dev.cxl.iam_service.entity.User;
import dev.cxl.iam_service.exception.AppException;
import dev.cxl.iam_service.exception.ErrorCode;
import dev.cxl.iam_service.mapper.UserMapper;
import dev.cxl.iam_service.respository.RoleRepository;
import dev.cxl.iam_service.respository.UserRespository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRespository userRespository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleRepository  roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    public UserResponse createUser(UserCreationRequest request){

    if(userRespository.existsByUserMail(request.getUserMail())){
        throw new AppException(ErrorCode.USER_EXISTED);
    }
    User user=userMapper.toUser(request);
        user.setPassWord(passwordEncoder.encode(request.getPassWord()));
        HashSet<Role> roles=new HashSet<>();
        var role=roleRepository.findById(dev.cxl.iam_service.enums.Role.USER.name()).orElseThrow(()-> new RuntimeException(""));
        roles.add(role);
      user.setRoles(roles);
    return  userMapper.toUserResponse(userRespository.save(user)) ;
    }

    public UserResponse updareUser( UserUpdateRequest request){
        String userID=SecurityContextHolder.getContext().getAuthentication().getName();
        User user=userRespository.findById(userID).orElseThrow(()->new RuntimeException("user not found"));
        user=userMapper.updateUser(user,request);
        user.setPassWord(passwordEncoder.encode(request.getPassWord()));
       var roles=roleRepository.findAllById(request.getRoles());
       user.setRoles(new HashSet<>(roles));
        return userMapper.toUserResponse(userRespository.save(user));
    }
    public UserResponse getMyInfor(){
       var context= SecurityContextHolder.getContext();
       String id= context.getAuthentication().getName();
        User user=userRespository.findById(id).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
         return  userMapper.toUserResponse(user);
    }
    public Boolean replacePassword(UserRepalcePass userRepalcePass){
        var context= SecurityContextHolder.getContext();
        String id= context.getAuthentication().getName();
        User user = userRespository.findById(id).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
        log.info(user.getUserMail());
        log.info(user.getPassWord());
        Boolean checkPass = passwordEncoder.matches(userRepalcePass.getOldPassword(),user.getPassWord());
        if(!checkPass) throw new AppException(ErrorCode.INVALID_KEY);
        Boolean aBoolean = userRepalcePass.getConfirmPassword().equals(userRepalcePass.getNewPassword());
        if(!aBoolean) throw new RuntimeException("password does not confirm");

        user.setPassWord(passwordEncoder.encode(userRepalcePass.getNewPassword()));
        log.info(user.getPassWord());
        userRespository.save(user);
        return true;
    }


    public void del(String id){
      userRespository.deleteById(id);
    }
}

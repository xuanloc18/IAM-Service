package dev.cxl.iam_service.service;


import dev.cxl.iam_service.dto.request.PermissionRequest;
import dev.cxl.iam_service.dto.response.PermissionResponse;
import dev.cxl.iam_service.entity.Permission;
import dev.cxl.iam_service.mapper.PermissionMapper;
import dev.cxl.iam_service.respository.PermissionRespository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PermissionService {

    @Autowired
    PermissionRespository permissionRespository;
    @Autowired
    PermissionMapper mapper;

    public  PermissionResponse createPermission(PermissionRequest request){
        Permission permission=mapper.toPermission(request);
        return mapper.toPermissionResponse(permissionRespository.save(permission));
    }
    public  List<PermissionResponse> getListsPer(){
        return permissionRespository.findAll().stream().map(permission -> mapper.toPermissionResponse(permission)).toList();
    }

    public void deletePermission(String name){
        permissionRespository.deleteById(name);
    }



}

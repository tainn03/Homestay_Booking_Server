package com.homestay.service;

import com.homestay.dto.request.RoleRequest;
import com.homestay.dto.response.RoleResponse;
import com.homestay.model.Permission;
import com.homestay.model.Role;
import com.homestay.repository.PermissionRepository;
import com.homestay.repository.RoleRepository;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class RoleService {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PermissionRepository permissionRepository;

    public RoleResponse create(RoleRequest request) {
        var role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
//        var permissions = permissionRepository.findAllById(request.getPermissions());
//        role.setPermissions(new HashSet<>(permissions));
        role = roleRepository.save(role);
        return RoleResponse.builder()
                .name(role.getName())
                .description(role.getDescription())
                .permissions(request.getPermissions())
                .build();
    }

    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> RoleResponse.builder()
                        .name(role.getName())
                        .description(role.getDescription())
                        .permissions(role.getPermissions().stream()
                                .map(Permission::getName)
                                .toList())
                        .build())
                .toList();
    }

    public void delete(String name) {
        roleRepository.deleteById(name);
    }

    public RoleResponse update(String name, RoleRequest request) {
        var role = roleRepository.findById(name)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        role.setDescription(request.getDescription());
        role.setName(request.getName());
//        var permissions = permissionRepository.findAllById(request.getPermissions());
//        role.setPermissions(new HashSet<>(permissions));
        role = roleRepository.save(role);
        return RoleResponse.builder()
                .name(role.getName())
                .description(role.getDescription())
                .permissions(request.getPermissions())
                .build();
    }
}

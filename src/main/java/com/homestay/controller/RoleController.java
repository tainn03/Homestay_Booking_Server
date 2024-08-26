package com.homestay.controller;

import com.homestay.dto.ApiResponse;
import com.homestay.dto.request.RoleRequest;
import com.homestay.dto.response.RoleResponse;
import com.homestay.service.RoleService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleController {
    @Autowired
    RoleService roleService;

    @PostMapping
    public ApiResponse<RoleResponse> create(@RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .code(1000)
                .message("Success")
                .result(roleService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.<List<RoleResponse>>builder()
                .code(1000)
                .message("Success")
                .result(roleService.getAllRoles())
                .build();
    }

    @DeleteMapping("/{name}")
    public ApiResponse<Void> delete(@PathVariable String name) {
        roleService.delete(name);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Success")
                .build();
    }

    @PutMapping("/{name}")
    public ApiResponse<RoleResponse> update(@PathVariable String name, @RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .code(1000)
                .message("Success")
                .result(roleService.update(name, request))
                .build();
    }
}

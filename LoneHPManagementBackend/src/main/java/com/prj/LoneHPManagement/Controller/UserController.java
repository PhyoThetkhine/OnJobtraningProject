package com.prj.LoneHPManagement.Controller;


import com.prj.LoneHPManagement.Service.UserPermissionService;
import com.prj.LoneHPManagement.Service.impl.CIFService;
import com.prj.LoneHPManagement.Service.impl.UserServiceImpl;
import com.prj.LoneHPManagement.model.dto.ApiResponse;
import com.prj.LoneHPManagement.model.dto.UpdatedCifDTO;
import com.prj.LoneHPManagement.model.dto.UserUpdateDTO;
import com.prj.LoneHPManagement.model.entity.ConstraintEnum;
import com.prj.LoneHPManagement.model.entity.User;

import com.prj.LoneHPManagement.model.entity.UserPermission;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.exception.UserNotFoundException;
import com.prj.LoneHPManagement.model.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.rowset.serial.SerialException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private CIFService cifService;
    @GetMapping("/permissions/{userid}")
    public ResponseEntity<?> getPermissionByUserId(@PathVariable int userid) {
        List<UserPermission> permissions = userService.getPermissionByUserId(userid);
        ApiResponse<List<UserPermission>> response = ApiResponse.success(HttpStatus.OK.value(), "User Permission fetched successfully", permissions);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/emails")
    public ResponseEntity<Set<String>> getAllUniqueEmails() {
        Set<String> emails = cifService.getAllUniqueEmails();
        return ResponseEntity.ok(emails);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") int id, @RequestBody UserUpdateDTO updatedUser) {
        try {
            User user = userService.updateUser(id, updatedUser );
            return ResponseEntity.ok(user);
        } catch (ServiceException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/phoneNumbers")
    public ResponseEntity<Set<String>> getAllUniquePhoneNumbers() {
        Set<String> phoneNumbers = cifService.getAllUniquePhoneNumbers();
        return ResponseEntity.ok(phoneNumbers);
    }
    @GetMapping("/{branchId}/users")
    public ResponseEntity<?> getBranchUsers(
            @PathVariable int branchId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        sortBy = sortBy.trim();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<User> users = userService.getUsersByBranchId(branchId,pageable);

        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("content", users.getContent());
        responseData.put("totalPages", users.getTotalPages());
        responseData.put("totalElements", users.getTotalElements());
        responseData.put("size", users.getSize());
        responseData.put("number", users.getNumber());
        responseData.put("numberOfElements", users.getNumberOfElements());
        responseData.put("first", users.isFirst());
        responseData.put("last", users.isLast());
        responseData.put("empty", users.isEmpty());
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("user    List");
        response.setData(responseData);
        return ResponseEntity.ok(response);
    }



    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserPermissionService userPermissionService;
    @GetMapping("/allUsers") //GET http://localhost:8080/api/user/users?page=0&size=20&sortBy=id
    public ResponseEntity<?> getUserPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        sortBy = sortBy.trim();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<User> users = userService.getAllUser(pageable);
        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("content", users.getContent());
        responseData.put("totalPages", users.getTotalPages());
        responseData.put("totalElements", users.getTotalElements());
        responseData.put("size", users.getSize());
        responseData.put("number", users.getNumber());
        responseData.put("numberOfElements", users.getNumberOfElements());
        responseData.put("first", users.isFirst());
        responseData.put("last", users.isLast());
        responseData.put("empty", users.isEmpty());
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("user    List");
        response.setData(responseData);
        return ResponseEntity.ok(response);
    }


    @Autowired
    private UserRepository userRepository;

    @PostMapping("/saveUser")
    public ResponseEntity<ApiResponse<User>> saveUser(@RequestBody User user) {
        User savedUser = userService.save(user);
        ApiResponse<User> response = ApiResponse.success(HttpStatus.CREATED.value(), "User saved successfully", savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


//    @PostMapping("/register")
//    public ResponseEntity<ApiResponse<User>> registerUser(@RequestBody UserDTO userDTO) {
//
//        User user = userService.registerUser(userDTO);
//
////        UserDTO dto = new UserDTO();
////        dto.setId(user.getId());
////        dto.setUserCode(user.getUserCode());
////        dto.setStatus(user.getStatus());
//
//        ApiResponse<User> response = ApiResponse.success(
//                HttpStatus.CREATED.value(),
//                "User registered successfully", user);
//
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }

//    @GetMapping("/userList")
//    public ResponseEntity<ApiResponse<List<User>>> getUserList(){
//        List<User> userList = userService.getAllUsers();
//        ApiResponse<List<User>> list = ApiResponse.success(
//                HttpStatus.OK.value(),
//                "User List are found" , userList);
//
//        return  new ResponseEntity<>(list, HttpStatus.OK);
//    }



@GetMapping("/findUser/{id}")
public ResponseEntity<?> getUserById(@PathVariable Integer id) {
    User user = userService.getUserById(id);
    ApiResponse<User> response = ApiResponse.success(HttpStatus.OK.value(), "User fetched successfully", user);
    return ResponseEntity.ok(response);

}
    @PutMapping("/permissions/{userId}/{permissionId}")
    public ResponseEntity<ApiResponse<UserPermission>> updateUserPermission(
            @PathVariable Integer userId,
            @PathVariable Integer permissionId,
            @RequestBody Map<String, String> payload) {

        String status = payload.get("status");
        if (status == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Missing status"));
        }

        UserPermission updatedPermission = userPermissionService.updateUserPermission(userId, permissionId, status);
        return ResponseEntity.ok(ApiResponse.success(200, "Permission updated successfully", updatedPermission));
    }


    @PutMapping("/changeRole/{userId}")
    public ResponseEntity<ApiResponse<User>> changeUserRole(
            @PathVariable int userId,
            @RequestBody Map<String, Integer> payload) {

        Integer roleId = payload.get("roleId");
        if (roleId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Missing roleId"));
        }

        // Call the service to update the user's role.
        User updatedUser = userService.changeUserRole(userId, roleId);

        ApiResponse<User> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "User role updated successfully",
                updatedUser
        );
        return ResponseEntity.ok(response);
    }
//    @PutMapping("/users/{id}")
//    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable int id, @RequestBody UserDTO userDTO) {
//
//        User updatedUser = userService.updateUser(id, userDTO);
//
//        ApiResponse<User> response = ApiResponse.success(HttpStatus.OK.value(),
//                "User updated successfully", updatedUser);
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    @PutMapping("/changeStatus/{userId}")
    public ResponseEntity<ApiResponse<User>> changeUserStatus(
            @PathVariable int userId,
            @RequestBody Map<String, String> payload) {

        String status = payload.get("status");
        int statusCode;

        switch(status.toLowerCase()) {
            case "active":
                statusCode = ConstraintEnum.ACTIVE.getCode();
                break;
            case "terminated":
                statusCode = ConstraintEnum.TERMINATED.getCode();
                break;
            case "retired":
                statusCode = ConstraintEnum.RETIRED.getCode();
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid status value"));
        }

        User updatedUser = userService.changeUserStatus(userId, statusCode);
        ApiResponse<User> response = ApiResponse.success(
                HttpStatus.OK.value(), "User status updated successfully", updatedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable int id) {

        //userService.deleteUser(id);

        ApiResponse<String> response = ApiResponse.success(HttpStatus.OK.value(),
                "User deleted successfully", "User with ID " + id + " is deleted");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @PutMapping("/users/{id}/restore")
//    public ResponseEntity<ApiResponse<User>> restoreUser(@PathVariable int id) {
//
//       // User restoredUser = userService.restoreUser(id);
//
//        ApiResponse<User> response = ApiResponse.success(HttpStatus.OK.value(),
//                "User restored successfully", restoredUser);
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

//    @GetMapping("/branch/{branchCode}")
//    public ResponseEntity<Page<User>> getUsersByBranchCode(
//            @PathVariable String branchCode,
//            Pageable pageable) {
//        return ResponseEntity.ok(userService.findByBranchCode(branchCode, pageable));
//    }


}

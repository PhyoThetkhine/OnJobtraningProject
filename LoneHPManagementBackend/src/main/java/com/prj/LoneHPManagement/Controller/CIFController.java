package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.Service.impl.CIFService;
import com.prj.LoneHPManagement.model.dto.ApiResponse;
import com.prj.LoneHPManagement.model.dto.ClientRegistrationDTO;
import com.prj.LoneHPManagement.model.dto.UpdatedCifDTO;
import com.prj.LoneHPManagement.model.entity.CIF;
import com.prj.LoneHPManagement.model.exception.CifNotFoundException;
import com.prj.LoneHPManagement.model.repo.AddressRepository;
import com.prj.LoneHPManagement.model.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping("/api/cif")
public class CIFController {

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<CIF>> registerClient(@RequestBody ClientRegistrationDTO dto) {
        System.out.println("Received DTO: " + dto);
        try {
            CIF registeredCIF = cifService.registerClient(dto);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            HttpStatus.OK.value(),
                            "Client registered successfully",
                            registeredCIF
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null));
        }
    }
    @GetMapping("/allCIFToSelect/{currentUserId}")
    public ResponseEntity<ApiResponse<List<CIF>>> getCIFsToSelect(
            @PathVariable int currentUserId,
            @RequestParam(defaultValue = "") String searchTerm) {

        List<CIF> cifList = cifService.getCIFsToSelect(currentUserId, searchTerm);
        ApiResponse<List<CIF>> response = new ApiResponse<>(HttpStatus.OK.value(), "Success", cifList);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<CIF>> updateCIF(@PathVariable int id, @RequestBody UpdatedCifDTO updatedCifDTO) {
        CIF cif = cifService.updateCIF(id, updatedCifDTO);
        ApiResponse<CIF> response = new ApiResponse<>(HttpStatus.OK.value(), "CIF updated successfully", cif);
        return ResponseEntity.ok(response);
    }
    @Autowired
    private CIFService cifService;
    @GetMapping("/branch/{branchCode}")
    public ResponseEntity<?> getCIFsByBranchCode(
            @PathVariable String branchCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        // Validate sort parameter
        sortBy = sortBy.trim();


        // Create pagination object
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        // Get paginated results
        Page<CIF> cifs = cifService.getCIFsByBranchCode(branchCode, pageable);

        // Build response
        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        Map<String, Object> responseData = new HashMap<>();

        responseData.put("content", cifs.getContent());
        responseData.put("totalPages", cifs.getTotalPages());
        responseData.put("totalElements", cifs.getTotalElements());
        responseData.put("size", cifs.getSize());
        responseData.put("number", cifs.getNumber());
        responseData.put("numberOfElements", cifs.getNumberOfElements());
        responseData.put("first", cifs.isFirst());
        responseData.put("last", cifs.isLast());
        responseData.put("empty", cifs.isEmpty());

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("CIFs for branch code: " + branchCode);
        response.setData(responseData);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/emails")
    public ResponseEntity<Set<String>> getAllUniqueEmails() {
        Set<String> emails = cifService.getAllUniqueEmails();
        return ResponseEntity.ok(emails);
    }

    @GetMapping("/phoneNumbers")
    public ResponseEntity<Set<String>> getAllUniquePhoneNumbers() {
        Set<String> phoneNumbers = cifService.getAllUniquePhoneNumbers();
        return ResponseEntity.ok(phoneNumbers);
    }

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;
    @GetMapping("/state")
    public ResponseEntity<?> getCIFsByState(@RequestParam String state,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy) {
            sortBy = sortBy.trim();
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
            Page<CIF> cifs = cifService.findByState(state, pageable);
            ApiResponse<Map<String, Object>> response = new ApiResponse<>();
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", cifs.getContent());
            responseData.put("totalPages", cifs.getTotalPages());
            responseData.put("totalElements", cifs.getTotalElements());
            responseData.put("size", cifs.getSize());
            responseData.put("number", cifs.getNumber());
            responseData.put("numberOfElements", cifs.getNumberOfElements());
            responseData.put("first", cifs.isFirst());
            responseData.put("last", cifs.isLast());
            responseData.put("empty", cifs.isEmpty());
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("user    List");
            response.setData(responseData);
            return ResponseEntity.ok(response);
    }

    @GetMapping ("/allCIF")// GET http://localhost:8080/api/cifs?page=0&size=10&sortBy=id
    public ResponseEntity<?> getCIFPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        sortBy = sortBy.trim();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<CIF> cifs = cifService.getAllCIFs(pageable);

        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        Map<String, Object> responseData = new HashMap<>();

        responseData.put("content", cifs.getContent());
        responseData.put("totalPages", cifs.getTotalPages());
        responseData.put("totalElements", cifs.getTotalElements());
        responseData.put("size", cifs.getSize());
        responseData.put("number", cifs.getNumber());
        responseData.put("numberOfElements", cifs.getNumberOfElements());
        responseData.put("first", cifs.isFirst());
        responseData.put("last", cifs.isLast());
        responseData.put("empty", cifs.isEmpty());

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("CIF List Retrieved Successfully");
        response.setData(responseData);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/getBy/{id}")
    public ResponseEntity<?> getCIFById(@PathVariable Integer id) {
        CIF cif = cifService.getById(id);
        ApiResponse<CIF> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "CIF retrieved successfully",
                cif
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<CIF> registerCIF(@RequestBody CIF cif) {

        System.out.println("Received CIF Object: " + cif);

        if (cif.getCreatedUser() == null ) {
            System.out.println("Created User is NULL");
        } else {
            System.out.println("Created User ID: " + cif.getCreatedUser().getId());
        }
        CIF Recif = cifService.registerCIF(
                cif,
                cif.getAddress().getId(),
                cif.getCreatedUser().getId()
        );
        System.out.println(cif.getCreatedUser().getId());
        return ResponseEntity.ok(Recif);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<CIF>> getAllCIFs(Pageable pageable) {
        Page<CIF> cifPage = cifService.getAll(pageable);
        return ResponseEntity.ok(cifPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CIF> getCifById(@PathVariable int id) {
        try {
            CIF cif = cifService.getById(id);
            return ResponseEntity.ok(cif);
        } catch (CifNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/cifs/{id}")
    public ResponseEntity<ApiResponse<CIF>> updateCif(@PathVariable int id, @RequestBody CIF cif) {

        CIF updatedCif = cifService.updateCif(id, cif);

        ApiResponse<CIF> response = ApiResponse.success(HttpStatus.OK.value(),
                "Cif updated successfully", updatedCif);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
//
//    @GetMapping("/branch/{branchCode}")
//    public ResponseEntity<Page<CIF>> getCIFsByBranchCode(@PathVariable String branchCode, Pageable pageable) {
//        return ResponseEntity.ok(cifService.findCIFsByBranchCode(branchCode, pageable));
//    }

//    @GetMapping("/state")
//    public ResponseEntity<Page<CIF>> getCIFsByState(@RequestParam String state, Pageable pageable) {
//        return ResponseEntity.ok(cifService.findByState(state, pageable));
//    }


}





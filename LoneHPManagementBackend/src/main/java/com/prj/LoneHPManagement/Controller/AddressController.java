package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.Service.impl.AddressService;
import com.prj.LoneHPManagement.model.dto.ApiResponse;
import com.prj.LoneHPManagement.model.entity.Address;
import com.prj.LoneHPManagement.model.repo.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressService addressService;

    @PostMapping("/createAddress")
    public ResponseEntity<Address> createAddress(@RequestBody Address address){
        Address createAddress = addressService.createAddress(address);
        return new ResponseEntity<Address>(createAddress, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Address>> getAddressById(@PathVariable int id) {
        Address address = addressService.getAddressById(id);

        if (address != null) {
            ApiResponse<Address> response = ApiResponse.success(HttpStatus.OK.value(), "Address found", address);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<Address> response = ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Address not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}

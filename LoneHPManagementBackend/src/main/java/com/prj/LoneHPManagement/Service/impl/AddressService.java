package com.prj.LoneHPManagement.Service.impl;


import com.prj.LoneHPManagement.model.entity.Address;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public Address createAddress(Address address){

        return addressRepository.save(address);
    }

    public Address getAddressById(int id) {
        return addressRepository.findById(id).orElseThrow(() -> new ServiceException("Address Id Not Found"));
    }
}

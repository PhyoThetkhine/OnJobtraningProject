package com.prj.LoneHPManagement.Service.impl;


import com.prj.LoneHPManagement.model.entity.Address;
import com.prj.LoneHPManagement.model.entity.User;
import com.prj.LoneHPManagement.model.exception.UserNotFoundException;
import com.prj.LoneHPManagement.model.repo.AddressRepository;
import com.prj.LoneHPManagement.model.repo.BranchRepository;
import com.prj.LoneHPManagement.model.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private AddressRepository addressRepository;



    public User registerUser(User user ){

        Address address = new Address();
        address.setCity(user.getAddress().getCity());
        address.setState(user.getAddress().getState());
        address.setTownship(user.getAddress().getTownship());
        address.setAdditionalAddress(user.getAddress().getAdditionalAddress());
        Address savedAddress = addressRepository.save(address);
        user.setAddress(savedAddress);
        return userRepository.save(user);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(int id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        return user;
    }

    public User updateUser(int id, User user){
        User DBuser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        DBuser.setUserCode(user.getUserCode());
        DBuser.setEmail(user.getEmail());
        DBuser.setPhoneNumber(user.getPhoneNumber());

        return userRepository.save(user);
    }

    public User deleteUser(int id){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setStatus(14);

        return  userRepository.save(user);
    }

    public User restoreUser(int id){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setStatus(13);

        return userRepository.save(user);
    }

    public Page<User> findByBranchCode(String branchCode, Pageable pageable) {
        return userRepository.findByBranchCode(branchCode, pageable);
    }

}

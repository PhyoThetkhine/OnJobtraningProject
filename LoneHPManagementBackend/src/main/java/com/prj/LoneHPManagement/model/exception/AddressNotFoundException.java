package com.prj.LoneHPManagement.model.exception;

public class AddressNotFoundException extends RuntimeException{

        public AddressNotFoundException(String message){
            super(message);
        }
}

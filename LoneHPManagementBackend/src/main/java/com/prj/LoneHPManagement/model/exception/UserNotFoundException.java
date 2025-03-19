package com.prj.LoneHPManagement.model.exception;

public class UserNotFoundException extends RuntimeException{

        public UserNotFoundException(String message){
            super(message);
        }
}

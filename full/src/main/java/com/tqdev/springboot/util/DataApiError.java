package com.tqdev.springboot.util;


public class DataApiError {

    private String errorMessage;

    public DataApiError(String errorMessage){
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}

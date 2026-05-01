package com.example.filescanner.BEE;

public enum UserRole
{
    USER( "Bruger"),
    ADMIN("Admin");


    private final String displayName;

    UserRole(String displayName) {this.displayName = displayName;}


}


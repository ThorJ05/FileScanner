package com.example.filescanner.BEE;

public abstract class User {

    private final String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private final UserRole role;

   //constructor
    protected User(String id, String firstName, String lastName, String email, String password, UserRole role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters
    public String getId()                       {return id;}
    public String getFirstName()                {return firstName;}
    public String getLastName()                 {return lastName;}
    public String getEmail()                    {return email;}
    public String getRole()                     {return role.toString();}

    // Setters
    public void setFirstName(String fn)         {this.firstName = fn;}
    public void setLastName(String ln)          {this.lastName = ln;}
    public void setEmail(String em)             {this.email = em;}
    public void setPassword(String pw)          {this.password = pw;}

    public boolean checkPassword(String pw)     {return this.password != null && this.password.equals(pw);}



}

package com.jgermaine.fyp.rest.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "employee")
public class Employee {

	
	@Id
    @NotEmpty(message = "Please enter your email addresss.")
	@Email
    private String email;
	
    @NotEmpty
    @Pattern(regexp="[a-zA-Z0-9]")
    private String password;
    
    @NotEmpty
    private String firstName;
    
    @NotEmpty
    private String lastName;
    
    @NotEmpty
    private String phoneNum;
    
    
    private boolean assigned;
    
    public Employee() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
    
    public boolean getAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }
}

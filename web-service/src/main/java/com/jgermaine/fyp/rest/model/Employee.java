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
    @NotEmpty(message = "Please enter an email addresss.")
	@Email(message = "Please enter a valid email")
    private String email;
	
    @NotEmpty(message = "Please enter a password")
    @Pattern(regexp="[a-zA-Z0-9]", message="Password does not match criteria")
    private String password;
    
    @NotEmpty(message="Please enter a first name")
    private String firstName;
    
    @NotEmpty(message="Please enter a last name")
    private String lastName;
    
    @NotEmpty(message="Please enter a phone number")
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

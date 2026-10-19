/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package registrationlogin;

import java.util.Scanner;

public class RegistrationLogin {

    private String username;
    private String password;
    private String cellPhoneNumber;
    private String firstName;
    private String lastName;

    public RegistrationLogin(String username, String password, String cellPhoneNumber,
                              String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public boolean checkuserName() {
        if (username.contains("_") && username.length() <= 5) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPasswordComplexity() {
        boolean hasUpper = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (Character.isUpperCase(c)) {
                hasUpper = true;
            }
            if (Character.isDigit(c)) {
                hasNumber = true;
            }
            if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }

        if (password.length() >= 8 && hasUpper && hasNumber && hasSpecial) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkCellPhoneNumber() {
        if (cellPhoneNumber.startsWith("+") && cellPhoneNumber.length() <= 12) {
            for (int i = 1; i < cellPhoneNumber.length(); i++) {
                if (!Character.isDigit(cellPhoneNumber.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public String registerUser() {
        if (checkuserName() == false) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }

        if (checkPasswordComplexity() == false) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }

        return "Username successfully captured.\nPassword successfully captured.";
    }

    public boolean loginUser(String enteredUsername, String enteredPassword) {
        if (enteredUsername.equals(username) && enteredPassword.equals(password)) {
            return true;
        } else {
            return false;
        }
    }

    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful == true) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter a username (must contain an underscore and no more than 5 characters):");
        String username = scanner.nextLine();

        System.out.println("Please enter a password (min 8 characters, a capital letter, a number and a special character):");
        String password = scanner.nextLine();

        System.out.println("Please enter a South African cell phone number (with international code, e.g. +27...):");
        String cellPhoneNumber = scanner.nextLine();

        System.out.println("Please enter your first name:");
        String firstName = scanner.nextLine();

        System.out.println("Please enter your last name:");
        String lastName = scanner.nextLine();

        RegistrationLogin user = new RegistrationLogin(username, password, cellPhoneNumber, firstName, lastName);

        System.out.println(user.registerUser());

        System.out.println("Now please log in.");
        System.out.println("Enter username:");
        String loginUsername = scanner.nextLine();
        System.out.println("Enter password:");
        String loginPassword = scanner.nextLine();

        boolean loginResult = user.loginUser(loginUsername, loginPassword);
        System.out.println(user.returnLoginStatus(loginResult));

        scanner.close();
    }
}
   

   
  

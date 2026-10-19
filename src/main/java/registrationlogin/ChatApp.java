package registrationlogin;

import java.util.Random;
import java.util.Scanner;

public class ChatApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // registration, same as Part 1
        System.out.println("Register a new account");
        System.out.println("Enter a username:");
        String username = scanner.nextLine();
        System.out.println("Enter a password:");
        String password = scanner.nextLine();
        System.out.println("Enter your cell phone number (with international code):");
        String cellPhone = scanner.nextLine();
        System.out.println("Enter your first name:");
        String firstName = scanner.nextLine();
        System.out.println("Enter your last name:");
        String lastName = scanner.nextLine();

        RegistrationLogin user = new RegistrationLogin(username, password, cellPhone, firstName, lastName);
        System.out.println(user.registerUser());

        if (user.checkCellPhoneNumber() == false) {
            System.out.println("Cell phone number is incorrectly formatted or does not contain an international code.");
        }

        if (user.checkuserName() == false || user.checkPasswordComplexity() == false
                || user.checkCellPhoneNumber() == false) {
            System.out.println("Registration failed, please run the program and try again.");
            scanner.close();
            return;
        }

        // login, messages can only be sent if this works
        System.out.println("Now please log in.");
        System.out.println("Enter username:");
        String loginUsername = scanner.nextLine();
        System.out.println("Enter password:");
        String loginPassword = scanner.nextLine();

        boolean loggedIn = user.loginUser(loginUsername, loginPassword);
        System.out.println(user.returnLoginStatus(loggedIn));

        if (loggedIn == false) {
            scanner.close();
            return;
        }

        System.out.println("Welcome to QuickChat.");

        // the user decides how many messages they will enter
        System.out.println("How many messages do you wish to enter?");
        int numMessages = readNumber(scanner);
        while (numMessages < 1) {
            System.out.println("Please enter a number of 1 or more:");
            numMessages = readNumber(scanner);
        }

        int choice = 0;
        int entered = 0;
        int sentCount = 0;

        while (choice != 3) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            choice = readNumber(scanner);

            if (choice == 1) {

                if (entered >= numMessages) {
                    System.out.println("You have already entered all " + numMessages + " messages.");
                }

                while (entered < numMessages) {
                    System.out.println("Message " + (entered + 1) + " of " + numMessages);
                    System.out.println("Enter the recipient cell number:");
                    String recipient = scanner.nextLine().trim();
                    System.out.println("Enter your message:");
                    String text = scanner.nextLine();

                    Message msg = new Message(makeMessageID(), entered, recipient, text);

                    // check the cell number, ask again if it is wrong
                    String cellResult = msg.checkRecipientCell();
                    System.out.println(cellResult);
                    if (!cellResult.equals("Cell phone number successfully captured.")) {
                        continue;
                    }

                    // check the length, ask again if it is too long
                    String lengthResult = msg.checkMessageLength();
                    System.out.println(lengthResult);
                    if (!lengthResult.equals("Message ready to send.")) {
                        continue;
                    }

                    System.out.println("Message ID generated: " + msg.getMessageID());

                    // ask what to do with the message
                    String option = "";
                    while (!option.equals("1") && !option.equals("2") && !option.equals("3")) {
                        System.out.println("What would you like to do with this message?");
                        System.out.println("1) Send Message");
                        System.out.println("2) Disregard Message");
                        System.out.println("3) Store Message to send later");
                        option = scanner.nextLine().trim();
                    }

                    String result = "";
                    if (option.equals("1")) {
                        result = msg.SentMessage("send");
                        sentCount++;
                    } else if (option.equals("2")) {
                        result = msg.SentMessage("discard");
                    } else {
                        result = msg.SentMessage("store");
                    }
                    System.out.println(result);

                    if (option.equals("1")) {
                        // full details in the order the brief asks for
                        System.out.println("Message ID: " + msg.getMessageID());
                        System.out.println("Message Hash: " + msg.getMessageHash());
                        System.out.println("Recipient: " + msg.getRecipient());
                        System.out.println("Message: " + msg.getMessage());
                    } else if (option.equals("2")) {
                        String zero = scanner.nextLine().trim();
                        if (zero.equals("0")) {
                            System.out.println("Message deleted.");
                        }
                    }

                    entered++;
                }

                System.out.println("Total number of messages sent: " + sentCount);

            } else if (choice == 2) {
                System.out.println("Coming Soon.");
            } else if (choice == 3) {
                System.out.println("Total number of messages sent: " + sentCount);
                System.out.println("Goodbye.");
            } else {
                System.out.println("Invalid option, please choose 1, 2 or 3.");
            }
        }

        scanner.close();
    }

    // reads a whole line and turns it into a number, gives -1 if it is not a number
    private static int readNumber(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // builds a random 10 digit message ID one digit at a time
    private static String makeMessageID() {
        Random random = new Random();
        String id = "";
        for (int i = 0; i < 10; i++) {
            id = id + random.nextInt(10);
        }
        return id;
    }
}
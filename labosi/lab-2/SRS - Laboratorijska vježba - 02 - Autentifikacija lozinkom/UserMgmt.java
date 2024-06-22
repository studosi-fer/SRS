import java.io.*;
import java.nio.file.*;
import java.util.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserMgmt {
    private static final String FILE_NAME = "user_data.txt";
    private static Map<Integer, String> userMap = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);
    private static String randomString;
    /*private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";*/

    public static void main(String[] args) {
        System.out.println("Starting user management tool. Loading users...\n");
        try {
            if (Files.exists(Paths.get(FILE_NAME))) {
                List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
                if (lines.size() > 0) {
                    randomString = lines.get(0);
                    for (int i = 1; i < lines.size(); i++) {
                        String[] parts = lines.get(i).split(":");
                        userMap.put(Integer.valueOf(parts[0]), parts[1] + ":" + parts[2]);
                    }
                } else {
                    randomString = "a8Jk3tQsW7xNz2BnG9hDvYcXe1LfR6pTm5UoE4rI0";
                }
            } else {
                randomString = "a8Jk3tQsW7xNz2BnG9hDvYcXe1LfR6pTm5UoE4rI0";
            }
        } catch (IOException e) {
            System.err.println("Error loading user data.");
        }

        while (true) {
            System.out.print("Command (add, passwd, forcepass, del or exit): ");
            String command = scanner.nextLine().trim();


            if (command.equals("add") || command.equals("passwd")
                    || command.equals("forcepass") || command.equals("del")) {
                System.out.print("Username: ");
                String username = scanner.nextLine().trim();
                Integer usernameSaltedHash = (username + generateSalt(randomString)).hashCode();

                if (command.equals("add")) {
                    // Dodavanje novog korisničkog imena
                    if (userMap.containsKey(usernameSaltedHash)) {
                        System.out.println("FAILED: User already exists.");
                        continue;
                    }

                    if(addChangeUserPassword(username) == 0)
                        System.out.println("User " + username + " successfully added.");

                } else if (command.equals("passwd")) {
                    // Promjenu lozinke postojećeg korisničkog imena
                    if (!userMap.containsKey(usernameSaltedHash)) {
                        System.out.println("FAILED: User not found.");
                        continue;
                    }

                    if(addChangeUserPassword(username) == 0)
                        System.out.println("Password change successful.");
                } else if (command.equals("forcepass")) {
                    // Forsiranje promjene lozinke korisničkog imena
                    if (!userMap.containsKey(usernameSaltedHash)) {
                        System.out.println("FAILED: User not found.");
                        continue;
                    }

                    userMap.put(usernameSaltedHash, userMap.get(usernameSaltedHash).split(":")[0] + ":1");
                    saveUsersToFile();
                    System.out.println("User will be requested to change password on next login.");
                } else if (command.equals("del")) {
                    // Uklanjanje postojećeg korisničkog imena
                    if (!userMap.containsKey(usernameSaltedHash)) {
                        System.out.println("FAILED: User not found.");
                        continue;
                    }

                    userMap.remove((username + generateSalt(randomString)).hashCode());
                    saveUsersToFile();
                    System.out.println("User " + username + " successfully deleted.");
                }
            } else if (command.equals("exit")) {
                System.out.println("Exiting user management tool.");
                return;
            } else {
                System.out.println("Invalid command.");
            }

        }

    }

    private static Integer addChangeUserPassword(String username) {
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        if (!isPasswordValid(password)) {
            System.out.println("FAILED: Password doesn't meet requirements.");
            return -1; // error
        }

        System.out.print("Repeat Password: ");
        String confirmPassword = scanner.nextLine().trim();
        if (!password.equals(confirmPassword)) {
            System.out.println("FAILED: Passwords are not equal.");
            return -1; // error
        }

        userMap.put((username + generateSalt(randomString)).hashCode(),
                (password + generateSalt(randomString + username)).hashCode() + ":0");
        saveUsersToFile();
        return 0;
    }

    private static boolean isPasswordValid(String password) {
        String SpecialChars = "!@#$%^&*()-_=+[{]}|;:',<.>/?`~";
        boolean hasSpecialChars = false;
        boolean hasLowerCase = false;
        boolean hasUpperCase = false;

        for (char c : password.toCharArray()) {
            if (SpecialChars.indexOf(c) >= 0) {
                hasSpecialChars = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isUpperCase(c)){
                hasUpperCase = true;
            }
        }
        return hasSpecialChars && hasLowerCase && hasUpperCase && password.length() > 8;
    }

    private static void saveUsersToFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_NAME))) {
            writer.write(randomString);
            writer.newLine();
            for (Map.Entry<Integer, String> entry : userMap.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving user data.");
        }
    }


    private static String generateSalt(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] usernameBytes = input.getBytes(StandardCharsets.UTF_8);
            byte[] saltBytes = messageDigest.digest(usernameBytes);
            return Base64.getEncoder().encodeToString(saltBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to find SHA-256 algorithm", e);
        }
    }
}


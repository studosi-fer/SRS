import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Login {
    private static final String FILE_NAME = "user_data.txt";
    private static Map<Integer, String> userMap = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);
    private static String randomString;

    public static void main(String[] args) {
        System.out.println("Starting login tool. Loading users...\n");
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
                System.out.println("No users. Exiting login tool.");
                return;
            }
        } catch (IOException e) {
            System.err.println("Error loading user data.");
        }

        while (true) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            Integer usernameSaltedHash = (username + generateSalt(randomString)).hashCode();

            System.out.print("Password: ");
            String password = new String(System.console().readPassword());

            if (!userMap.containsKey(usernameSaltedHash)) {
                System.out.println("Username or password incorrect.");
                continue;
            }

            Integer passwordSaltedHash = (password + generateSalt(randomString + username)).hashCode();
            String[] lineSplits = userMap.get(usernameSaltedHash).split(":");

            if (passwordSaltedHash.equals(Integer.valueOf(lineSplits[0]))) {
                if (lineSplits[1].equals("1")) {
                    // if administrator requested password change
                    System.out.println("Administrator requested password change.");
                    System.out.print("New password: ");
                    String newPassword = new String(System.console().readPassword());
					
					if (((newPassword + generateSalt(randomString + username)).hashCode()).equals(passwordSaltedHash)) {
						System.out.println("FAILED: New password must not be the same as the old one.");
						continue;
					}	
					
					if (!isPasswordValid(newPassword)) {
						System.out.println("FAILED: Password doesn't meet requirements.");
						continue;
					}
		
                    System.out.print("Repeat new password: ");
                    String confirmPassword = new String(System.console().readPassword());

                    if (!newPassword.equals(confirmPassword)) {
                        System.out.println("Passwords are not equal.");
                        continue;
                    }

                    userMap.put(usernameSaltedHash, (newPassword + generateSalt(randomString + username)).hashCode() + ":0");
                    saveUsersToFile();
                }

                System.out.println("Login successful.");
            } else {
                System.out.println("Username or password incorrect.");
            }
        }
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
       

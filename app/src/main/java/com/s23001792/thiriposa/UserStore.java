package com.s23001792.thiriposa;

import java.util.HashMap;
import java.util.Map;

public class UserStore {
    // still map ID → User (for your record‐keeping)
    private static final Map<String, User> usersById   = new HashMap<>();
    // NEW: map username → User  (for login)
    private static final Map<String, User> usersByName = new HashMap<>();
    private static final Map<String, Integer> counters = new HashMap<>();

    static {
        counters.put("MOH Officer", 0);
        counters.put("Mother",     0);
        counters.put("Home Nurse", 0);
    }

    public static class User {
        public final String name;
        public final String userType;
        public final String idNumber;
        private      String password;

        public User(String name, String userType, String idNumber, String password) {
            this.name     = name;
            this.userType = userType;
            this.idNumber = idNumber;
            this.password = password;
        }

        public boolean checkPassword(String pin) {
            return this.password.equals(pin);
        }

        public void setPassword(String newPin) {
            this.password = newPin;
        }
    }

    /**
     * Adds a new user. Returns the internal ID (e.g. "MTH-001"), but
     * your UI can ignore it and just show "Signup successful".
     */
    public static String addUser(String name, String userType, String password) {
        // generate next numeric suffix
        int next = counters.get(userType) + 1;
        counters.put(userType, next);

        String prefix = userType.equals("MOH Officer") ? "MOH"
                : userType.equals("Mother")      ? "MTH"
                :                                 "NRS";
        String idNum = prefix + "-" + String.format("%03d", next);

        User u = new User(name, userType, idNum, password);
        usersById.put(idNum, u);
        usersByName.put(name.toLowerCase(), u);  // store lowercase for case‐insensitive lookup
        return idNum;
    }

    /**
     * Check login by username + pin.
     * @return the User object on success, or null on failure.
     */
    public static User authenticate(String name, String pin) {
        if (name == null) return null;
        User u = usersByName.get(name.toLowerCase());
        if (u != null && u.checkPassword(pin)) {
            return u;
        }
        return null;
    }

    /** Returns true if password was successfully changed. */
    public static boolean resetPassword(String name, String newPassword) {
        User u = usersByName.get(name.toLowerCase());
        if (u == null) return false;
        u.setPassword(newPassword);
        return true;
    }
}

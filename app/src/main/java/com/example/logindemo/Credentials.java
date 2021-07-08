package com.example.logindemo;

import java.util.HashMap;
import java.util.Map;

public class Credentials {
    private HashMap<String, String> credentialsMap = new HashMap<String,String>();

    //store credentials
    public void addCredentials(String username, String password) {
        credentialsMap.put(username, password);
    }

    //check if valid username
    public boolean alreadyTakenUsername(String username) {
        return credentialsMap.containsKey(username);
    }

    //check if valid pair
    public boolean checkCredentials(String username, String password) {
        return credentialsMap.containsKey(username) && credentialsMap.get(username).equals(password);
    }

    //to retrieve/load credentials
    public void loadCredentials(Map<String, ?> preferencesMap) {
        for (Map.Entry<String, ?> entry : preferencesMap.entrySet()) {
            //
            if (!entry.getKey().equals("shouldRemember") || !entry.getKey().equals("LastSavedUsername") ||
                    !entry.getKey().equals("LastSavedPassword")) {
                credentialsMap.put(entry.getKey(), entry.getValue().toString());
            }
        }
    }
}

package com.example.myapplication;

public class ReadwriterUserDetails {

    public String fullname;
    public String dob;
    public String gender;

    public String role;
    public String mobile;



    // No-argument constructor (required by Firebase)
    public ReadwriterUserDetails() {
        // Default constructor required for Firebase
    }

    public ReadwriterUserDetails(String textfullname, String textdob, String role, String textgender, String textmobile) {
        this.fullname = textfullname;
        this.dob = textdob;
        this.gender = textgender;
        this.role=role;
        this.mobile = textmobile;
    }
}

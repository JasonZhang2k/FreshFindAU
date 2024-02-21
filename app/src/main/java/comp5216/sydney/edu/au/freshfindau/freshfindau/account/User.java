package comp5216.sydney.edu.au.freshfindau.account;

public class User {
    public String username;
    public String email;
    public String gender;
    public int age;
    public String imageURL;

    public User() {
    }

    public User(String username, String email, String gender, int age) {
        this.username = username;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.imageURL = "";
    }
}

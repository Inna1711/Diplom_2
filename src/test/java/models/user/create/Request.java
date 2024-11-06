package models.user.create;

public class Request {
    private String name;
    private String password;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Request(String userName, String email){
        this.name = userName;
        this.email = email;
    }

    public Request(String userName, String email, String password){
        this.name = userName;
        this.email = email;
        this.password = password;
    }
}

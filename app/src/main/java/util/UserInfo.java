package util;

import android.app.Application;

public class UserInfo extends Application {
    public String userId;
    private String userName;
    private static UserInfo instance;
    public static UserInfo getInstance(){
        if (instance==null){
            instance=new UserInfo();

        }
        return instance;
    }

    public UserInfo() {
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

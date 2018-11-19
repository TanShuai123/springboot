package com.tan.mvc.principal;

import java.security.Principal;

public class MyPrincipal implements Principal {
    private String loginName;

    public MyPrincipal(String loginName) {
        this.loginName = loginName;
    }

    public String getName(){
        return loginName;
    }
}

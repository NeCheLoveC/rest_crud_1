package com.example.springsecr.models;

public enum RoleType
{
    USER("ROLE_USER"),
    MODERATOR("ROLE_MODERATOR"),
    ADMIN("ROLE_ADMIN");
    private String roleName;
    RoleType(String roleName)
    {
        this.roleName = roleName;
    }

    public String getRoleName()
    {
        return this.roleName;
    }
    public static final String ROLE_SUFFIX = "ROLE_";
}

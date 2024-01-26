package org.potato.util.web;

public interface ResponseCode {

    /**
     * login code
     */
    public static final int LOGIN_USERNAME_IS_REQUIRED = -1;
    public static final int LOGIN_USERNAME_IS_NOT_EXIST = -2;
    public static final int LOGIN_USERNAME_IS_DISABLED = -3;
    public static final int LOGIN_PASSWORD_IS_REQUIRED = -4;
    public static final int LOGIN_PASSWORD_IS_INCORRECT = -5;
    public static final int LOGIN_CLIENTTYPE_IS_REQUIRED = -6;

    /**
     * auth code
     */
    public static final int AUTH_TOKEN_IS_REQUIRED = 401;
    public static final int AUTH_TOKEN_IS_INVALID = 401;
    public static final int AUTH_TOKEN_IS_EXPIRED = 401;
    public static final int AUTH_TOKEN_IS_NO_PERMISSION = 401;
}
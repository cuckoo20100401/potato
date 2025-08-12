package org.potato.security.enumerate;

public enum Logical {
    DEFAULT("and"),
    AND("and"),
    OR("or");
    private String value;
    Logical(String value) { this.value = value; }
    public String value() { return value; }
}

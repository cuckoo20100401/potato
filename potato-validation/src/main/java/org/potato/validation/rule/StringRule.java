package org.potato.validation.rule;

public class StringRule {

    private ValidateType currentValidateType;
    private int limitLength;
    private int limitLengthMin;
    private int limitLengthMax;

    public enum ValidateType {
        NOT_NULL_AND_EMPTY,
        LENGTH_EQUAL,
        LENGTH_GREATER_THAN,
        LENGTH_LESS_THAN,
        LENGTH_BETWEEN,
        EMAIL,
        CELL_PHONE,
        TELE_PHONE;
        private ValidateType() {}
    }

    public static StringRule notNullAndEmpty() {
        StringRule rule = new StringRule();
        rule.currentValidateType = ValidateType.NOT_NULL_AND_EMPTY;
        return rule;
    }

    public static StringRule lengthEqual(int limitLength) {
        StringRule rule = new StringRule();
        rule.currentValidateType = ValidateType.LENGTH_EQUAL;
        rule.limitLength = limitLength;
        return rule;
    }

    public static StringRule lengthGreaterThan(int limitLength) {
        StringRule rule = new StringRule();
        rule.currentValidateType = ValidateType.LENGTH_GREATER_THAN;
        rule.limitLength = limitLength;
        return rule;
    }

    public static StringRule lengthLessThan(int limitLength) {
        StringRule rule = new StringRule();
        rule.currentValidateType = ValidateType.LENGTH_LESS_THAN;
        rule.limitLength = limitLength;
        return rule;
    }

    public static StringRule lengthBetween(int limitLengthMin, int limitLengthMax) {
        StringRule rule = new StringRule();
        rule.currentValidateType = ValidateType.LENGTH_BETWEEN;
        rule.limitLengthMin = limitLengthMin;
        rule.limitLengthMax = limitLengthMax;
        return rule;
    }

    public ValidateType getCurrentValidateType() {
        return currentValidateType;
    }

    public int getLimitLength() {
        return limitLength;
    }

    public int getLimitLengthMin() {
        return limitLengthMin;
    }

    public int getLimitLengthMax() {
        return limitLengthMax;
    }
}

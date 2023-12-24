package org.potato.validation.rule;

public class IntegerRule {

    private ValidateType currentValidateType;
    private int limitLength;
    private int limitLengthMin;
    private int limitLengthMax;

    public enum ValidateType {
        NOT_NULL,
        SIZE_EQUAL,
        SIZE_GREATER_THAN,
        SIZE_LESS_THAN,
        SIZE_BETWEEN;
        private ValidateType() {}
    }

    public static IntegerRule notNull() {
        IntegerRule rule = new IntegerRule();
        rule.currentValidateType = ValidateType.NOT_NULL;
        return rule;
    }

    public static IntegerRule lengthEqual(int limitLength) {
        IntegerRule rule = new IntegerRule();
        rule.currentValidateType = ValidateType.SIZE_EQUAL;
        rule.limitLength = limitLength;
        return rule;
    }

    public static IntegerRule lengthGreaterThan(int limitLength) {
        IntegerRule rule = new IntegerRule();
        rule.currentValidateType = ValidateType.SIZE_GREATER_THAN;
        rule.limitLength = limitLength;
        return rule;
    }

    public static IntegerRule lengthLessThan(int limitLength) {
        IntegerRule rule = new IntegerRule();
        rule.currentValidateType = ValidateType.SIZE_LESS_THAN;
        rule.limitLength = limitLength;
        return rule;
    }

    public static IntegerRule lengthBetween(int limitLengthMin, int limitLengthMax) {
        IntegerRule rule = new IntegerRule();
        rule.currentValidateType = ValidateType.SIZE_BETWEEN;
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

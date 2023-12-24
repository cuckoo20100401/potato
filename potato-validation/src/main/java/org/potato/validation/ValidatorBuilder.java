package org.potato.validation;

import org.potato.util.Result;
import org.potato.util.StringUtils;
import org.potato.util.web.DTO;
import org.potato.validation.rule.IntegerRule;
import org.potato.validation.rule.StringRule;

import java.util.ArrayList;
import java.util.List;

/**
 * ValidatorBuilder
 */
public class ValidatorBuilder {

    private DTO dto;
    private List<String> errorNames = new ArrayList<>();
    private List<String> errorMessages = new ArrayList<>();

    public ValidatorBuilder() {}

    public ValidatorBuilder(DTO dto) {
        this.dto = dto;
    }

    public ValidatorBuilder validateString(String name, StringRule rule, String errorMessage) {
        return this.validateString(name, dto.getAsString(name), rule, errorMessage);
    }

    public ValidatorBuilder validateString(String name, String value, StringRule rule, String errorMessage) {
        switch (rule.getCurrentValidateType()) {
            case NOT_NULL_AND_EMPTY:
                if (StringUtils.isNullOrEmpty(value)) {
                    this.addErrorNameAndMessage(name, errorMessage);
                }
                break;
            case LENGTH_EQUAL:
                if (value.length() != rule.getLimitLength()) {
                    this.addErrorNameAndMessage(name, errorMessage);
                }
                break;
            case LENGTH_GREATER_THAN:
                if (value.length() <= rule.getLimitLength()) {
                    this.addErrorNameAndMessage(name, errorMessage);
                }
                break;
            case LENGTH_LESS_THAN:
                if (value.length() >= rule.getLimitLength()) {
                    this.addErrorNameAndMessage(name, errorMessage);
                }
                break;
            case LENGTH_BETWEEN:
                if (value.length() < rule.getLimitLengthMin() || value.length() > rule.getLimitLengthMax()) {
                    this.addErrorNameAndMessage(name, errorMessage);
                }
                break;
            case EMAIL:
            case CELL_PHONE:
            case TELE_PHONE:
        }
        return this;
    }

    public ValidatorBuilder validateInteger(String name, IntegerRule rule, String errorMessage) {
        return this.validateInteger(name, dto.getAsInt(name), rule, errorMessage);
    }

    public ValidatorBuilder validateInteger(String name, Integer value, IntegerRule rule, String errorMessage) {
        switch (rule.getCurrentValidateType()) {
            case NOT_NULL:
                if (value == null) {
                    this.addErrorNameAndMessage(name, errorMessage);
                }
                break;
            case SIZE_EQUAL:
                if (value != rule.getLimitLength()) {
                    this.addErrorNameAndMessage(name, errorMessage);
                }
                break;
            case SIZE_GREATER_THAN:
                if (value <= rule.getLimitLength()) {
                    this.addErrorNameAndMessage(name, errorMessage);
                }
                break;
            case SIZE_LESS_THAN:
                if (value >= rule.getLimitLength()) {
                    this.addErrorNameAndMessage(name, errorMessage);
                }
                break;
            case SIZE_BETWEEN:
                if (value < rule.getLimitLengthMin() || value > rule.getLimitLengthMax()) {
                    this.addErrorNameAndMessage(name, errorMessage);
                }
                break;
        }
        return this;
    }

    public Result result() {
        if (!this.errorNames.isEmpty()) {
            return Result.failure().message("校验失败").addPayload("errorNames", errorNames).addPayload("errorMessages", errorMessages);
        }
        return Result.success().message("校验成功");
    }

    private void addErrorNameAndMessage(String errorName, String errorMessage) {
        if (!errorNames.contains(errorName)) {
            errorNames.add(errorName);
            errorMessages.add(errorMessage);
        }
    }
}

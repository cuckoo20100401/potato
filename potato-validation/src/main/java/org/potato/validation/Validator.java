package org.potato.validation;

import org.potato.util.DTO;

/**
 * Validator
 */
public class Validator {

    public static ValidatorBuilder builder() {
        return new ValidatorBuilder();
    }

    public static ValidatorBuilder builder(DTO dto) {
        return new ValidatorBuilder(dto);
    }
}

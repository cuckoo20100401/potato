package org.potato.validation;

import org.potato.util.web.DTO;

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

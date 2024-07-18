package org.potato.util;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

/**
 * DTO
 *
 * <p>
 *     可用于 Spring Controller 和 MyBatis Mapper.xml 等地方
 * </p>
 */
public class DTO extends LinkedHashMap<String, Object> {

    public String getAsString(String name) {
        return this.get(name) != null ? this.get(name).toString() : null;
    }

    public Integer getAsInt(String name) {
        return this.get(name) != null ? Integer.parseInt(this.get(name).toString()) : null;
    }

    public Long getAsLong(String name) {
        return this.get(name) != null ? Long.parseLong(this.get(name).toString()) : null;
    }

    public Double getAsDouble(String name) {
        return this.get(name) != null ? Double.parseDouble(this.get(name).toString()) : null;
    }

    public BigDecimal getAsBigDecimal(String name) {
        return this.get(name) != null ? new BigDecimal(this.get(name).toString()) : null;
    }

    public Boolean getAsBoolean(String name) {
        return this.get(name) != null ? Boolean.parseBoolean(this.get(name).toString()) : null;
    }
}

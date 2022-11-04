package com.prs.hub.constant;

import lombok.Getter;

/**
 * @author fanshupeng
 * @create 2022/10/21 17:05
 */
public enum LdEnum {

    EUR(0,"eur", "欧洲"),
    AFR(1,"afr", "非洲"),
    SAS(2,"sas", "南亚"),
    EAS(3,"eas", "东亚"),
    ;

    LdEnum(int code, String value,String message) {
        this.code = code;
        this.value = value;
        this.message = message;
    }

    @Getter
    private final int code;

    @Getter
    private final String value;

    @Getter
    private final String message;

    public static LdEnum valueOf(int value) {
        LdEnum[] enums = values();
        for (LdEnum enumItem : enums) {
            if (value == enumItem.getCode()) {
                return enumItem;
            }
        }
        return null;
    }
}

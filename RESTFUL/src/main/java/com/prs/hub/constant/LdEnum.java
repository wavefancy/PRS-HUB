package com.prs.hub.constant;

import lombok.Getter;

/**
 * @author fanshupeng
 * @create 2022/10/21 17:05
 */
public enum LdEnum {

    EUR(0,"eur", "欧洲","/pmaster/zhaohq/tmp/PTWDL/P_T.tar"),
    AFR(1,"afr", "非洲","/pmaster/zhaohq/tmp/PTWDL/P_T.tar"),
    SAS(2,"sas", "南亚","/pmaster/zhaohq/tmp/PTWDL/P_T.tar"),
    EAS(3,"eas", "东亚","/pmaster/zhaohq/tmp/PTWDL/P_T.tar"),
    ;

    LdEnum(int code, String value,String message,String path) {
        this.code = code;
        this.value = value;
        this.message = message;
        this.path = path;
    }

    @Getter
    private final int code;

    @Getter
    private final String value;

    @Getter
    private final String message;
    @Getter
    private final String path;

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

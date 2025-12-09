package com.cathaybk.demo.common;

// TODO 要更改異常代碼

public enum ReturnCodeAndDescEnum {
    SUCCESS("0000", "交易成功"),
    ERROR_INPUT("E001", "必填欄位不完整"),
    DATA_FORMAT_ERROR("E002", "資料格式錯誤"),
    DATA_DUPLICATE("E003", "新增失敗，資料重複"),
    DATA_NOT_FOUND("E702", "查無資料"),
    FAIL("E005", "更新失敗"),
    DELETE_ERROR("E006", "刪除失敗，查無資料"),
    SQL_ERROR("E007", "SQL有誤"),
    CONN_ERROR("E008", "系統連線異常"),
    S9999("9999", "其他系統異常");

    private String code;

    private String desc;

    private ReturnCodeAndDescEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
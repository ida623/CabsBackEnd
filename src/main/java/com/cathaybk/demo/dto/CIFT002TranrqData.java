package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * XXA-C-CIFT002 修改客戶資料
 */
@Data
public class CIFT002TranrqData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "orderId為必填欄位")
    @JsonProperty("orderId")
    private Long orderId;

    @NotBlank(message = "idNum為必填欄位")
    @JsonProperty("idNum")
    private String idNum;

    @NotBlank(message = "chineseName為必填欄位")
    @JsonProperty("chineseName")
    private String chineseName;

    @NotBlank(message = "gender為必填欄位")
    @Pattern(regexp = "^[mf]$", message = "性別只可填入 m 或 f")
    @JsonProperty("gender")
    private String gender;

    @NotBlank(message = "education為必填欄位")
    @JsonProperty("education")
    private String education;

    @NotBlank(message = "zipCode1為必填欄位")
    @Pattern(regexp = "^\\d+$", message = "郵遞區號只可填入數字")
    @JsonProperty("zipCode1")
    private String zipCode1;

    @NotBlank(message = "address1為必填欄位")
    @JsonProperty("address1")
    private String address1;

    @NotBlank(message = "telephone1為必填欄位")
    @Pattern(regexp = "^\\d{2}-\\d{7,8}$", message = "電話格式錯誤，應為 XX-XXXXXXX 或 XX-XXXXXXXX")
    @JsonProperty("telephone1")
    private String telephone1;

    @NotBlank(message = "zipCode2為必填欄位")
    @Pattern(regexp = "^\\d+$", message = "郵遞區號只可填入數字")
    @JsonProperty("zipCode2")
    private String zipCode2;

    @NotBlank(message = "address2為必填欄位")
    @JsonProperty("address2")
    private String address2;

    @NotBlank(message = "telephone2為必填欄位")
    @Pattern(regexp = "^\\d{2}-\\d{7,8}$", message = "電話格式錯誤，應為 XX-XXXXXXX 或 XX-XXXXXXXX")
    @JsonProperty("telephone2")
    private String telephone2;

    @NotBlank(message = "mobile為必填欄位")
    @Pattern(regexp = "^\\d+$", message = "行動電話只可填入數字")
    @JsonProperty("mobile")
    private String mobile;

    @Email(message = "email格式錯誤")
    @JsonProperty("email")
    private String email;

    @NotNull(message = "year為必填欄位")
    @JsonProperty("year")
    private Integer year;
}
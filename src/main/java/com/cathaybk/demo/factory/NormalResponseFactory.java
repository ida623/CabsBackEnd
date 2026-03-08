package com.cathaybk.demo.factory;

import com.cathaybk.demo.dto.MwHeader;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import org.springframework.stereotype.Component;


/**
 * 正常回應工廠
 * 用於產生標準的下行電文回應
 *
 * @author 00550396
 */
@Component
public class NormalResponseFactory {

    /** 成功代碼 */
    private static final String SUCCESS_CODE = "0000";

    /** 成功訊息 */
    private static final String SUCCESS_MESSAGE = "Success";

    /**
     * 產生正常回應
     *
     * @param <T> TRANRS 類型
     * @param <R> TRANRQ 類型
     * @param tranrs 下行電文內容
     * @param request 上行電文請求
     * @return ResponseTemplate
     */
    public <T, R> ResponseTemplate<T> genNormalResponse(T tranrs, RequestTemplate<R> request) {
        MwHeader requestHeader = request.getMwheader();
        
        MwHeader responseHeader = new MwHeader();
        responseHeader.setMsgid(requestHeader.getMsgid());
        responseHeader.setSourcechannel(requestHeader.getSourcechannel());
        responseHeader.setTxnseq(requestHeader.getTxnseq());
        responseHeader.setReturncode(SUCCESS_CODE);
        responseHeader.setReturndesc(SUCCESS_MESSAGE);
        responseHeader.setO360seq(null);

        return new ResponseTemplate<>(responseHeader, tranrs);
    }

    /**
     * 產生正常回應（自訂回應訊息）
     *
     * @param <T> TRANRS 類型
     * @param <R> TRANRQ 類型
     * @param tranrs 下行電文內容
     * @param request 上行電文請求
     * @param returnDesc 自訂回應訊息
     * @return ResponseTemplate
     */
    public <T, R> ResponseTemplate<T> genNormalResponse(T tranrs, RequestTemplate<R> request, String returnDesc) {
        MwHeader requestHeader = request.getMwheader();
        
        MwHeader responseHeader = new MwHeader();
        responseHeader.setMsgid(requestHeader.getMsgid());
        responseHeader.setSourcechannel(requestHeader.getSourcechannel());
        responseHeader.setTxnseq(requestHeader.getTxnseq());
        responseHeader.setReturncode(SUCCESS_CODE);
        responseHeader.setReturndesc(returnDesc);
        responseHeader.setO360seq(null);

        return new ResponseTemplate<>(responseHeader, tranrs);
    }

}
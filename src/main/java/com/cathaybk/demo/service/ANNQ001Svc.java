package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.ANNQ001Tranrq;
import com.cathaybk.demo.dto.ANNQ001Tranrs;
import java.io.IOException;


/**
 * CABS-B-ANNQ001 查詢公告清單
 * @author
 */
public interface ANNQ001Svc {
    ANNQ001Tranrs queryAnnouncementList(ANNQ001Tranrq tranrq) throws IOException;
}
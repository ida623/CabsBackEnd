package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.ANNQ002Tranrq;
import com.cathaybk.demo.dto.ANNQ002Tranrs;
import com.cathaybk.demo.exception.DataNotFoundException;

/**
 * CABS-B-ANNQ002 查詢公告
 * @author
 */
public interface ANNQ002Svc {
    ANNQ002Tranrs queryAnnouncement(ANNQ002Tranrq tranrq) throws DataNotFoundException;
}
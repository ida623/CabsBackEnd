package com.cathaybk.demo.client;

import com.cathaybk.demo.dto.CabsListO001Tranrq;
import com.cathaybk.demo.dto.CabsListO001Tranrs;

import java.util.List;

/**
 * CFS-C-CABSLISTO001 CABS審核狀態回寫
 */
public interface IContactClient {

    CabsListO001Tranrs callCabsListO001(CabsListO001Tranrq tranrq);

}
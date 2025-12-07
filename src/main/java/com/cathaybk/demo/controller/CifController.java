package com.cathaybk.demo.controller;

import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.InsertFailException;
import com.cathaybk.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/cif")
public class CifController {

    @Autowired
    private CifQ001Svc cifQ001Svc;

    @Autowired
    private CifQ002Svc cifQ002Svc;

    @Autowired
    private CifQ003Svc cifQ003Svc;

    @Autowired
    private CifQ004Svc cifQ004Svc;

    @Autowired
    private CifT001Svc cifT001Svc;

    @Autowired
    private CifT002Svc cifT002Svc;

    @Autowired
    private CifT003Svc cifT003Svc;

    @PostMapping("/getOne")
    public ResponseTemplate<CIFQ001Tranrs> getOne(@Validated @RequestBody RequestTemplate<CIFQ001Tranrq> request) throws DataNotFoundException, IOException {
        return cifQ001Svc.getOne(request);
    }

    @PostMapping("/filter")
    public ResponseTemplate<CIFQ002Tranrs> filter(@Validated @RequestBody RequestTemplate<CIFQ002Tranrq> request) throws DataNotFoundException, IOException {
        return cifQ002Svc.filter(request);
    }

    @PostMapping("/checkId")
    public ResponseTemplate<EmptyTranrs> checkId(@Validated @RequestBody RequestTemplate<CIFQ003Tranrq> request) throws InsertFailException, IOException {
        return cifQ003Svc.checkId(request);
    }

    @PostMapping("/commCode")
    public ResponseTemplate<CIFQ004Tranrs> commCode(@Validated @RequestBody RequestTemplate<CIFQ004Tranrq> request) throws IOException {
        return cifQ004Svc.commCode(request);
    }

    @PostMapping("/createInfo")
    public ResponseTemplate<EmptyTranrs> createInfo(@Validated @RequestBody RequestTemplate<CIFT001Tranrq> request) throws IOException, InsertFailException {
        return cifT001Svc.createInfo(request);
    }

    @PostMapping("/editInfo")
    public ResponseTemplate<EmptyTranrs> editInfo(@Validated @RequestBody RequestTemplate<CIFT002Tranrq> request) throws DataNotFoundException, IOException {
        return cifT002Svc.editInfo(request);
    }

    @PostMapping("/deleteInfo")
    public ResponseTemplate<EmptyTranrs> deleteInfo(@Validated @RequestBody RequestTemplate<CIFT003Tranrq> request) throws DataNotFoundException, IOException {
        return cifT003Svc.deleteInfo(request);
    }
}
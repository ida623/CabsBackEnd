package com.cathaybk.demo.controller;

import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.InsertFailException;
import com.cathaybk.demo.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/cif")
@CrossOrigin("http://localhost:4200")
@RequiredArgsConstructor
public class CifController {

    private final CIFQ001SvcMapStruct cifQ001SvcMapStruct;
    private final CIFQ001SvcModelMapper cifQ001SvcModelMapper;
    private final CIFQ001SvcOrika cifQ001SvcOrika;
    private final CIFQ001SvcApacheBeanUtils cifQ001SvcApacheBeanUtils;
    private final CIFQ001SvcSpringBeanUtils cifQ001SvcSpringBeanUtils;
    private final CIFQ001Svc cifQ001Svc;
    private final CIFQ002Svc cifQ002Svc;
    private final CIFQ003Svc cifQ003Svc;
    private final CIFQ004Svc cifQ004Svc;
    private final CIFT001SvcMapStruct cifT001SvcMapStruct;
    private final CIFT001SvcModelMapper cifT001SvcModelMapper;
    private final CIFT001SvcOrika cifT001SvcOrika;
    private final CIFT001SvcApacheBeanUtils cifT001SvcApacheBeanUtils;
    private final CIFT001SvcSpringBeanUtils cifT001SvcSpringBeanUtils;
    private final CIFT001Svc cifT001Svc;
    private final CIFT002SvcMapStruct cifT002SvcMapStruct;
    private final CIFT002SvcModelMapper cifT002SvcModelMapper;
    private final CIFT002SvcOrika cifT002SvcOrika;
    private final CIFT002SvcApacheBeanUtils cifT002SvcApacheBeanUtils;
    private final CIFT002SvcSpringBeanUtils cifT002SvcSpringBeanUtils;
    private final CIFT002Svc cifT002Svc;
    private final CIFT003Svc cifT003Svc;

    @PostMapping("/getOneMS")
    public ResponseTemplate<CIFQ001Tranrs> getOneMapStruct(@Validated @RequestBody RequestTemplate<CIFQ001Tranrq> request) throws DataNotFoundException, IOException {
        return cifQ001SvcMapStruct.getOneMapStruct(request);
    }

    @PostMapping("/getOneMM")
    public ResponseTemplate<CIFQ001Tranrs> getOneModelMapper(@Validated @RequestBody RequestTemplate<CIFQ001Tranrq> request) throws DataNotFoundException, IOException {
        return cifQ001SvcModelMapper.getOneModelMapper(request);
    }

    @PostMapping("/getOneOk")
    public ResponseTemplate<CIFQ001Tranrs> getOneOrika(@Validated @RequestBody RequestTemplate<CIFQ001Tranrq> request) throws DataNotFoundException, IOException {
        return cifQ001SvcOrika.getOneOrika(request);
    }

    @PostMapping("/getOneAB")
    public ResponseTemplate<CIFQ001Tranrs> getOneApacheBeanUtils(@Validated @RequestBody RequestTemplate<CIFQ001Tranrq> request) throws DataNotFoundException, IOException {
        return cifQ001SvcApacheBeanUtils.getOneApacheBeanUtils(request);
    }

    @PostMapping("/getOneSB")
    public ResponseTemplate<CIFQ001Tranrs> getOneSpringBeanUtils(@Validated @RequestBody RequestTemplate<CIFQ001Tranrq> request) throws DataNotFoundException, IOException {
        return cifQ001SvcSpringBeanUtils.getOneSpringBeanUtils(request);
    }

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

    @PostMapping("/createInfoMS")
    public ResponseTemplate<EmptyTranrs> createInfoMapStruct(@Validated @RequestBody RequestTemplate<CIFT001Tranrq> request) throws IOException, InsertFailException {
        return cifT001SvcMapStruct.createInfoMapStruct(request);
    }

    @PostMapping("/createInfoMM")
    public ResponseTemplate<EmptyTranrs> createInfoModelMapper(@Validated @RequestBody RequestTemplate<CIFT001Tranrq> request) throws IOException, InsertFailException {
        return cifT001SvcModelMapper.createInfoModelMapper(request);
    }

    @PostMapping("/createInfoOk")
    public ResponseTemplate<EmptyTranrs> createInfoOrika(@Validated @RequestBody RequestTemplate<CIFT001Tranrq> request) throws IOException, InsertFailException {
        return cifT001SvcOrika.createInfoOrika(request);
    }

    @PostMapping("/createInfoAB")
    public ResponseTemplate<EmptyTranrs> createInfoApacheBeanUtils(@Validated @RequestBody RequestTemplate<CIFT001Tranrq> request) throws IOException, InsertFailException {
        return cifT001SvcApacheBeanUtils.createInfoApacheBeanUtils(request);
    }

    @PostMapping("/createInfoSB")
    public ResponseTemplate<EmptyTranrs> createInfoSpringBeanUtils(@Validated @RequestBody RequestTemplate<CIFT001Tranrq> request) throws IOException, InsertFailException {
        return cifT001SvcSpringBeanUtils.createInfoSpringBeanUtils(request);
    }

    @PostMapping("/createInfo")
    public ResponseTemplate<EmptyTranrs> createInfo(@Validated @RequestBody RequestTemplate<CIFT001Tranrq> request) throws IOException, InsertFailException {
        return cifT001Svc.createInfo(request);
    }

    @PostMapping("/editInfoMS")
    public ResponseTemplate<EmptyTranrs> editInfoMapStruct(@Validated @RequestBody RequestTemplate<CIFT002Tranrq> request) throws DataNotFoundException, IOException {
        return cifT002SvcMapStruct.editInfoMapStruct(request);
    }

    @PostMapping("/editInfoMM")
    public ResponseTemplate<EmptyTranrs> editInfoModelMapper(@Validated @RequestBody RequestTemplate<CIFT002Tranrq> request) throws DataNotFoundException, IOException {
        return cifT002SvcModelMapper.editInfoModelMapper(request);
    }

    @PostMapping("/editInfoOk")
    public ResponseTemplate<EmptyTranrs> editInfoOrika(@Validated @RequestBody RequestTemplate<CIFT002Tranrq> request) throws DataNotFoundException, IOException {
        return cifT002SvcOrika.editInfoOrika(request);
    }

    @PostMapping("/editInfoAB")
    public ResponseTemplate<EmptyTranrs> editInfoApacheBeanUtils(@Validated @RequestBody RequestTemplate<CIFT002Tranrq> request) throws DataNotFoundException, IOException {
        return cifT002SvcApacheBeanUtils.editInfoApacheBeanUtils(request);
    }

    @PostMapping("/editInfoSB")
    public ResponseTemplate<EmptyTranrs> editInfoSpringBeanUtils(@Validated @RequestBody RequestTemplate<CIFT002Tranrq> request) throws DataNotFoundException, IOException {
        return cifT002SvcSpringBeanUtils.editInfoSpringBeanUtils(request);
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
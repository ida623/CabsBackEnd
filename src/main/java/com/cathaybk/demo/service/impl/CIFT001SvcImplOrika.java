package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import com.cathaybk.demo.exception.InsertFailException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CIFT001SvcOrika;
import jakarta.transaction.Transactional;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CIFT001SvcImplOrika implements CIFT001SvcOrika {

    private final CustomerInfoRepository customerInfoRepository;
    private final MapperFacade orikaMapper;

    public CIFT001SvcImplOrika(CustomerInfoRepository customerInfoRepository) {
        this.customerInfoRepository = customerInfoRepository;

    // 初始化 Orika Mapper，管理映射配置
        MapperFactory factory = new DefaultMapperFactory.Builder()
                // 設定編譯策略為 EclipseJdtCompilerStrategy
                // 從 JDK 9 開始，反射存取權限變得更嚴格，預設的 Javassist 直接產生位元碼會失敗
                // 改用 Eclipse JDT 編譯器可以避免反射存取限制問題，確保 Orika 能正常運行
                .compilerStrategy(new EclipseJdtCompilerStrategy())
                .useBuiltinConverters(false)                        // 停用內建的型別轉換器
                .build();                                           // 建立 MapperFactory 實例

    // 定義「來源類別」到「目標類別」的映射規則
        factory.classMap(CIFT001TranrqData.class, CustomerInfoEntity.class)
                .byDefault()                                        // 使用預設映射規則：對應名稱相同的欄位
                .register();                                        // 註冊映射關係

        this.orikaMapper = factory.getMapperFacade();               // 後續呼叫 orikaMapper.map() 可以進行物件轉換
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<EmptyTranrs> createInfoOrika(RequestTemplate<CIFT001Tranrq> request) throws InsertFailException {

        CIFT001Tranrq tranrq = request.getTranrq();
        CIFT001TranrqData data = tranrq.getData();

        // 檢查身份證號是否已存在
        List<CustomerInfoEntity> existingCustomers = customerInfoRepository.findByIdNum(data.getIdNum());
        if (!existingCustomers.isEmpty()) {
            throw new InsertFailException("身份證號已存在");
        }

        // 使用 Orika 進行映射
        CustomerInfoEntity customerInfoEntity = orikaMapper.map(data, CustomerInfoEntity.class);

        // 執行新增
        try {
            customerInfoRepository.save(customerInfoEntity);
        } catch (Exception e) {
            throw new InsertFailException("新增失敗");
        }

        ResponseTemplate<EmptyTranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER(
                request.getMwheader().getMsgid(),
                ReturnCodeAndDescEnum.SUCCESS.getCode(),
                ReturnCodeAndDescEnum.SUCCESS.getDesc()
        );
        response.setMwheader(header);
        response.setTranrs(new EmptyTranrs());

        return response;
    }
}
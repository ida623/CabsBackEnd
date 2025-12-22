package com.cathaybk.demo.benchmark;

import com.cathaybk.demo.dto.CIFT001TranrqData;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import com.cathaybk.demo.mapper.CustomerInfoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy;
import org.apache.commons.beanutils.BeanUtils;
import org.mapstruct.factory.Mappers;
import org.modelmapper.ModelMapper;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime, Mode.Throughput})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
@Fork(value = 2, warmups = 1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class MapperPerformanceBenchmark {

    private CIFT001TranrqData sourceData;
    private ObjectMapper objectMapper;
    private ModelMapper modelMapper;
    private MapperFacade orikaMapper;
    private CustomerInfoMapper mapStructMapper;

    @Setup
    public void setup() {
        // 初始化測試數據
        sourceData = createTestData();

        // 初始化 ObjectMapper (Jackson)
        objectMapper = new ObjectMapper();

        // 初始化 ModelMapper
        modelMapper = new ModelMapper();

        // 初始化 Orika Mapper
        MapperFactory factory = new DefaultMapperFactory.Builder()
                .compilerStrategy(new EclipseJdtCompilerStrategy())
                .useBuiltinConverters(false)
                .build();
        factory.classMap(CIFT001TranrqData.class, CustomerInfoEntity.class)
                .byDefault()
                .register();
        orikaMapper = factory.getMapperFacade();

        // 初始化 MapStruct Mapper - 使用 Mappers.getMapper()
        mapStructMapper = Mappers.getMapper(CustomerInfoMapper.class);
    }

    /**
     * 建立測試數據
     */
    private CIFT001TranrqData createTestData() {
        CIFT001TranrqData data = new CIFT001TranrqData();
        data.setIdNum("K123456735");
        data.setChineseName("測試使用者");
        data.setGender("m");
        data.setEducation("4");
        data.setZipCode1("100");
        data.setAddress1("台北市中正區忠孝東路一段1號");
        data.setTelephone1("02-12345678");
        data.setZipCode2("104");
        data.setAddress2("台北市中山區南京東路二段2號");
        data.setTelephone2("02-87654321");
        data.setMobile("0912345678");
        data.setEmail("test@example.com");
        data.setYear(5);
        return data;
    }

    @Benchmark
    public CustomerInfoEntity testObjectMapper() {
        return objectMapper.convertValue(sourceData, CustomerInfoEntity.class);
    }

    @Benchmark
    public CustomerInfoEntity testMapStruct() {
        return mapStructMapper.toEntity(sourceData);
    }

    @Benchmark
    public CustomerInfoEntity testModelMapper() {
        return modelMapper.map(sourceData, CustomerInfoEntity.class);
    }

    @Benchmark
    public CustomerInfoEntity testOrika() {
        return orikaMapper.map(sourceData, CustomerInfoEntity.class);
    }

    @Benchmark
    public CustomerInfoEntity testSpringBeanUtils() {
        CustomerInfoEntity entity = new CustomerInfoEntity();
        org.springframework.beans.BeanUtils.copyProperties(sourceData, entity);
        return entity;
    }

    @Benchmark
    public CustomerInfoEntity testApacheBeanUtils() {
        CustomerInfoEntity entity = new CustomerInfoEntity();
        try {
            BeanUtils.copyProperties(entity, sourceData);
        } catch (Exception e) {
            throw new RuntimeException("Mapping 失敗", e);
        }
        return entity;
    }

    /**
     * 執行基準測試
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MapperPerformanceBenchmark.class.getSimpleName())
                .resultFormat(ResultFormatType.JSON)
                .result("benchmark-results.json")
                .build();

        new Runner(opt).run();
    }
}
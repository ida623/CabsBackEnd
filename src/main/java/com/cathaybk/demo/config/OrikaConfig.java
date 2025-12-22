//package com.cathaybk.demo.config;
//
//import com.cathaybk.demo.dto.CIFQ001TranrsData;
//import com.cathaybk.demo.entity.CustomerInfoEntity;
//import ma.glasnost.orika.MapperFacade;
//import ma.glasnost.orika.MapperFactory;
//import ma.glasnost.orika.impl.DefaultMapperFactory;
//import ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class OrikaConfig {
//
//    @Bean
//    public MapperFacade orikaMapper() {
//        // 使用 Eclipse JDT 編譯策略，避免 Javassist 反射問題
//        MapperFactory factory = new DefaultMapperFactory.Builder()
//                .compilerStrategy(new EclipseJdtCompilerStrategy())
//                .useBuiltinConverters(false) // 避免載入 CloneableConverter
//                .build();
//
//        // 註冊映射關係
//        factory.classMap(CustomerInfoEntity.class, CIFQ001TranrsData.class)
//                .byDefault()
//                .register();
//
//        return factory.getMapperFacade();
//    }
//}
//
//在 Spring Boot 專案中使用 Orika 進行 DTO 與 Entity 的映射時，通常會透過 DefaultMapperFactory 建立 MapperFacade。
// 最初的設定如下：
//MapperFactory factory = new DefaultMapperFactory.Builder().build();
//這樣的寫法在 JDK 8 ~ JDK 11 環境下通常沒有問題，因為 Orika 預設使用 Javassist 作為動態編譯策略。
// 但在 JDK 16 之後，Java 模組系統收緊了反射存取，導致 Orika 嘗試透過 Javassist 反射 ClassLoader.defineClass 或 Object.clone() 時，會拋出 InaccessibleObjectException。
// 在啟動 Spring Boot 時遇到的 ApplicationContext 啟動失敗的根本原因。
//
//問題的根源：Javassist 與 JDK 模組限制
//Javassist 是一個動態生成 Java 類別的工具，Orika 依賴它來建立映射程式碼。
//在 JDK 17+，模組系統禁止未命名模組直接反射存取某些核心方法，例如：
//ClassLoader.defineClass
//Object.clone()
//
//因此，Orika 在初始化時會因為反射被阻擋而失敗，導致 Spring 容器無法建立 MapperFacade Bean。
//
//解決方案：改用 Eclipse JDT 編譯策略
//為了避免 Javassist 的反射問題，可以改用 Eclipse JDT 編譯器作為 Orika 的編譯策略。這樣 Orika 會透過 JDT 編譯器生成 Mapper 類別，而不是依賴 Javassist 的反射。

// 原本版本
//
//@Configuration
//public class OrikaConfig {
//
//    @Bean
//    public MapperFacade orikaMapper() {
//        MapperFactory factory = new DefaultMapperFactory.Builder().build();
//
//        // 註冊映射關係
//        factory.classMap(CustomerInfoEntity.class, CIFQ001TranrsData.class)
//                .byDefault()
//                .register();
//
//        return factory.getMapperFacade();
//    }
//}

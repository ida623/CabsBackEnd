# Java Object Mapping 完整技術文件

> 本文件整理自完整的技術諮詢對話，涵蓋 6 種常用 Java 物件映射工具的使用指南

**目錄**
- [1. ObjectMapper (Jackson)](#1-objectmapper-jackson)
- [2. Apache BeanUtils](#2-apache-beanutils)
- [3. Spring BeanUtils](#3-spring-beanutils)
- [4. MapStruct](#4-mapstruct)
- [5. ModelMapper](#5-modelmapper)
- [6. Orika](#6-orika)
- [7. Null 值處理完整指南](#7-null-值處理完整指南)
- [8. 異常處理策略](#8-異常處理策略)
- [9. 檢核清單](#9-檢核清單)
- [10. 工具比較與選擇建議](#10-工具比較與選擇建議)

---

# 1. ObjectMapper (Jackson)

## 簡介
ObjectMapper 是 Jackson 函式庫的核心類別，主要用於 JSON 序列化/反序列化，也可用於物件轉換。

## 一、添加依賴

### Maven (`pom.xml`)
```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

**說明**：Spring Boot 專案通常已內建此依賴，無需額外添加。

### Gradle (`build.gradle`)
```gradle
implementation 'com.fasterxml.jackson.core:jackson-databind'
```

## 二、ObjectMapper 的注入方式

### 建構子注入 (推薦)
```java
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CIFT001SvcImpl implements CIFT001Svc {

    private final ObjectMapper objectMapper;  // 宣告為 final
    
    // Lombok 會自動生成包含 objectMapper 的建構子
}
```

**說明**：
- `@RequiredArgsConstructor`：Lombok 註解，自動為所有 `final` 欄位生成建構子
- Spring 會自動注入 `ObjectMapper` Bean
- 這是最推薦的注入方式，支援不可變性且便於測試

## 三、ObjectMapper 核心方法

### 1. `convertValue()` - 物件轉換

**語法**：
```java
TargetClass target = objectMapper.convertValue(source, TargetClass.class);
```

**範例**：
```java
// DTO 轉 Entity
CIFT001TranrqData data = tranrq.getData();
CustomerInfoEntity entity = objectMapper.convertValue(data, CustomerInfoEntity.class);
```

**注意事項**：
- 欄位名稱需一致 (或使用 `@JsonProperty` 對應)
- 會自動處理型別轉換 (如 String → Integer)
- 若欄位不匹配會拋出例外

### 2. 其他常用方法

#### `writeValueAsString()` - 物件轉 JSON 字串
```java
String jsonString = objectMapper.writeValueAsString(object);
```

#### `readValue()` - JSON 字串轉物件
```java
MyClass obj = objectMapper.readValue(jsonString, MyClass.class);
```

## 四、關鍵註解說明

### `@Service`
- **作用**：標記此類為 Service 層元件
- **效果**：Spring 會自動掃描並註冊為 Bean

### `@RequiredArgsConstructor`
- **來源**：Lombok
- **作用**：自動生成包含所有 `final` 欄位的建構子

### `@JsonProperty`
- **作用**：指定 JSON 欄位名稱與 Java 屬性的對應關係
```java
@JsonProperty("idNum")
private String identityNumber;  // JSON 中是 idNum，Java 中是 identityNumber
```

## 五、Null 值處理

### 使用 `readerForUpdating()` 更新物件

```java
@Service
@RequiredArgsConstructor
public class CIFT002SvcImpl {

    private final ObjectMapper objectMapper;
    
    public void updateEntity(CIFT002TranrqData data, CustomerInfoEntity entity) {
        try {
            // readerForUpdating() 會將資料更新到現有物件，自動忽略 null 值
            entity = objectMapper.readerForUpdating(entity)
                    .readValue(objectMapper.writeValueAsString(data));
        } catch (Exception e) {
            throw new RuntimeException("Mapping 失敗", e);
        }
    }
}
```

**工作原理**：
1. `readerForUpdating(entity)` 建立一個更新模式的 Reader
2. 將 `data` 轉為 JSON 字串
3. 再將 JSON 反序列化到現有的 `entity` 物件
4. **只更新 JSON 中存在的欄位，null 值不會覆蓋原有資料**

## 六、完整範例

```java
package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.dto.CIFT001TranrqData;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CIFT001SvcImpl {

    private final ObjectMapper objectMapper;

    public CustomerInfoEntity createCustomer(CIFT001TranrqData data) {
        // DTO 轉 Entity
        CustomerInfoEntity entity = objectMapper.convertValue(data, CustomerInfoEntity.class);
        return entity;
    }
    
    public void updateCustomer(CIFT001TranrqData data, CustomerInfoEntity entity) {
        try {
            // 更新現有物件，忽略 null 值
            entity = objectMapper.readerForUpdating(entity)
                    .readValue(objectMapper.writeValueAsString(data));
        } catch (Exception e) {
            throw new RuntimeException("更新失敗", e);
        }
    }
}
```

## 七、常見問題 FAQ

### Q1: ObjectMapper 從哪裡來？
**A**: Spring Boot 會自動配置並提供 `ObjectMapper` Bean，無需手動建立。

### Q2: 如果欄位名稱不一致怎麼辦？
**A**: 使用 `@JsonProperty` 註解對應：
```java
@JsonProperty("idNum")
private String idNumber;  // JSON 中是 idNum，Java 中是 idNumber
```

### Q3: 轉換失敗會發生什麼？
**A**: 會拋出 `IllegalArgumentException`，建議用 try-catch 處理。

### Q4: 必須使用 try-catch 嗎？
**A**: 
- 使用 `convertValue()` 時：不需要 (但建議在關鍵業務中使用)
- 使用 `readerForUpdating()` 時：**必須** (checked exception)

---

# 2. Apache BeanUtils

## 簡介
Apache Commons BeanUtils 提供物件屬性複製功能，支援型別轉換。

## 一、添加依賴

### Maven (`pom.xml`)
```xml
<dependency>
    <groupId>commons-beanutils</groupId>
    <artifactId>commons-beanutils</artifactId>
    <version>1.9.4</version>
</dependency>
```

### Gradle (`build.gradle`)
```gradle
implementation 'commons-beanutils:commons-beanutils:1.9.4'
```

**注意**：
- 此依賴**不是** Spring Boot 內建的，必須手動添加
- 建議使用最新穩定版本

## 二、BeanUtils 的使用方式

### 特點：無需注入，直接使用靜態方法

```java
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class CIFT001SvcImplApacheBeanUtils {

    public void exampleMethod() {
        // 直接使用 BeanUtils.copyProperties()
        // 無需注入或宣告
    }
}
```

## 三、BeanUtils 核心方法

### `copyProperties()` - 屬性複製

**語法**：
```java
BeanUtils.copyProperties(target, source);
```

**⚠️ 重要**：參數順序是 **(目標, 來源)**，與 Spring BeanUtils 相反！

**範例**：
```java
// DTO → Entity
CIFT001TranrqData data = tranrq.getData();
CustomerInfoEntity entity = new CustomerInfoEntity();

try {
    BeanUtils.copyProperties(entity, data);  // 目標在前，來源在後
} catch (Exception e) {
    throw new RuntimeException("Mapping 失敗", e);
}
```

### 方法特性

| 特性 | 說明 |
|------|------|
| **參數順序** | `copyProperties(目標, 來源)` |
| **例外處理** | 會拋出 `IllegalAccessException`, `InvocationTargetException` |
| **型別轉換** | 支援基本型別的自動轉換 (String ↔ Integer 等) |
| **null 處理** | null 值會被複製 (會覆蓋目標物件的值) |
| **效能** | 使用反射，效能較慢 |

## 四、Null 值處理

### 使用 `populate()` + `describe()`

```java
@Service
@RequiredArgsConstructor
public class CIFT002SvcImplApacheBeanUtils {

    public void updateEntity(CIFT002TranrqData data, CustomerInfoEntity entity) {
        try {
            // describe() 將物件轉為 Map，populate() 更新到目標物件
            // Apache BeanUtils 的 populate 會自動跳過 null 值
            BeanUtils.populate(entity, BeanUtils.describe(data));
        } catch (Exception e) {
            throw new RuntimeException("Mapping 失敗", e);
        }
    }
}
```

**工作原理**：
1. `BeanUtils.describe(data)` 將來源物件轉為 `Map<String, String>`
2. `BeanUtils.populate(entity, map)` 從 Map 更新到目標物件
3. **`populate()` 方法預設會跳過 null 值**

## 五、完整範例

```java
package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.dto.CIFT001TranrqData;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class CIFT001SvcImplApacheBeanUtils {

    public CustomerInfoEntity createCustomer(CIFT001TranrqData data) {
        CustomerInfoEntity entity = new CustomerInfoEntity();
        
        try {
            BeanUtils.copyProperties(entity, data);
        } catch (Exception e) {
            throw new RuntimeException("屬性複製失敗", e);
        }
        
        return entity;
    }
}
```

## 六、常見錯誤與解決方案

### ❌ 錯誤 1: 參數順序錯誤
```java
// 錯誤寫法
BeanUtils.copyProperties(data, entity);  // 來源覆蓋目標 ❌
```
```java
// 正確寫法
BeanUtils.copyProperties(entity, data);  // 目標在前，來源在後 ✅
```

### ❌ 錯誤 2: 未處理例外
```java
// 錯誤寫法
CustomerInfoEntity entity = new CustomerInfoEntity();
BeanUtils.copyProperties(entity, data);  // 編譯錯誤：未處理例外 ❌
```
```java
// 正確寫法
try {
    BeanUtils.copyProperties(entity, data);  ✅
} catch (IllegalAccessException | InvocationTargetException e) {
    throw new RuntimeException("Mapping 失敗", e);
}
```

## 七、常見問題 FAQ

### Q1: Apache BeanUtils 需要注入嗎？
**A**: **不需要**。`BeanUtils.copyProperties()` 是靜態方法，直接 import 後即可使用。

### Q2: 為什麼要用 try-catch？
**A**: `BeanUtils.copyProperties()` 會拋出檢查例外，必須處理。

### Q3: 參數順序為什麼是 (目標, 來源)？
**A**: 這是 Apache BeanUtils 的設計。注意 Spring BeanUtils 的順序相反。

### Q4: Apache BeanUtils vs Spring BeanUtils 該選哪個？
**A**: 
- **Apache BeanUtils**：需要型別轉換功能時使用
- **Spring BeanUtils**：追求效能且不需要複雜轉換時使用

---

# 3. Spring BeanUtils

## 簡介
Spring BeanUtils 是 Spring Framework 核心模組的一部分，提供簡單高效的物件屬性複製功能。

## 一、添加依賴

### ✅ **無需添加額外依賴**

```xml
<!-- Spring BeanUtils 已包含在 Spring Core 中 -->
<!-- 如果專案已使用 Spring Boot，則無需額外添加任何依賴 -->
```

**說明**：
- `Spring BeanUtils` 是 Spring Framework 核心模組的一部分
- 只要專案中有 `spring-boot-starter`，就已經包含此工具
- **不需要**額外添加任何依賴

## 二、BeanUtils 的使用方式

### 特點：無需注入，直接使用靜態方法

```java
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class CIFT001SvcImplSpringBeanUtils {

    public void exampleMethod() {
        // 直接使用 BeanUtils.copyProperties()
        // 無需注入或宣告
    }
}
```

## 三、BeanUtils 核心方法

### `copyProperties()` - 屬性複製

**語法**：
```java
BeanUtils.copyProperties(source, target);
```

**⚠️ 重要**：參數順序是 **(來源, 目標)**，與 Apache BeanUtils 相反！

**範例**：
```java
// DTO → Entity
CIFT001TranrqData data = tranrq.getData();
CustomerInfoEntity entity = new CustomerInfoEntity();

BeanUtils.copyProperties(data, entity);  // 來源在前，目標在後
```

### 方法特性

| 特性 | 說明 |
|------|------|
| **參數順序** | `copyProperties(來源, 目標)` |
| **例外處理** | ❌ 不拋出檢查例外 (內部會忽略錯誤) |
| **型別轉換** | ⚠️ 僅支援相同型別複製，不支援自動轉換 |
| **null 處理** | ⚠️ null 值會被複製 (會覆蓋目標物件的值) |
| **效能** | ⚡ 使用反射但經過優化，效能較佳 |

### 進階方法：忽略特定屬性

```java
// 忽略某些屬性不複製
BeanUtils.copyProperties(source, target, "ignoreField1", "ignoreField2");
```

**範例**：
```java
// 忽略 id 和 createTime 欄位
BeanUtils.copyProperties(data, entity, "id", "createTime");
```

## 四、Null 值處理

### 取得物件中值為 null 的屬性名稱

```java
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public static String[] getNullPropertyNames(Object source) {
    final BeanWrapper src = new BeanWrapperImpl(source);
    PropertyDescriptor[] pds = src.getPropertyDescriptors();
    
    Set<String> emptyNames = new HashSet<>();
    for (PropertyDescriptor pd : pds) {
        Object srcValue = src.getPropertyValue(pd.getName());
        if (srcValue == null) {
            emptyNames.add(pd.getName());
        }
    }
    
    return emptyNames.toArray(new String[0]);
}

// 使用方式：只複製非 null 的屬性
BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
```

## 五、完整範例

### 基本使用
```java
package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.dto.CIFT001TranrqData;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class CIFT001SvcImplSpringBeanUtils {

    public CustomerInfoEntity createCustomer(CIFT001TranrqData data) {
        CustomerInfoEntity entity = new CustomerInfoEntity();
        BeanUtils.copyProperties(data, entity);
        return entity;
    }
}
```

### 進階使用：忽略特定欄位
```java
public CustomerInfoEntity updateCustomer(CIFT001TranrqData data, CustomerInfoEntity existingEntity) {
    // 複製屬性，但保留原本的 id 和建立時間
    BeanUtils.copyProperties(data, existingEntity, "id", "createTime");
    return existingEntity;
}
```

## 六、常見錯誤與解決方案

### ❌ 錯誤 1: 參數順序錯誤
```java
// 錯誤寫法 (與 Apache BeanUtils 混淆)
BeanUtils.copyProperties(entity, data);  // 目標在前 ❌
```
```java
// 正確寫法
BeanUtils.copyProperties(data, entity);  // 來源在前，目標在後 ✅
```

### ❌ 錯誤 2: Import 錯誤的類別
```java
// 錯誤：導入 Apache BeanUtils ❌
import org.apache.commons.beanutils.BeanUtils;
```
```java
// 正確：導入 Spring BeanUtils ✅
import org.springframework.beans.BeanUtils;
```

### ❌ 錯誤 3: 期待自動型別轉換
```java
// 來源物件
class DTO {
    private String age;  // String 型別
}

// 目標物件
class Entity {
    private Integer age;  // Integer 型別
}

// ⚠️ Spring BeanUtils 不會自動轉換，age 會保持為 null
BeanUtils.copyProperties(dto, entity);
```

**解決方案**：
- 確保來源和目標的屬性型別一致
- 或使用 ObjectMapper / Apache BeanUtils 進行型別轉換

## 七、常見問題 FAQ

### Q1: Spring BeanUtils 需要添加依賴嗎？
**A**: **不需要**。它是 Spring Core 的一部分，只要有 Spring Boot 就已包含。

### Q2: Spring BeanUtils 需要注入嗎？
**A**: **不需要**。`BeanUtils.copyProperties()` 是靜態方法，直接 import 後即可使用。

### Q3: 參數順序為什麼是 (來源, 目標)？
**A**: 這是 Spring BeanUtils 的設計，符合直覺的「從來源複製到目標」順序。注意 Apache BeanUtils 順序相反。

### Q4: 為什麼不需要 try-catch？
**A**: Spring BeanUtils 內部會捕獲並忽略錯誤，不會拋出檢查例外。但這也意味著複製失敗時不會有明顯提示。

### Q5: 支援型別轉換嗎？
**A**: **不支援**。只能複製相同型別的屬性。如需型別轉換，請使用 ObjectMapper 或 Apache BeanUtils。

---

# 4. MapStruct

## 簡介
MapStruct 是一個程式碼生成器，在編譯時期生成型別安全的 Bean 映射程式碼，提供接近手寫程式碼的效能。

## 一、添加依賴

MapStruct 需要添加**兩個**依賴：核心庫和註解處理器。

### Maven (`pom.xml`)

```xml
<properties>
    <!-- 定義 MapStruct 版本 -->
    <mapstruct.version>1.5.5.Final</mapstruct.version>
    <lombok-mapstruct-binding.version>0.2.0</lombok-mapstruct-binding.version>
</properties>

<dependencies>
    <!-- MapStruct 核心庫 -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${mapstruct.version}</version>
    </dependency>
    
    <!-- MapStruct 註解處理器 (編譯時生成程式碼) -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>${mapstruct.version}</version>
        <scope>provided</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>17</source>
                <target>17</target>
                <annotationProcessorPaths>
                    <!-- Lombok 註解處理器 -->
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>${lombok.version}</version>
                    </path>
                    <!-- MapStruct 註解處理器 -->
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>${mapstruct.version}</version>
                    </path>
                    <!-- Lombok 與 MapStruct 綁定 -->
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok-mapstruct-binding</artifactId>
                        <version>${lombok-mapstruct-binding.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Gradle (`build.gradle`)

```gradle
dependencies {
    // MapStruct 核心庫
    implementation 'org.mapstruct:mapstruct:1.5.5.Final'
    
    // MapStruct 註解處理器
    annotationProcessor 'org.mapstruct:mapstruct-processor:1.5.5.Final'
    
    // Lombok (如果有使用)
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    
    // Lombok 與 MapStruct 綁定
    annotationProcessor 'org.projectlombok:lombok-mapstruct-binding:0.2.0'
}
```

## 二、建立 Mapper 介面

### 步驟 1: 建立 Mapper 介面

```java
package com.cathaybk.demo.mapper;

import com.cathaybk.demo.dto.CIFT001TranrqData;
import com.cathaybk.demo.dto.CIFT002TranrqData;
import com.cathaybk.demo.dto.CIFQ001TranrsData;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CustomerInfoMapper {

    // Entity → DTO
    CIFQ001TranrsData toTranrsData(CustomerInfoEntity entity);

    // DTO → Entity
    CustomerInfoEntity toEntity(CIFT001TranrqData data);

    // 更新方法：自動忽略 null 值
    void updateEntityFromData(CIFT002TranrqData data, @MappingTarget CustomerInfoEntity entity);
}
```

### 步驟 2: 編譯專案生成實作類別

執行編譯指令：

```bash
# Maven
mvn clean compile

# Gradle
./gradlew clean build
```

編譯後，MapStruct 會自動生成實作類別 `CustomerInfoMapperImpl.java`。

## 三、在 Service 中使用 Mapper

```java
package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.mapper.CustomerInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CIFT001SvcImplMapStruct {

    private final CustomerInfoMapper customerInfoMapper;  // 注入 Mapper
    
    public CustomerInfoEntity createCustomer(CIFT001TranrqData data) {
        // 使用 Mapper 轉換
        CustomerInfoEntity entity = customerInfoMapper.toEntity(data);
        return entity;
    }
}
```

## 四、MapStruct 核心註解

### `@Mapper` - 標記 Mapper 介面

```java
@Mapper(componentModel = "spring")
public interface CustomerInfoMapper {
    // ...
}
```

**重要參數**：
- **`componentModel = "spring"`**：生成的實作類別會加上 `@Component` 註解
- **必須設定**，否則無法注入使用

### `@Mapping` - 自訂欄位映射

```java
@Mapper(componentModel = "spring")
public interface CustomerInfoMapper {
    
    @Mapping(source = "idNum", target = "identityNumber")
    @Mapping(source = "chineseName", target = "name")
    CustomerInfoEntity toEntity(CIFT001TranrqData data);
}
```

### `@MappingTarget` - 更新現有物件

```java
@Mapper(componentModel = "spring")
public interface CustomerInfoMapper {
    
    // 更新現有 Entity
    void updateEntityFromData(CIFT002TranrqData data, @MappingTarget CustomerInfoEntity entity);
}
```

### `nullValuePropertyMappingStrategy` - null 值處理策略

```java
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CustomerInfoMapper {
    
    // 忽略來源物件中的 null 值
    void updateEntityFromData(CIFT002TranrqData data, @MappingTarget CustomerInfoEntity entity);
}
```

**策略選項**：
- **`IGNORE`**：忽略 null 值，不覆蓋目標物件
- **`SET_TO_NULL`**(預設)：null 值會覆蓋目標物件
- **`SET_TO_DEFAULT`**：null 值會設為預設值

## 五、完整範例

```java
// Mapper 介面
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CustomerInfoMapper {
    
    // 新增：建立新物件
    CustomerInfoEntity toEntity(CIFT001TranrqData data);
    
    // 更新：更新現有物件 (忽略 null)
    void updateEntityFromData(CIFT002TranrqData data, @MappingTarget CustomerInfoEntity entity);
    
    // List 轉換
    List<CustomerInfoEntity> toEntityList(List<CIFT001TranrqData> dataList);
}

// Service
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerInfoMapper mapper;
    
    @Transactional
    public CustomerInfoEntity update(Long id, CIFT002TranrqData data) {
        CustomerInfoEntity entity = repository.findById(id).orElseThrow();
        mapper.updateEntityFromData(data, entity);  // 自動忽略 null
        return repository.save(entity);
    }
}
```

## 六、常見錯誤與解決方案

### ❌ 錯誤 1: 找不到 Mapper 實作類別

**錯誤訊息**：
```
Field customerInfoMapper required a bean of type '...' that could not be found.
```

**解決方案**：

1. 確認已添加 `mapstruct-processor` 依賴
2. 確認設定 `componentModel = "spring"`
3. 執行 `mvn clean compile` 重新編譯

### ❌ 錯誤 2: Lombok 與 MapStruct 衝突

**解決方案**：添加 `lombok-mapstruct-binding` 依賴

## 七、常見問題 FAQ

### Q1: MapStruct 需要哪些依賴？
**A**: 兩個必要依賴：
1. `mapstruct` - 核心庫
2. `mapstruct-processor` - 註解處理器

### Q2: 為什麼需要重新編譯？
**A**: MapStruct 在**編譯時**生成實作類別，修改 Mapper 介面後必須重新編譯。

### Q3: MapStruct 如何注入使用？
**A**: 
```java
@Service
@RequiredArgsConstructor
public class MyService {
    private final CustomerInfoMapper mapper;  // 透過建構子注入
}
```

### Q4: 支援 List 轉換嗎？
**A**: **支援**。定義單一物件轉換方法後，MapStruct 會自動支援 List。

---

# 5. ModelMapper

## 簡介
ModelMapper 是一個智慧型物件映射函式庫，能自動匹配相似名稱的屬性，適合快速開發。

## 一、添加依賴

### Maven (`pom.xml`)

```xml
<dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>3.2.0</version>
</dependency>
```

### Gradle (`build.gradle`)

```gradle
implementation 'org.modelmapper:modelmapper:3.2.0'
```

## 二、ModelMapper 的使用方式

### 方式：透過 Spring Bean 管理 (推薦)

#### 步驟 1: 建立配置類別

```java
package com.cathaybk.demo.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        // 設定匹配策略
        modelMapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT)  // 嚴格匹配
            .setSkipNullEnabled(true)  // 跳過 null 值
            .setAmbiguityIgnored(true);  // 忽略模糊匹配
        
        return modelMapper;
    }
}
```

#### 步驟 2: 在 Service 中注入使用

```java
package com.cathaybk.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CIFT001SvcImplModelMapper {

    private final ModelMapper modelMapper;  // 透過建構子注入
    
    public CustomerInfoEntity convert(CIFT001TranrqData data) {
        return modelMapper.map(data, CustomerInfoEntity.class);
    }
}
```

## 三、ModelMapper 核心方法

### `map()` - 物件映射

**語法**：
```java
TargetClass target = modelMapper.map(source, TargetClass.class);
```

**範例**：
```java
// DTO → Entity
CIFT001TranrqData data = tranrq.getData();
CustomerInfoEntity entity = modelMapper.map(data, CustomerInfoEntity.class);

// 更新現有物件
modelMapper.map(updateData, entity);
```

## 四、ModelMapper 配置選項

### 匹配策略

```java
modelMapper.getConfiguration()
    .setMatchingStrategy(MatchingStrategies.STANDARD);
```

| 策略 | 說明 | 使用時機 |
|------|------|----------|
| **STANDARD** (預設) | 智慧匹配，允許部分名稱匹配 | 一般場景 |
| **STRICT** | 嚴格匹配，屬性名稱必須完全對應 | 避免錯誤映射 |
| **LOOSE** | 寬鬆匹配，最大程度嘗試匹配 | 屬性名稱差異大時 |

### Null 值處理

```java
@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
            .setSkipNullEnabled(true);  // 忽略 null 值 ✅
        return modelMapper;
    }
}
```

## 五、完整範例

```java
// 配置類別
@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
            .setSkipNullEnabled(true)
            .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }
}

// Service
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final ModelMapper modelMapper;
    
    @Transactional
    public CustomerInfoEntity update(Long id, CIFT002TranrqData data) {
        CustomerInfoEntity entity = repository.findById(id).orElseThrow();
        modelMapper.map(data, entity);  // 自動忽略 null
        return repository.save(entity);
    }
}
```

## 六、常見問題 FAQ

### Q1: ModelMapper 需要添加依賴嗎？
**A**: **需要**。必須手動添加 `org.modelmapper:modelmapper` 依賴。

### Q2: ModelMapper 需要建立配置類別嗎？
**A**: **強烈建議**。透過配置類別管理有以下優點：
- 單例模式，節省記憶體
- 集中管理配置
- 符合 Spring 依賴注入原則

### Q3: 如何避免 null 值覆蓋？
**A**: 
```java
modelMapper.getConfiguration().setSkipNullEnabled(true);
```

### Q4: ModelMapper vs MapStruct 該選哪個？
**A**: 
- **ModelMapper**：智慧匹配、屬性名稱差異大、快速開發
- **MapStruct**：追求極致效能、大型專案、需要編譯時檢查

---

# 6. Orika

## 簡介
Orika 是一個高效能的 Java Bean 映射框架，使用位元組碼生成技術實現接近手寫程式碼的效能。

## 一、添加依賴

Orika 需要添加**兩個**依賴：核心庫和編譯器支援。

### Maven (`pom.xml`)

```xml
<dependencies>
    <!-- Orika 核心庫 -->
    <dependency>
        <groupId>ma.glasnost.orika</groupId>
        <artifactId>orika-core</artifactId>
        <version>1.5.4</version>
    </dependency>
    
    <!-- Eclipse JDT 編譯器 (必須) -->
    <dependency>
        <groupId>org.eclipse.jdt</groupId>
        <artifactId>ecj</artifactId>
        <version>3.26.0</version>
    </dependency>
</dependencies>
```

### Gradle (`build.gradle`)

```gradle
dependencies {
    // Orika 核心庫
    implementation 'ma.glasnost.orika:orika-core:1.5.4'
    
    // Eclipse JDT 編譯器
    implementation 'org.eclipse.jdt:ecj:3.26.0'
}
```

## 二、Orika 的使用方式

### 方式：透過 Spring Bean 管理 (推薦)

#### 步驟 1: 建立配置類別

```java
package com.cathaybk.demo.config;

import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrikaConfig {

    @Bean
    public MapperFactory orikaMapperFactory() {
        MapperFactory factory = new DefaultMapperFactory.Builder()
                .compilerStrategy(new EclipseJdtCompilerStrategy())
                .useBuiltinConverters(true)
                .mapNulls(false)  // 不映射 null 值
                .build();
        
        // 註冊所有映射關係
        registerMappings(factory);
        
        return factory;
    }
    
    @Bean
    public MapperFacade orikaMapperFacade(MapperFactory mapperFactory) {
        return mapperFactory.getMapperFacade();
    }
    
    private void registerMappings(MapperFactory factory) {
        // 註冊 DTO → Entity 映射
        factory.classMap(CIFT001TranrqData.class, CustomerInfoEntity.class)
                .byDefault()
                .register();
        
        // 註冊 Entity → DTO 映射
        factory.classMap(CustomerInfoEntity.class, CIFQ001TranrsData.class)
                .byDefault()
                .register();
    }
}
```

#### 步驟 2: 在 Service 中注入使用

```java
package com.cathaybk.demo.service.impl;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CIFT001SvcImplOrika {

    private final MapperFacade orikaMapper;  // 透過建構子注入
    
    public CustomerInfoEntity convert(CIFT001TranrqData data) {
        return orikaMapper.map(data, CustomerInfoEntity.class);
    }
}
```

## 三、Orika 核心元件

### 1. `MapperFactory` - 映射工廠

```java
MapperFactory factory = new DefaultMapperFactory.Builder()
    .compilerStrategy(new EclipseJdtCompilerStrategy())  // 編譯策略（必須）
    .useBuiltinConverters(true)   // 使用內建轉換器
    .mapNulls(false)              // 不映射 null 值
    .build();
```

### 2. `MapperFacade` - 映射門面

```java
MapperFacade facade = factory.getMapperFacade();
```

### 3. 註冊映射關係

```java
// 基本映射：自動映射同名屬性
factory.classMap(SourceDto.class, TargetEntity.class)
    .byDefault()
    .register();

// 自訂欄位映射
factory.classMap(SourceDto.class, TargetEntity.class)
    .field("idNum", "identityNumber")
    .byDefault()
    .register();
```

## 四、Orika 核心方法

### `map()` - 物件映射

```java
// 建立新物件
CustomerInfoEntity entity = orikaMapper.map(data, CustomerInfoEntity.class);

// 更新現有物件
orikaMapper.map(updateData, entity);

// 批量映射 List
List<CustomerInfoEntity> entityList = 
    orikaMapper.mapAsList(dtoList, CustomerInfoEntity.class);
```

## 五、Null 值處理

```java
MapperFactory factory = new DefaultMapperFactory.Builder()
    .mapNulls(false)  // 不映射 null 值 ✅
    .build();
```

## 六、完整範例

```java
// 配置類別
@Configuration
public class OrikaConfig {
    @Bean
    public MapperFactory orikaMapperFactory() {
        MapperFactory factory = new DefaultMapperFactory.Builder()
                .compilerStrategy(new EclipseJdtCompilerStrategy())
                .mapNulls(false)
                .build();
        
        factory.classMap(CIFT002TranrqData.class, CustomerInfoEntity.class)
                .byDefault()
                .register();
        
        return factory;
    }
    
    @Bean
    public MapperFacade orikaMapper(MapperFactory factory) {
        return factory.getMapperFacade();
    }
}

// Service
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final MapperFacade orikaMapper;
    
    @Transactional
    public CustomerInfoEntity update(Long id, CIFT002TranrqData data) {
        CustomerInfoEntity entity = repository.findById(id).orElseThrow();
        orikaMapper.map(data, entity);  // 自動忽略 null
        return repository.save(entity);
    }
}
```

## 七、常見錯誤與解決方案

### ❌ 錯誤 1: ClassNotFoundException - INameEnvironment

**錯誤訊息**：
```
java.lang.NoClassDefFoundError: org/eclipse/jdt/internal/compiler/env/INameEnvironment
```

**解決方案**：添加 `ecj` 依賴

### ❌ 錯誤 2: 忘記設定 CompilerStrategy

**解決方案**：
```java
MapperFactory factory = new DefaultMapperFactory.Builder()
    .compilerStrategy(new EclipseJdtCompilerStrategy())  // 必須加這行 ✅
    .build();
```

### ❌ 錯誤 3: 映射失敗 - 未註冊映射關係

**解決方案**：確認已註冊映射關係
```java
factory.classMap(SourceDto.class, TargetEntity.class)
    .byDefault()
    .register();  // 必須呼叫 register() ✅
```

## 八、常見問題 FAQ

### Q1: Orika 需要哪些依賴？
**A**: **兩個**必要依賴：
1. `orika-core` - 核心庫
2. `ecj` (Eclipse JDT Compiler) - 位元組碼編譯器

### Q2: 為什麼需要 Eclipse JDT 編譯器？
**A**: Orika 使用位元組碼生成技術來實現高效能映射，需要編譯器在執行時期動態生成 Mapper 類別。

### Q3: Orika vs MapStruct 該選哪個？
**A**: 
- **Orika**：執行時期位元組碼生成、靈活配置、成熟穩定
- **MapStruct**：編譯時期程式碼生成、型別安全、更活躍的社群

---

# 7. Null 值處理完整指南

## 情境說明

在更新物件時，我們通常希望**只更新有值的欄位**，而保留原本的資料。例如：

原有資料：
```java
CustomerInfoEntity entity = new CustomerInfoEntity();
entity.setEmail("old@example.com");
entity.setMobile("0912345678");
```

更新資料 (email 為 null，希望保留原本的 email)：
```java
UpdateDto data = new UpdateDto();
data.setEmail(null);
data.setMobile("0987654321");
```

期望結果：
- email 保留 "old@example.com" (不被 null 覆蓋)
- mobile 更新為 "0987654321"

## 各工具的 Null 值處理方法

### 1. ObjectMapper

**方法：使用 `readerForUpdating()`**

```java
try {
    entity = objectMapper.readerForUpdating(entity)
            .readValue(objectMapper.writeValueAsString(data));
} catch (Exception e) {
    throw new RuntimeException("Mapping 失敗", e);
}
```

**優缺點**：
- ✅ 自動忽略 null 值
- ⚠️ 效能較差 (經過序列化/反序列化)
- ⚠️ 必須使用 try-catch

### 2. Apache BeanUtils

**方法：使用 `populate()` + `describe()`**

```java
try {
    BeanUtils.populate(entity, BeanUtils.describe(data));
} catch (Exception e) {
    throw new RuntimeException("Mapping 失敗", e);
}
```

**優缺點**：
- ✅ 預設跳過 null 值
- ⚠️ 效能較差
- ⚠️ 必須使用 try-catch

### 3. Spring BeanUtils

**方法：自訂工具方法**

```java
// 建立工具類別
public static String[] getNullPropertyNames(Object source) {
    final BeanWrapper src = new BeanWrapperImpl(source);
    PropertyDescriptor[] pds = src.getPropertyDescriptors();
    Set<String> emptyNames = new HashSet<>();
    for (PropertyDescriptor pd : pds) {
        if (src.getPropertyValue(pd.getName()) == null) {
            emptyNames.add(pd.getName());
        }
    }
    return emptyNames.toArray(new String[0]);
}

// 使用方式
BeanUtils.copyProperties(data, entity, getNullPropertyNames(data));
```

**優缺點**：
- ✅ 效能較好
- ❌ 需要自己寫工具方法

### 4. MapStruct

**方法：使用 `nullValuePropertyMappingStrategy`**

```java
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CustomerInfoMapper {
    void updateEntity(UpdateDto dto, @MappingTarget Entity entity);
}
```

**優缺點**：
- ✅ 效能極佳
- ✅ 設定簡單清晰
- ✅ 型別安全

### 5. ModelMapper

**方法：設定 `setSkipNullEnabled(true)`**

```java
// 在配置類別中設定（推薦）
@Bean
public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setSkipNullEnabled(true);
    return modelMapper;
}
```

**優缺點**：
- ✅ 設定簡單
- ⚠️ 效能中等

### 6. Orika

**方法：設定 `mapNulls(false)`**

```java
MapperFactory factory = new DefaultMapperFactory.Builder()
        .mapNulls(false)  // 不映射 null 值
        .build();
```

**優缺點**：
- ✅ 效能極佳
- ✅ 設定簡單清晰

## Null 值處理比較總表

| Mapper 工具 | Null 值處理方法 | 設定難度 | 效能 | 推薦度 |
|-------------|----------------|----------|------|--------|
| **ObjectMapper** | `readerForUpdating()` | ⚠️ 中等 | 🐢 慢 | ⭐⭐ |
| **Apache BeanUtils** | `populate()` 自動跳過 | ✅ 簡單 | 🐢 慢 | ⭐⭐ |
| **Spring BeanUtils** | 自訂工具方法 | ❌ 複雜 | 🐇 快 | ⭐⭐ |
| **MapStruct** | `NullValuePropertyMappingStrategy.IGNORE` | ✅ 簡單 | 🚀 極快 | ⭐⭐⭐⭐⭐ |
| **ModelMapper** | `setSkipNullEnabled(true)` | ✅ 簡單 | 🐇 中等 | ⭐⭐⭐⭐ |
| **Orika** | `mapNulls(false)` | ✅ 簡單 | 🚀 極快 | ⭐⭐⭐⭐ |

---

# 8. 異常處理策略

## 各工具的異常處理需求

### 必須使用 try-catch 的工具

#### ObjectMapper
```java
// ✅ 必須用 try-catch (使用 readerForUpdating 時)
public void updateEntity(UpdateDto dto, Entity entity) {
    try {
        entity = objectMapper.readerForUpdating(entity)
                .readValue(objectMapper.writeValueAsString(dto));
    } catch (JsonProcessingException e) {
        log.error("JSON 處理失敗", e);
        throw new MappingException("資料格式錯誤", e);
    } catch (IOException e) {
        log.error("映射失敗", e);
        throw new MappingException("資料轉換失敗", e);
    }
}
```

#### Apache BeanUtils
```java
// ✅ 必須用 try-catch
public void updateEntity(UpdateDto dto, Entity entity) {
    try {
        BeanUtils.populate(entity, BeanUtils.describe(dto));
    } catch (IllegalAccessException e) {
        log.error("無法存取屬性", e);
        throw new MappingException("屬性存取失敗", e);
    } catch (InvocationTargetException e) {
        log.error("方法呼叫失敗", e);
        throw new MappingException("資料轉換失敗", e);
    } catch (NoSuchMethodException e) {
        log.error("找不到方法", e);
        throw new MappingException("類別結構不匹配", e);
    }
}
```

### 不需要但建議使用 try-catch 的工具

#### Spring BeanUtils
```java
// ⚠️ Spring BeanUtils 會靜默失敗
public void updateEntity(UpdateDto dto, Entity entity) {
    BeanUtils.copyProperties(dto, entity);
    
    // ✅ 建議驗證關鍵欄位是否成功更新
    if (dto.getName() != null && entity.getName() == null) {
        log.warn("名字映射失敗: dto={}, entity={}", dto, entity);
        throw new MappingException("關鍵資料更新失敗");
    }
}
```

#### ModelMapper
```java
// ✅ 建議在關鍵業務邏輯中捕獲
public void updateEntity(UpdateDto dto, Entity entity) {
    try {
        modelMapper.map(dto, entity);
    } catch (Exception e) {
        log.error("ModelMapper 映射失敗: dto={}", dto, e);
        throw new MappingException("資料轉換失敗", e);
    }
}
```

#### Orika
```java
// ✅ 建議在關鍵業務邏輯中捕獲
public void updateEntity(UpdateDto dto, Entity entity) {
    try {
        orikaMapper.map(dto, entity);
    } catch (Exception e) {
        log.error("Orika 映射失敗: dto={}", dto, e);
        throw new MappingException("資料轉換失敗", e);
    }
}
```

### 完全不需要 try-catch 的工具

#### MapStruct
```java
// ✅ 不需要 try-catch，最安全
public void updateEntity(UpdateDto dto, Entity entity) {
    mapper.updateEntity(dto, entity);
    // 編譯時期已經檢查，執行時期不會有映射問題
}
```

## 異常處理風險等級

| 工具 | 不用 try-catch | 風險等級 | 說明 |
|------|----------------|----------|------|
| **ObjectMapper** | ❌ 無法編譯 | 🚫 無法執行 | 編譯器強制要求處理 |
| **Apache BeanUtils** | ❌ 無法編譯 | 🚫 無法執行 | 編譯器強制要求處理 |
| **Spring BeanUtils** | ✅ 可編譯執行 | ⚠️⚠️⚠️ 高風險 | 靜默失敗，無法得知錯誤 |
| **MapStruct** | ✅ 可編譯執行 | ✅ 低風險 | 編譯時期檢查，執行時期安全 |
| **ModelMapper** | ✅ 可編譯執行 | ⚠️⚠️ 中風險 | 可能拋出 unchecked exception |
| **Orika** | ✅ 可編譯執行 | ⚠️⚠️ 中風險 | 可能拋出 unchecked exception |

## 全域異常處理器範例

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MappingException.class)
    public ResponseEntity<ErrorResponse> handleMappingException(MappingException e) {
        log.error("映射異常", e);
        
        ErrorResponse error = new ErrorResponse(
            "MAPPING_ERROR",
            "資料轉換失敗，請檢查輸入格式"
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
```

---

# 9. 檢核清單

## ObjectMapper 檢核清單

### ✅ 基本設定
- [ ] **(1) 添加依賴** (通常 Spring Boot 已內建)
- [ ] **(2) 注入 ObjectMapper**
  ```java
  @RequiredArgsConstructor
  private final ObjectMapper objectMapper;
  ```
- [ ] **(3) 使用 `convertValue()` 進行物件轉換**
  ```java
  CustomerInfoEntity entity = objectMapper.convertValue(data, CustomerInfoEntity.class);
  ```
- [ ] **(4) 確認 DTO 和 Entity 的欄位名稱一致**
  - 或使用 `@JsonProperty` 註解對應欄位

### ✅ 進階功能
- [ ] **(5) Null 值處理**
  - 使用 `readerForUpdating()` 忽略 null 值
- [ ] **(6) 例外處理**
  - 使用 `readerForUpdating` 時必須加 try-catch

---

## Apache BeanUtils 檢核清單

### ✅ 基本設定
- [ ] **(1) 添加依賴**
  ```xml
  <dependency>
      <groupId>commons-beanutils</groupId>
      <artifactId>commons-beanutils</artifactId>
      <version>1.9.4</version>
  </dependency>
  ```
- [ ] **(2) 引入 BeanUtils**
  ```java
  import org.apache.commons.beanutils.BeanUtils;
  ```
- [ ] **(3) 使用 `copyProperties()` 進行屬性複製**
  ```java
  BeanUtils.copyProperties(entity, data);  // 目標在前，來源在後
  ```
  - **⚠️ 重要**：參數順序是 (目標, 來源)，與 Spring BeanUtils 相反！
- [ ] **(4) 使用 try-catch 處理例外**
  - `BeanUtils.copyProperties()` 會拋出檢查例外，必須處理

### ✅ 進階功能
- [ ] **(5) Null 值處理**
  - 使用 `populate()` + `describe()` 自動跳過 null 值

---

## Spring BeanUtils 檢核清單

### ✅ 基本設定
- [ ] **(1) 引入 BeanUtils** (無需添加依賴)
  ```java
  import org.springframework.beans.BeanUtils;
  ```
- [ ] **(2) 使用 `copyProperties()` 進行屬性複製**
  ```java
  BeanUtils.copyProperties(data, entity);  // 來源在前，目標在後
  ```
  - **⚠️ 重要**：參數順序是 (來源, 目標)，與 Apache BeanUtils 相反！
- [ ] **(3) 確認來源和目標物件的屬性名稱與型別一致**

### ✅ 進階功能
- [ ] **(4) Null 值處理**
  - 需自訂工具方法忽略 null
- [ ] **(5) 忽略特定欄位**
  ```java
  BeanUtils.copyProperties(data, entity, "id", "createTime");
  ```

---

## MapStruct 檢核清單

### ✅ 基本設定
- [ ] **(1) 添加依賴**
  - `mapstruct` 核心庫
  - `mapstruct-processor` 註解處理器
- [ ] **(2) 配置編譯器**
  - 設定 annotationProcessorPaths
  - 如使用 Lombok，添加 `lombok-mapstruct-binding`
- [ ] **(3) 建立 Mapper 介面**
  ```java
  @Mapper(componentModel = "spring")
  public interface CustomerInfoMapper {
      CustomerInfoEntity toEntity(CIFT001TranrqData data);
  }
  ```
- [ ] **(4) 重新編譯專案**
  ```bash
  mvn clean compile
  ```
- [ ] **(5) 確認生成實作類別**
  - 檢查 `target/generated-sources/annotations/` 目錄
- [ ] **(6) 注入 Mapper**
  ```java
  @RequiredArgsConstructor
  private final CustomerInfoMapper customerInfoMapper;
  ```
- [ ] **(7) 使用 Mapper 轉換**
  ```java
  CustomerInfoEntity entity = customerInfoMapper.toEntity(data);
  ```

### ✅ MapStruct 核心註解
- [ ] **(8) `@Mapper`** - 標記 Mapper 介面
  - `componentModel = "spring"` 必須設定
- [ ] **(9) `@Mapping`** - 自訂欄位映射
- [ ] **(10) `@MappingTarget`** - 更新現有物件

### ✅ 進階功能
- [ ] **(11) Null 值處理**
  ```java
  @Mapper(
      componentModel = "spring",
      nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
  )
  ```

---

## ModelMapper 檢核清單

### ✅ 基本設定
- [ ] **(1) 添加依賴**
  ```xml
  <dependency>
      <groupId>org.modelmapper</groupId>
      <artifactId>modelmapper</artifactId>
      <version>3.2.0</version>
  </dependency>
  ```
- [ ] **(2) 建立配置類別**
  ```java
  @Configuration
  public class ModelMapperConfig {
      @Bean
      public ModelMapper modelMapper() {
          ModelMapper modelMapper = new ModelMapper();
          modelMapper.getConfiguration()
              .setMatchingStrategy(MatchingStrategies.STRICT)
              .setSkipNullEnabled(true);
          return modelMapper;
      }
  }
  ```
- [ ] **(3) 注入 ModelMapper**
  ```java
  @RequiredArgsConstructor
  private final ModelMapper modelMapper;
  ```
- [ ] **(4) 使用 `map()` 進行轉換**
  ```java
  CustomerInfoEntity entity = modelMapper.map(data, CustomerInfoEntity.class);
  ```

### ✅ 進階功能
- [ ] **(5) Null 值處理**
  ```java
  modelMapper.getConfiguration().setSkipNullEnabled(true);
  ```
- [ ] **(6) 匹配策略設定**
  - STRICT / STANDARD / LOOSE

---

## Orika 檢核清單

### ✅ 基本設定
- [ ] **(1) 添加依賴**
  - `orika-core` 核心庫
  - `ecj` Eclipse JDT 編譯器 (必須)
- [ ] **(2) 建立配置類別**
  ```java
  @Configuration
  public class OrikaConfig {
      @Bean
      public MapperFactory orikaMapperFactory() {
          MapperFactory factory = new DefaultMapperFactory.Builder()
                  .compilerStrategy(new EclipseJdtCompilerStrategy())
                  .mapNulls(false)
                  .build();
          
          factory.classMap(SourceDto.class, TargetEntity.class)
                  .byDefault()
                  .register();
          
          return factory;
      }
      
      @Bean
      public MapperFacade orikaMapperFacade(MapperFactory factory) {
          return factory.getMapperFacade();
      }
  }
  ```
- [ ] **(3) 注入 MapperFacade**
  ```java
  @RequiredArgsConstructor
  private final MapperFacade orikaMapper;
  ```
- [ ] **(4) 註冊映射關係**
  ```java
  factory.classMap(SourceDto.class, TargetEntity.class)
          .byDefault()
          .register();  // 必須呼叫 register()
  ```
- [ ] **(5) 使用 `map()` 進行轉換**
  ```java
  CustomerInfoEntity entity = orikaMapper.map(data, CustomerInfoEntity.class);
  ```

### ✅ 進階功能
- [ ] **(6) Null 值處理**
  ```java
  .mapNulls(false)  // 不映射 null 值
  ```

---

# 10. 工具比較與選擇建議

## 綜合比較表

| 特性 | ObjectMapper | Apache BeanUtils | Spring BeanUtils | MapStruct | ModelMapper | Orika |
|------|--------------|------------------|------------------|-----------|-------------|-------|
| **需要額外依賴** | ⚠️ 通常內建 | ✅ 需要 | ❌ 內建 | ✅ 需要 2 個 | ✅ 需要 | ✅ 需要 2 個 |
| **需要配置類別** | ⚠️ 建議 | ❌ 不需要 | ❌ 不需要 | ❌ 不需要 | ✅ 建議 | ✅ 建議 |
| **需要注入** | ✅ 是 | ❌ 否 | ❌ 否 | ✅ 是 | ✅ 是 | ✅ 是 |
| **必須 try-catch** | ⚠️ 部分需要 | ✅ 是 | ❌ 否 | ❌ 否 | ❌ 否 | ❌ 否 |
| **參數順序** | - | (目標, 來源) | (來源, 目標) | - | - | - |
| **效能** | 🐇 中等 | 🐢 慢 | 🐇 快 | 🚀 極快 | 🐇 中等 | 🚀 極快 |
| **型別轉換** | ✅ 支援 | ✅ 支援 | ❌ 不支援 | ✅ 支援 | ✅ 智慧轉換 | ✅ 支援 |
| **Null 值處理** | readerForUpdating | populate | 自訂工具 | IGNORE 策略 | skipNullEnabled | mapNulls(false) |
| **學習曲線** | ⚠️ 中等 | ✅ 簡單 | ✅ 簡單 | ⚠️ 中等 | ⚠️ 中等 | ⚠️ 中高 |
| **編譯時檢查** | ❌ 否 | ❌ 否 | ❌ 否 | ✅ 是 | ❌ 否 | ❌ 否 |

## 效能比較 (100,000 次操作)

```
手動 setter:         ~30ms   (最快，但不彈性)
MapStruct:          ~35ms   (接近手寫) 🏆
Orika:              ~40ms   (極快) 🏆
ObjectMapper:       ~60ms   
Spring BeanUtils:   ~80ms   
ModelMapper:        ~120ms  
Apache BeanUtils:   ~500ms  (最慢)
```

## 選擇建議

### ✅ 企業級新專案：MapStruct
**理由**：
- 效能最佳
- 編譯時期型別檢查
- 程式碼清晰易維護
- 活躍的社群支援

### ✅ 快速開發/中小型專案：ModelMapper
**理由**：
- 智慧匹配，開發快速
- 配置簡單
- 效能可接受

### ✅ 追求極致效能：Orika
**理由**：
- 效能接近 MapStruct
- 靈活的配置選項
- 成熟穩定

### ⚠️ 簡單場景：Spring BeanUtils
**理由**：
- 無需額外依賴
- 使用簡單
- 適合簡單的屬性複製

### ❌ 不推薦

- **Apache BeanUtils**：效能太差，除非需要特殊的型別轉換功能
- **ObjectMapper** (for mapping)：主要用途是 JSON 處理，用於物件映射較為笨重

## 快速決策樹

```
需要選擇 Mapper 工具？
├─ 新專案？
│  ├─ 大型企業專案 → MapStruct ✅
│  └─ 中小型專案 → ModelMapper ✅
├─ 已有專案維護？
│  ├─ 效能有問題 → 考慮遷移到 MapStruct/Orika
│  └─ 效能可接受 → 保持現狀
├─ 簡單屬性複製？
│  └─ Spring BeanUtils ✅
└─ 需要複雜型別轉換？
   └─ ModelMapper / Apache BeanUtils
```

## 最終建議

對於大多數專案，我們推薦以下優先順序：

1. **MapStruct** - 首選，最全面的解決方案
2. **ModelMapper** - 次選，快速開發的好選擇
3. **Orika** - 效能要求極高時的選擇
4. **Spring BeanUtils** - 簡單場景的輕量級選擇

---

# 附錄

## 參考資源

### ObjectMapper
- [Jackson 官方文件](https://github.com/FasterXML/jackson-databind)
- [Spring Boot Jackson 配置](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.spring-mvc.customize-jackson-objectmapper)

### Apache BeanUtils
- [Apache Commons BeanUtils 官方文件](https://commons.apache.org/proper/commons-beanutils/)

### Spring BeanUtils
- [Spring Framework BeanUtils 官方文件](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/BeanUtils.html)

### MapStruct
- [MapStruct 官方網站](https://mapstruct.org/)
- [MapStruct GitHub](https://github.com/mapstruct/mapstruct)
- [MapStruct 參考文件](https://mapstruct.org/documentation/stable/reference/html/)

### ModelMapper
- [ModelMapper 官方網站](http://modelmapper.org/)
- [ModelMapper GitHub](https://github.com/modelmapper/modelmapper)

### Orika
- [Orika GitHub](https://github.com/orika-mapper/orika)
- [Orika 使用者指南](https://orika-mapper.github.io/orika-docs/)

---

**文件版本**: 1.0  
**最後更新**: 2024年  
**作者**: Technical Documentation Team  
**聯絡方式**: 請透過 GitHub Issues 提供反饋

---

## 版權聲明

本文件內容基於實際技術諮詢對話整理而成，僅供學習和參考使用。
所有提及的第三方函式庫和框架的版權歸其各自所有者所有。

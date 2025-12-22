# Mapper 效能測試報告

測試日期：Tue Dec 23 01:11:56 CST 2025

## 測試環境

- JVM: 24.0.2
- OS: Mac OS X 14.7.1
- CPU: 8 cores
- 測試配置: Fork=2, Warmup=3 iterations, Measurement=5 iterations

## 1. 平均執行時間 (Average Time)

**越低越好** (單位: 微秒 μs)

| Mapper | 平均時間 (μs) | 誤差範圍 | 排名 |
|--------|--------------|---------|------|
| testMapStruct | 0.02 | ±0.03 | 1 |
| testOrika | 1.64 | ±1.09 | 2 |
| testObjectMapper | 5.31 | ±6.90 | 3 |
| testSpringBeanUtils | 6.69 | ±5.64 | 4 |
| testModelMapper | 6.89 | ±0.76 | 5 |
| testApacheBeanUtils | 13.00 | ±3.43 | 6 |

### 效能分析

- **最快**: testMapStruct (0.02 μs)
- **最慢**: testApacheBeanUtils (13.00 μs) - 比最快慢 597.9 倍

## 2. 冷啟動時間 (Single Shot Time)

**越低越好** (單位: 微秒 μs)

| Mapper | 冷啟動時間 (μs) | 誤差範圍 | 排名 |
|--------|----------------|---------|------|
| testMapStruct | 2.60 | ±0.92 | 1 |
| testOrika | 51.55 | ±23.28 | 2 |
| testObjectMapper | 273.46 | ±281.58 | 3 |
| testSpringBeanUtils | 295.05 | ±434.70 | 4 |
| testApacheBeanUtils | 867.69 | ±359.66 | 5 |
| testModelMapper | 1,039.79 | ±911.44 | 6 |

### 冷啟動分析

-  **啟動最快**: testMapStruct (2.60 μs)
- 適合用於：需要快速初始化的場景

## 3. 吞吐量 (Throughput)

**越高越好** (單位: ops/μs - 每微秒操作數)

| Mapper | 吞吐量 (ops/μs) | 誤差範圍 | 排名 |
|--------|----------------|---------|------|
| testMapStruct | 138.03 | ±59.78 | 1 |
| testOrika | 2.87 | ±0.46 | 2 |
| testSpringBeanUtils | 1.41 | ±0.17 | 3 |
| testObjectMapper | 1.10 | ±0.24 | 4 |
| testModelMapper | 0.15 | ±0.02 | 5 |
| testApacheBeanUtils | 0.08 | ±0.02 | 6 |

### 吞吐量分析

- **吞吐量最高**: testMapStruct (138.03 ops/μs)
- 適合用於：高併發、大量數據轉換的場景


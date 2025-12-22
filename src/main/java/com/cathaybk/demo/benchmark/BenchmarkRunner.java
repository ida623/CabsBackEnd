package com.cathaybk.demo.benchmark;

import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class BenchmarkRunner {
    
    public static void main(String[] args) throws RunnerException, IOException {
        Options opt = new OptionsBuilder()
                .include(MapperPerformanceBenchmark.class.getSimpleName())
                .build();
        
        Collection<RunResult> results = new Runner(opt).run();
        
        // 生成 Markdown 報表
        generateMarkdownReport(results);
        
        System.out.println("\n測試完成！報表已生成：benchmark-report.md");
    }
    
    private static void generateMarkdownReport(Collection<RunResult> results) throws IOException {
        StringBuilder markdown = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#,##0.00");
        
        // 標題
        markdown.append("# Mapper 效能測試報告\n\n");
        markdown.append("測試日期：").append(new Date()).append("\n\n");
        
        // 測試環境資訊
        markdown.append("## 測試環境\n\n");
        markdown.append("- JVM: ").append(System.getProperty("java.version")).append("\n");
        markdown.append("- OS: ").append(System.getProperty("os.name")).append(" ").append(System.getProperty("os.version")).append("\n");
        markdown.append("- CPU: ").append(Runtime.getRuntime().availableProcessors()).append(" cores\n");
        markdown.append("- 測試配置: Fork=2, Warmup=3 iterations, Measurement=5 iterations\n\n");
        
        // 按測試模式分組
        Map<String, List<BenchmarkResult>> groupedResults = groupResultsByMode(results);
        
        // 1. 平均執行時間 (AverageTime)
        if (groupedResults.containsKey("avgt")) {
            markdown.append("## 1. 平均執行時間 (Average Time)\n\n");
            markdown.append("**越低越好** (單位: 微秒 μs)\n\n");
            markdown.append("| Mapper | 平均時間 (μs) | 誤差範圍 | 排名 |\n");
            markdown.append("|--------|--------------|---------|------|\n");
            
            List<BenchmarkResult> avgResults = groupedResults.get("avgt");
            avgResults.sort(Comparator.comparingDouble(r -> r.score));
            
            int rank = 1;
            for (BenchmarkResult result : avgResults) {
                markdown.append(String.format("| %s | %s | ±%s | %d |\n",
                        result.mapperName,
                        df.format(result.score),
                        df.format(result.error),
                        rank++));
            }
            markdown.append("\n");
            
            // 效能比較
            if (!avgResults.isEmpty()) {
                BenchmarkResult fastest = avgResults.get(0);
                markdown.append("### 效能分析\n\n");
                markdown.append(String.format("- **最快**: %s (%.2f μs)\n", fastest.mapperName, fastest.score));
                if (avgResults.size() > 1) {
                    BenchmarkResult slowest = avgResults.get(avgResults.size() - 1);
                    double ratio = slowest.score / fastest.score;
                    markdown.append(String.format("- **最慢**: %s (%.2f μs) - 比最快慢 %.1f 倍\n",
                            slowest.mapperName, slowest.score, ratio));
                }
                markdown.append("\n");
            }
        }
        
        // 2. 冷啟動時間 (SingleShotTime)
        if (groupedResults.containsKey("ss")) {
            markdown.append("## 2. 冷啟動時間 (Single Shot Time)\n\n");
            markdown.append("**越低越好** (單位: 微秒 μs)\n\n");
            markdown.append("| Mapper | 冷啟動時間 (μs) | 誤差範圍 | 排名 |\n");
            markdown.append("|--------|----------------|---------|------|\n");
            
            List<BenchmarkResult> ssResults = groupedResults.get("ss");
            ssResults.sort(Comparator.comparingDouble(r -> r.score));
            
            int rank = 1;
            for (BenchmarkResult result : ssResults) {
                markdown.append(String.format("| %s | %s | ±%s | %d |\n",
                        result.mapperName,
                        df.format(result.score),
                        df.format(result.error),
                        rank++));
            }
            markdown.append("\n");
            
            // 冷啟動分析
            if (!ssResults.isEmpty()) {
                BenchmarkResult fastest = ssResults.get(0);
                markdown.append("### 冷啟動分析\n\n");
                markdown.append(String.format("-  **啟動最快**: %s (%.2f μs)\n", fastest.mapperName, fastest.score));
                markdown.append("- 適合用於：需要快速初始化的場景\n\n");
            }
        }
        
        // 3. 吞吐量 (Throughput)
        if (groupedResults.containsKey("thrpt")) {
            markdown.append("## 3. 吞吐量 (Throughput)\n\n");
            markdown.append("**越高越好** (單位: ops/μs - 每微秒操作數)\n\n");
            markdown.append("| Mapper | 吞吐量 (ops/μs) | 誤差範圍 | 排名 |\n");
            markdown.append("|--------|----------------|---------|------|\n");
            
            List<BenchmarkResult> thrptResults = groupedResults.get("thrpt");
            thrptResults.sort((r1, r2) -> Double.compare(r2.score, r1.score)); // 降序
            
            int rank = 1;
            for (BenchmarkResult result : thrptResults) {
                markdown.append(String.format("| %s | %s | ±%s | %d |\n",
                        result.mapperName,
                        df.format(result.score),
                        df.format(result.error),
                        rank++));
            }
            markdown.append("\n");
            
            // 吞吐量分析
            if (!thrptResults.isEmpty()) {
                BenchmarkResult highest = thrptResults.get(0);
                markdown.append("### 吞吐量分析\n\n");
                markdown.append(String.format("- **吞吐量最高**: %s (%.2f ops/μs)\n", highest.mapperName, highest.score));
                markdown.append("- 適合用於：高併發、大量數據轉換的場景\n\n");
            }
        }

        // 寫入檔案
        try (FileWriter writer = new FileWriter("benchmark-report.md")) {
            writer.write(markdown.toString());
        }
    }
    
    private static Map<String, List<BenchmarkResult>> groupResultsByMode(Collection<RunResult> results) {
        Map<String, List<BenchmarkResult>> grouped = new HashMap<>();
        
        for (RunResult result : results) {
            String benchmarkName = result.getPrimaryResult().getLabel();
            String mapperName = extractMapperName(benchmarkName);
            String mode = result.getParams().getMode().shortLabel();
            
            Result<?> primaryResult = result.getPrimaryResult();
            double score = primaryResult.getScore();
            double error = primaryResult.getStatistics().getMeanErrorAt(0.999);
            
            grouped.computeIfAbsent(mode, k -> new ArrayList<>())
                   .add(new BenchmarkResult(mapperName, score, error));
        }
        
        return grouped;
    }
    
    private static String extractMapperName(String benchmarkName) {
        // 從 "MapperPerformanceBenchmark.testMapStruct" 提取 "MapStruct"
        if (benchmarkName.contains(".test")) {
            String name = benchmarkName.substring(benchmarkName.lastIndexOf(".test") + 5);
            return name;
        }
        return benchmarkName;
    }
    
    static class BenchmarkResult {
        String mapperName;
        double score;
        double error;
        
        BenchmarkResult(String mapperName, double score, double error) {
            this.mapperName = mapperName;
            this.score = score;
            this.error = error;
        }
    }
}
package io.github.tnas.introspectorfilter.util;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;

public class PerformanceLogger {

    private static final String MEM_REPORT_FILE = "memory_report.txt";
    private static final String RUN_REPORT_FILE = "runtime_report.txt";

    public static void initLogMemory() throws IOException {
        if (Files.notExists(Path.of(MEM_REPORT_FILE))) {
            new FileWriter(MEM_REPORT_FILE, false).close();
        }
    }

    public static void initRuntimeMemory() throws IOException {
        if (Files.notExists(Path.of(RUN_REPORT_FILE))) {
            new FileWriter(RUN_REPORT_FILE, false).close();
        }
    }

    public static void logMemoryUsage(String phase) throws IOException {

        var memoryBean = ManagementFactory.getMemoryMXBean();
        var heapUsage = memoryBean.getHeapMemoryUsage();
        var nonHeapUsage = memoryBean.getNonHeapMemoryUsage();

        var report = String.format("%s - Heap: Used %d MB, Max %d MB | Non-Heap: Used %d MB%n", phase,
                heapUsage.getUsed() / (1024 * 1024),
                heapUsage.getMax() / (1024 * 1024),
                nonHeapUsage.getUsed() / (1024 * 1024)
        );

        // Append to report file
        try (FileWriter writer = new FileWriter(MEM_REPORT_FILE, true)) {
            writer.write(report);
        }
    }

    public static void logElapsedRuntime(String message) throws IOException {
        try (FileWriter writer = new FileWriter(RUN_REPORT_FILE, true)) {
            writer.write(message);
        }
    }
}

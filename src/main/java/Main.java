
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    private static final int port = 9999;
    private static final int sizeThreadPool = 64;

    public static void main(String[] args) {
        Server server = new Server(port, sizeThreadPool);

        server.addHandler("GET", "/classic.html", (request, out) -> {
            try {
                final var filePath = Path.of(".", "public", request.getPath());
                final var mimeType = Files.probeContentType(filePath);
                System.out.println("events.html: " + request.getPath());
                final var template = Files.readString(filePath);
                final var content = template.replace("{time}",
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))
                        .getBytes();
                outWrite(mimeType, content, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        server.addHandler("POST", "/events.html", (request, out) -> {
            try {
                final var filePath = Path.of(".", "public", request.getPath());
                final var mimeType = Files.probeContentType(filePath);
                System.out.println("events.html: " + request.getPath());
                final var content = Files.readAllBytes(filePath);
                outWrite(mimeType, content, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        server.started();
    }

    private static void outWrite(String mimeType, byte[] content, BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + content.length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.write(content);
        out.flush();
    }
}



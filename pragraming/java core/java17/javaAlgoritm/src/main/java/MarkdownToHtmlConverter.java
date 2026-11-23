

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import java.util.List;


public class MarkdownToHtmlConverter {

    public static void main(String[] args) {

       // String name="java core";
        String name="broker";
        Path inputPath =Path.of(System.getProperty("user.dir")+ File.separator+name+
                ".md");
        Path outputPath = outputPath = Path.of(System.getProperty("user.dir")+ File.separator+name+
                ".html");
        if (args.length >2) {
             inputPath = Path.of(args[0]);
             outputPath = Path.of(args[1]);
             name = String.valueOf(Path.of(args[2]));
        }




        try {
            convert(inputPath, outputPath);
            System.out.println("تبدیل با موفقیت انجام شد: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("خطا در خواندن یا نوشتن فایل: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void convert(Path inputPath, Path outputPath) throws IOException {
        if (!Files.exists(inputPath)) {
            throw new IOException("فایل ورودی یافت نشد: " + inputPath);
        }

        String markdownContent = Files.readString(inputPath, StandardCharsets.UTF_8);


        List<Extension> extensions = List.of(TablesExtension.create());

// 2. Build the Parser with the extensions
        Parser parser = Parser.builder()
                .extensions(extensions)
                .build();

        Node document = parser.parse(markdownContent);

// 3. Build the HtmlRenderer with the same extensions
        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(extensions)
                .build();

        String htmlBody = renderer.render(document);

        String fullHtml = """
                <!DOCTYPE html>
                <html lang="fa" dir="auto">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                    <style>
                        body {
                            font-family: system-ui, -apple-system, sans-serif;
                            max-width: 800px;
                            margin: 40px auto;
                            padding: 0 20px;
                            line-height: 1.7;
                            color: #222;
                        }
                        pre {
                            background: #f4f4f4;
                            padding: 12px;
                            border-radius: 6px;
                            overflow-x: auto;
                            direction: ltr;
                            text-align: left;
                        }
                        code {
                            background: #f4f4f4;
                            padding: 2px 5px;
                            border-radius: 4px;
                            direction: ltr;
                        }
                        blockquote {
                            border-right: 4px solid #ccc;
                            margin: 16px 0;
                            padding-right: 16px;
                            color: #555;
                        }
                        table { border-collapse: collapse; margin: 16px 0; }
                        th, td { border: 1px solid #ccc; padding: 6px 12px; }
                        th { background: #f4f4f4; }
                    </style>
                </head>
                <body>
                %s
                </body>
                </html>
                """.formatted(escapeHtmlTitle(inputPath.getFileName().toString()), htmlBody);

        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }
        Files.writeString(outputPath, fullHtml, StandardCharsets.UTF_8);
    }

    private static String escapeHtmlTitle(String title) {
        return title.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;");
    }
}
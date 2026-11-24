package markdown;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.HtmlWriter;

public class MarkdownToHtmlConverter {

    /** نام فایل JS محلی در ریشه پروژه */
    private static final String MERMAID_LOCAL_FILE = "mermaid.min.js";

    public static void main(String[] args) {
        String defaultName = "doc";

      //  Path inputPath  = Path.of(System.getProperty("user.dir"), defaultName + ".md");
        Path inputPath  = Path.of("/home/salah/IdeaProjects/Document/14050810_تحلیل و شناخت_PCRS_PC-Provider (1)/test.md");
        Path outputPath = Path.of(System.getProperty("user.dir"), defaultName + ".html");

        if (args.length >= 2) {
            inputPath  = Path.of(args[0]);
            outputPath = Path.of(args[1]);
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

        Parser parser = Parser.builder()
                .extensions(extensions)
                .build();

        Node document = parser.parse(markdownContent);

        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(extensions)
                .nodeRendererFactory(MermaidNodeRenderer::new)
                .build();

        String htmlBody = renderer.render(document);

        String title = inputPath.getFileName().toString().replaceFirst("\\.md$", "");

        // ---- ساخت بلوک اسکریپت Mermaid ----
        // اولویت: 1) فایل محلی کنار پروژه 2) کنار خود فایل md 3) CDN
        Path localJs1 = Path.of(System.getProperty("user.dir"), MERMAID_LOCAL_FILE);
        Path localJs2 = (inputPath.getParent() != null)
                ? inputPath.getParent().resolve(MERMAID_LOCAL_FILE)
                : null;

        String mermaidScriptTag;
        String mermaidJsContent = null;

        if (Files.exists(localJs1)) {
            mermaidJsContent = Files.readString(localJs1, StandardCharsets.UTF_8);
            System.out.println("Mermaid JS از فایل محلی بارگذاری شد: " + localJs1.toAbsolutePath());
        } else if (localJs2 != null && Files.exists(localJs2)) {
            mermaidJsContent = Files.readString(localJs2, StandardCharsets.UTF_8);
            System.out.println("Mermaid JS از فایل محلی بارگذاری شد: " + localJs2.toAbsolutePath());
        }

        if (mermaidJsContent != null) {
            // جلوگیری از بستن ناخواسته تگ <script> داخل محتوای JS
            String safeJs = mermaidJsContent.replace("</script>", "<\\/script>");
            mermaidScriptTag = "<script>\n" + safeJs + "\n</script>";
        } else {
            System.err.println("⚠️  هشدار: فایل '" + MERMAID_LOCAL_FILE + "' پیدا نشد.");
            System.err.println("    برای کار آفلاین، یکبار این فایل را دانلود کنید:");
            System.err.println("    curl -L -o " + MERMAID_LOCAL_FILE
                    + " https://cdn.jsdelivr.net/npm/mermaid@11/dist/mermaid.min.js");
            System.err.println("    فعلاً از CDN استفاده می‌شود (نیاز به اینترنت).");
            mermaidScriptTag = "<script src=\"https://cdn.jsdelivr.net/npm/mermaid@11/dist/mermaid.min.js\"></script>";
        }

        String initScript = """
                <script>
                    mermaid.initialize({
                        startOnLoad: true,
                        theme: 'default',
                        securityLevel: 'loose'
                    });
                </script>
                """;

        String fullHtml = """
                <!DOCTYPE html>
                <html lang="fa">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                    <style>
                        html, body {
                            margin: 0;
                            padding: 0;
                            background: #ffffff;
                            color: #222222;
                        }
                        .content {
                            max-width: 900px;
                            margin: 40px auto;
                            padding: 0 24px 60px 24px;
                            direction: ltr;
                            text-align: left;
                            font-family: 'B Nazanin', Tahoma, sans-serif;
                            font-size: 17px;
                            line-height: 1.9;
                        }
                        h1, h2, h3, h4, h5, h6 {
                            font-family: 'B Nazanin', Tahoma, sans-serif;
                            margin-top: 1.4em;
                            margin-bottom: 0.6em;
                        }
                        p { margin: 0.8em 0; }
                        pre {
                            background: #f4f4f4;
                            padding: 12px 16px;
                            border-radius: 6px;
                            overflow-x: auto;
                            direction: ltr;
                            text-align: left;
                            font-family: 'JetBrains Mono', 'Consolas', 'Courier New', monospace;
                            font-size: 14px;
                            line-height: 1.55;
                        }
                        code {
                            background: #f4f4f4;
                            padding: 2px 6px;
                            border-radius: 4px;
                            direction: ltr;
                            font-family: 'JetBrains Mono', 'Consolas', 'Courier New', monospace;
                            font-size: 0.92em;
                        }
                        pre code {
                            background: transparent;
                            padding: 0;
                            border-radius: 0;
                            font-size: inherit;
                        }
                        blockquote {
                            border-right: 4px solid #cccccc;
                            margin: 16px 0;
                            padding: 0 16px 0 0;
                            color: #555555;
                        }
                        table {
                            border-collapse: collapse;
                            margin: 16px 0;
                            width: 100%%;
                        }
                        th, td {
                            border: 1px solid #cccccc;
                            padding: 8px 12px;
                            text-align: right;
                            vertical-align: top;
                        }
                        th { background: #f4f4f4; }
                        img { max-width: 100%%; height: auto; }
                        .mermaid {
                            direction: ltr;
                            text-align: center;
                            margin: 24px 0;
                            padding: 12px;
                            background: #fafafa;
                            border-radius: 8px;
                            overflow-x: auto;
                        }
                    </style>
                </head>
                <body>
                <div class="content">
                %s
                </div>

                %s
                %s
                </body>
                </html>
                """.formatted(
                escapeHtmlTitle(title),
                htmlBody,
                mermaidScriptTag,
                initScript
        );

        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }
        Files.writeString(outputPath, fullHtml, StandardCharsets.UTF_8);

        if (mermaidJsContent != null) {
            System.out.println("✅ خروجی HTML کاملاً آفلاین است (Mermaid درون فایل جاسازی شد).");
        }
    }

    private static String escapeHtmlTitle(String title) {
        return title.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;");
    }

    public static class MermaidNodeRenderer implements NodeRenderer {

        private final HtmlWriter html;

        public MermaidNodeRenderer(HtmlNodeRendererContext context) {
            this.html = context.getWriter();
        }

        @Override
        public Set<Class<? extends Node>> getNodeTypes() {
            return Collections.singleton(FencedCodeBlock.class);
        }

        @Override
        public void render(Node node) {
            FencedCodeBlock codeBlock = (FencedCodeBlock) node;
            String info    = codeBlock.getInfo();
            String literal = codeBlock.getLiteral();

            boolean isMermaid = info != null
                    && info.trim().toLowerCase().startsWith("mermaid");

            html.line();
            if (isMermaid) {
                html.tag("div", Map.of("class", "mermaid"));
                html.text(literal);
                html.tag("/div");
            } else {
                html.tag("pre");

                String languageClass = null;
                if (info != null && !info.trim().isEmpty()) {
                    String firstWord = info.trim().split("\\s+")[0];
                    languageClass = "language-" + firstWord;
                }

                if (languageClass != null) {
                    html.tag("code", Map.of("class", languageClass));
                } else {
                    html.tag("code");
                }

                html.text(literal);
                html.tag("/code");
                html.tag("/pre");
            }
            html.line();
        }
    }
}
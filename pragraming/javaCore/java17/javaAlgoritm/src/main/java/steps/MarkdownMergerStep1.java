package steps;

import markdown.MarkdownToHtmlConverter;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MarkdownMergerStep1 {

    // <img src="...">  (هم " و هم ')
    private static final Pattern HTML_IMG = Pattern.compile(
            "(<img\\b[^>]*?\\bsrc\\s*=\\s*)([\"'])([^\"']+)\\2",
            Pattern.CASE_INSENSITIVE);

    // ![alt](url)  و  ![alt](url "title")
    private static final Pattern MD_IMG = Pattern.compile(
            "(!\\[[^\\]]*\\]\\()([^)\\s]+)((?:\\s+\"[^\"]*\")?\\))");

    public static void main(String[] args) {
        try (var scanner = new java.util.Scanner(System.in)) {
            String htmlName = args[0];
            int x = Integer.parseInt(htmlName.split("-")[0]);
            int y = Integer.parseInt(htmlName.split("-")[1]);

            Properties props = new Properties();
            try (var reader = new InputStreamReader(
                    new FileInputStream("step.properties"),
                    StandardCharsets.UTF_8)) {
                props.load(reader);
            }

            ArrayList<String> step0 = new ArrayList<>();
            for (var i = x; i <= y; i++) {
                step0.add(props.getProperty(i + ""));
            }
            generateHtml(htmlName, step0);
        } catch (Exception e) {
            System.err.println("خطایی رخ داد: " + e.getMessage());
        }
    }

    private static void generateHtml(String htmlName, ArrayList<String> step0) throws IOException {
        String outputFileName = htmlName + ".md";

        Set<Path> inputPath = step0.stream().map(Paths::get).collect(Collectors.toSet());
        Path outputPath = Paths.get(outputFileName);

        mergeMarkdownFiles(inputPath, outputPath);

        MarkdownToHtmlConverter.convert(outputPath,
                Path.of(System.getProperty("user.dir") + File.separator + htmlName + ".html"));
    }

    /**
     * متد اصلی برای جستجو و ادغام فایل‌ها (با جاسازی تصاویر به‌صورت Base64)
     */
    public static void mergeMarkdownFiles(Set<Path> sourceDirs, Path outputFile) throws IOException {
        List<Path> mdFiles = new ArrayList<>();
        Path outAbs = outputFile.toAbsolutePath().normalize();

        for (Path sourceDir : sourceDirs) {
            try (Stream<Path> walk = Files.walk(sourceDir)) {
                mdFiles.addAll(walk
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().toLowerCase().endsWith(".md"))
                        // فایل خروجی را از لیست حذف کن
                        .filter(path -> !path.toAbsolutePath().normalize().equals(outAbs))
                        .sorted()
                        .collect(Collectors.toList()));
            }
        }

        // ←← بررسی خارج از حلقه (باگ قبلی رفع شد)
        if (mdFiles.isEmpty()) {
            System.out.println("هیچ فایل .md پیدا نشد.");
            return;
        }
        System.out.println("تعداد فایل‌های یافت شده: " + mdFiles.size());

        try (BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
            for (Path file : mdFiles) {
                appendFileContent(file, writer);
            }
        }

        System.out.println("عملیات با موفقیت انجام شد!");
        System.out.println("فایل نهایی در اینجا ذخیره شد: " + outputFile.toAbsolutePath());
    }

    /**
     * خواندن محتوای فایل، جایگزینی تصاویر با Base64، نوشتن در فایل خروجی
     */
    private static void appendFileContent(Path file, BufferedWriter writer) throws IOException {
        writer.write("\n\n<!-- SOURCE: " + file.getFileName() + " -->\n");
        writer.write("# File: " + file.getFileName() + "\n\n");

        String content = Files.readString(file, StandardCharsets.UTF_8);
        Path baseDir = file.getParent();  // تصاویر نسبت به همین پوشه resolve می‌شوند

        content = replaceHtmlImages(content, baseDir);
        content = replaceMarkdownImages(content, baseDir);

        writer.write(content);
        writer.newLine();
    }

    /** جایگزینی <img src="..."> با data URI */
    private static String replaceHtmlImages(String content, Path baseDir) {
        Matcher m = HTML_IMG.matcher(content);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String dataUri = toDataUri(m.group(3), baseDir);
            String replacement = m.group(1) + m.group(2) + dataUri + m.group(2);
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /** جایگزینی ![alt](url) با data URI */
    private static String replaceMarkdownImages(String content, Path baseDir) {
        Matcher m = MD_IMG.matcher(content);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String dataUri = toDataUri(m.group(2), baseDir);
            m.appendReplacement(sb, Matcher.quoteReplacement(m.group(1) + dataUri + m.group(3)));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * اگر مسیر تصویر محلی باشد، فایل را می‌خوانیم و به data URI تبدیل می‌کنیم.
     * لینک‌های http/https و data URI دست‌نخورده می‌مانند.
     */
    private static String toDataUri(String src, Path baseDir) {
        if (src.startsWith("data:")
                || src.matches("(?i)^(https?|ftp)://.*")
                || src.startsWith("//")) {
            return src;
        }

        // آدرس ممکن است URL-encode شده باشد (مثل %20)
        String decoded = src;
        try {
            decoded = URLDecoder.decode(src, StandardCharsets.UTF_8);
        } catch (Exception ignored) { }

        Path imgPath = baseDir.resolve(decoded).normalize();
        if (!Files.isRegularFile(imgPath)) {
            System.err.println("⚠ تصویر پیدا نشد: " + imgPath);
            return src; // همان مسیر اصلی
        }

        try {
            byte[] bytes = Files.readAllBytes(imgPath);
            String mime = Files.probeContentType(imgPath);
            if (mime == null) mime = guessMime(imgPath);
            return "data:" + mime + ";base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            System.err.println("⚠ خطا در خواندن تصویر: " + imgPath + " - " + e.getMessage());
            return src;
        }
    }

    private static String guessMime(Path p) {
        String name = p.getFileName().toString().toLowerCase();
        if (name.endsWith(".png"))  return "image/png";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".gif"))  return "image/gif";
        if (name.endsWith(".svg"))  return "image/svg+xml";
        if (name.endsWith(".webp")) return "image/webp";
        if (name.endsWith(".bmp"))  return "image/bmp";
        if (name.endsWith(".ico"))  return "image/x-icon";
        return "application/octet-stream";
    }
}
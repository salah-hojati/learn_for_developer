package steps;

import markdown.MarkdownToHtmlConverter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MarkdownMergerStep1 {

    public static void main(String[] args) {
        // دریافت مسیر ورودی از کاربر
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

        Set<Path> inputPath = step0.stream().map(s -> Paths.get(s)).collect(Collectors.toSet());
        Path outputPath = Paths.get(outputFileName);


        mergeMarkdownFiles(inputPath, outputPath);
        MarkdownToHtmlConverter.convert(outputPath, Path.of(System.getProperty("user.dir") + File.separator + htmlName + ".html"));
    }

    /**
     * متد اصلی برای جستجو و ادغام فایل‌ها
     */
    public static void mergeMarkdownFiles(Set<Path> sourceDirs, Path outputFile) throws IOException {
        //System.out.println("در حال جستجو در مسیر: " + sourceDir.toAbsolutePath());
        List<Path> mdFiles = new ArrayList<>();
        for (Path sourceDir : sourceDirs) {
            try (Stream<Path> walk = Files.walk(sourceDir)) {

                // پیدا کردن تمام فایل‌هایی که پسوند .md دارند

                mdFiles.addAll(walk
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().toLowerCase().endsWith(".md"))
                        .sorted() // مرتب‌سازی بر اساس نام برای نظم بیشتر
                        .collect(Collectors.toList()));

                if (mdFiles.isEmpty()) {
                    System.out.println("هیچ فایل .md پیدا نشد.");
                    return;
                }

                System.out.println("تعداد فایل‌های یافت شده: " + mdFiles.size());
            }
        }
        // استفاده از Files.walk برای پیمایش تمام زیرپوشه‌ها

        // ایجاد یا باز کردن فایل خروجی برای نوشتن
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
            for (Path file : mdFiles) {
                // جلوگیری از اینکه فایل خروجی خودش را داخل خودش ادغام کند (اگر در همان پوشه باشد)
                if (file.equals(outputFile.toAbsolutePath())) {
                    continue;
                }

                appendFileContent(file, writer);
            }
        }

        System.out.println("عملیات با موفقیت انجام شد!");
        System.out.println("فایل نهایی در اینجا ذخیره شد: " + outputFile.toAbsolutePath());

    }

    /**
     * متد کمکی برای خواندن محتوای هر فایل و نوشتن در فایل اصلی
     */
    private static void appendFileContent(Path file, BufferedWriter writer) throws IOException {
        // اضافه کردن یک جداکننده و نام فایل برای نظم در فایل نهایی (اختیاری)
        writer.write("\n\n<!-- SOURCE: " + file.getFileName() + " -->\n");
        writer.write("# File: " + file.getFileName() + "\n\n");

        // خواندن تمام خطوط فایل و نوشتن در writer
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }

        // اطمینان از وجود فاصله بین فایل‌ها
        writer.newLine();
    }
}

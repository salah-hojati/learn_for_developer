import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MarkdownMergerJee {

    public static void main(String[] args) {
        // دریافت مسیر ورودی از کاربر
        try (var scanner = new java.util.Scanner(System.in)) {
            System.out.print("لطفاً مسیر پوشه حاوی فایل‌های md را وارد کنید: ");
            String inputPathString ="C:\\Users\\salah1\\IdeaProjects\\learn_for_developer\\pragraming\\jee";

            System.out.print("نام فایل خروجی را وارد کنید (مثلاً merged.md): ");

            String outputFileName = "jee.md";

            Path inputPath = Paths.get(inputPathString);
            Path outputPath = Paths.get(outputFileName);

            if (!Files.exists(inputPath) || !Files.isDirectory(inputPath)) {
                System.err.println("خطا: مسیر وارد شده معتبر نیست یا یک پوشه نیست.");
                return;
            }

            mergeMarkdownFiles(inputPath, outputPath);

        } catch (Exception e) {
            System.err.println("خطایی رخ داد: " + e.getMessage());
        }
    }

    /**
     * متد اصلی برای جستجو و ادغام فایل‌ها
     */
    public static void mergeMarkdownFiles(Path sourceDir, Path outputFile) throws IOException {
        System.out.println("در حال جستجو در مسیر: " + sourceDir.toAbsolutePath());

        // استفاده از Files.walk برای پیمایش تمام زیرپوشه‌ها
        try (Stream<Path> walk = Files.walk(sourceDir)) {
            
            // پیدا کردن تمام فایل‌هایی که پسوند .md دارند
            List<Path> mdFiles = walk
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(".md"))
                    .sorted() // مرتب‌سازی بر اساس نام برای نظم بیشتر
                    .collect(Collectors.toList());

            if (mdFiles.isEmpty()) {
                System.out.println("هیچ فایل .md پیدا نشد.");
                return;
            }

            System.out.println("تعداد فایل‌های یافت شده: " + mdFiles.size());

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

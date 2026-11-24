import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownCodeBlockWrapper {

    private static final String RTL_OPEN =
            "<div dir=\"rtl\" style=\"text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;\">";

    private static final String LTR_OPEN =
            "<div dir=\"ltr\" style=\"text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;\">";

    // ``` یا ~~~ با حداکثر ۳ فاصله در ابتدای خط
    private static final Pattern OPEN_FENCE =
            Pattern.compile("^( {0,3})(`{3,}|~{3,})(.*)$");

    // خط بستن بلوک کد
    private static final Pattern CLOSE_FENCE =
            Pattern.compile("^ {0,3}(`{3,}|~{3,})\\s*$");

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java MarkdownCodeBlockWrapper input.md output.md");
            return;
        }

        Path inputPath = Path.of(args[0]);
        Path outputPath = Path.of(args[1]);

        try {
            String markdown = Files.readString(inputPath, StandardCharsets.UTF_8);
            String result = wrapCodeBlocks(markdown);

            Files.writeString(outputPath, result, StandardCharsets.UTF_8);
            System.out.println("فایل خروجی ساخته شد: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("خطا: " + e.getMessage());
        }
    }

    public static String wrapCodeBlocks(String markdown) {
        List<String> lines = markdown.lines().toList();

        StringBuilder out = new StringBuilder();

        // شروع کل فایل با div راست‌چین
        out.append(RTL_OPEN).append("\n\n");

        boolean inCodeBlock = false;
        char fenceChar = 0;
        int fenceLength = 0;

        for (String line : lines) {
            if (!inCodeBlock) {
                Matcher openMatcher = OPEN_FENCE.matcher(line);

                if (openMatcher.matches()) {
                    // قبل از div انگلیسی، اگر خط قبلی خالی نیست، یک خط خالی بگذار
                    if (!endsWithBlankLine(out)) {
                        out.append('\n');
                    }

                    out.append(LTR_OPEN).append("\n\n");
                    out.append(line).append('\n');

                    inCodeBlock = true;
                    fenceChar = openMatcher.group(2).charAt(0);
                    fenceLength = openMatcher.group(2).length();
                } else {
                    out.append(line).append('\n');
                }
            } else {
                out.append(line).append('\n');

                Matcher closeMatcher = CLOSE_FENCE.matcher(line);
                if (closeMatcher.matches()) {
                    String fence = closeMatcher.group(1);

                    if (fence.charAt(0) == fenceChar && fence.length() >= fenceLength) {
                        out.append('\n')
                           .append("</div>")
                           .append("\n\n");

                        inCodeBlock = false;
                    }
                }
            }
        }

        // اگر بلوک کد بسته نشده بود
        if (inCodeBlock) {
            out.append('\n').append("</div>").append("\n\n");
        }

        // بستن div کل فایل
        out.append("</div>").append('\n');

        return out.toString();
    }

    private static boolean endsWithBlankLine(StringBuilder sb) {
        int len = sb.length();
        return len >= 2 && sb.charAt(len - 1) == '\n' && sb.charAt(len - 2) == '\n';
    }
}
package com.example.util.regex;

import java.util.regex.Pattern;

/**
 * 300+ ready-to-use {@link Pattern} constants for common validation and
 * extraction tasks in Java.
 *
 * <p><b>Design notes:</b></p>
 * <ul>
 *   <li>All {@code Pattern}s are pre-compiled, immutable, and thread-safe.</li>
 *   <li>Reuse these constants instead of {@code String.matches()} (which recompiles on every call).</li>
 *   <li>Always null-check input; use {@link #matches(Pattern, String)} for convenience.</li>
 *   <li>For Persian input, call {@link #normalizeDigits(String)} before digit-based validators.</li>
 *   <li>Use {@code String#strip()} (Unicode-aware), not {@code trim()} (ASCII-only).</li>
 *   <li>Source file must be UTF-8 for patterns with Persian literals (e.g., {@code تهران}).</li>
 * </ul>
 *
 * <h2>Quick Start</h2>
 * <pre>{@code
 * import static com.example.util.regex.RegexPatterns.Users.*;
 *
 * if (matches(EMAIL_PRACTICAL, input)) {
 *     // ...
 * }
 * }</pre>
 *
 * @since 1.0
 */
public final class RegexPatterns {

    private RegexPatterns() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    // ═══════════════════════════════════════════════════════════════════
    // Helper methods
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Null-safe equivalent of {@code pattern.matcher(input).matches()}.
     *
     * @param pattern the pattern
     * @param input   the input; may be {@code null}
     * @return {@code true} if input is non-null and matches
     */
    public static boolean matches(Pattern pattern, String input) {
        return input != null && pattern.matcher(input).matches();
    }

    /**
     * Null-safe {@code String.strip()}.
     *
     * @param input the input; may be {@code null}
     * @return stripped string, or {@code null}
     */
    public static String safeStrip(String input) {
        return input == null ? null : input.strip();
    }

    /**
     * Converts Persian ({@code ۰-۹}) and Arabic ({@code ٠-٩}) digits to
     * ASCII ({@code 0-9}). Other characters are preserved.
     *
     * @param input the input; may be {@code null}
     * @return normalized string, or {@code null}
     */
    public static String normalizeDigits(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            if (c >= '\u06F0' && c <= '\u06F9')      sb.append((char) ('0' + (c - '\u06F0')));
            else if (c >= '\u0660' && c <= '\u0669') sb.append((char) ('0' + (c - '\u0660')));
            else                                     sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Validates an Iranian national code (10 digits + checksum).
     * Handles Persian/Arabic digits via {@link #normalizeDigits(String)}.
     *
     * @param code the national code
     * @return {@code true} if format and checksum are valid
     */
    public static boolean isValidNationalCode(String code) {
        if (code == null) return false;
        String n = normalizeDigits(code.strip());
        if (!Iranian.NATIONAL_CODE.matcher(n).matches()) return false;
        int sum = 0;
        for (int i = 0; i < 9; i++) sum += (n.charAt(i) - '0') * (10 - i);
        int r = sum % 11, last = n.charAt(9) - '0';
        return (r < 2 && last == r) || (r >= 2 && last == 11 - r);
    }

    // ═══════════════════════════════════════════════════════════════════
    // 1. NUMBERS (1-35)
    // ═══════════════════════════════════════════════════════════════════

    /** Numeric patterns. */
    public static final class Numbers {
        private Numbers() { throw new AssertionError(); }

        /** A single digit {@code 0-9}. Example: {@code 5}. */
        public static final Pattern DIGIT = Pattern.compile("\\d");
        /** Signed integer. Example: {@code -42}. */
        public static final Pattern SIGNED_INTEGER = Pattern.compile("-?\\d+");
        /** Unsigned integer. Example: {@code 123}. */
        public static final Pattern UNSIGNED_INTEGER = Pattern.compile("\\d+");
        /** Decimal number. Example: {@code 3.14}. */
        public static final Pattern DECIMAL = Pattern.compile("-?\\d+\\.\\d+");
        /** Thousands-separated. Example: {@code 1,234,567}. */
        public static final Pattern NUMBER_WITH_COMMA = Pattern.compile("\\d{1,3}(,\\d{3})*");
        /** Number with optional sign. Example: {@code +42}. */
        public static final Pattern SIGNED_NUMBER = Pattern.compile("[+-]?\\d+");
        /** Scientific notation. Example: {@code 1.5e10}. */
        public static final Pattern SCIENTIFIC = Pattern.compile("-?\\d+(\\.\\d+)?[eE][+-]?\\d+");
        /** Binary. Example: {@code 1010}. */
        public static final Pattern BINARY = Pattern.compile("^[01]+$");
        /** Hexadecimal. Example: {@code 0xFF}. */
        public static final Pattern HEX = Pattern.compile("^0x[0-9A-Fa-f]+$");
        /** Octal. Example: {@code 0755}. */
        public static final Pattern OCTAL = Pattern.compile("^0[0-7]+$");
        /** Positive integer without leading zero. Example: {@code 42}. */
        public static final Pattern POSITIVE_NO_LEADING_ZERO = Pattern.compile("^[1-9]\\d*$");
        /** Integer 0-100. Example: {@code 100}. */
        public static final Pattern RANGE_0_100 = Pattern.compile("^(100|[1-9]?\\d)$");
        /** Integer 1-1000. Example: {@code 1000}. */
        public static final Pattern RANGE_1_1000 = Pattern.compile("^([1-9]\\d{0,2}|1000)$");
        /** Exactly 3 digits. Example: {@code 123}. */
        public static final Pattern THREE_DIGITS = Pattern.compile("^\\d{3}$");
        /** Price with up to 2 decimals. Example: {@code 99.99}. */
        public static final Pattern PRICE_TWO_DECIMALS = Pattern.compile("^\\d+(\\.\\d{1,2})?$");
        /** Roman numeral. Example: {@code XIV}. */
        public static final Pattern ROMAN_NUMERAL = Pattern.compile("^[IVXLCDM]+$");
        /** Percentage. Example: {@code 75%}. */
        public static final Pattern PERCENTAGE = Pattern.compile("^\\d+(\\.\\d+)?%$");
        /** Amount with unit. Example: {@code 5000 تومان}. */
        public static final Pattern PRICE_WITH_UNIT = Pattern.compile("^\\d+\\s*(تومان|ریال)$");
        /** Number with leading zeros. Example: {@code 007}. */
        public static final Pattern LEADING_ZERO_NUMBER = Pattern.compile("^0\\d+$");
        /** Simple IPv4 (no range check). Example: {@code 192.168.1.1}. */
        public static final Pattern IP_SIMPLE = Pattern.compile("^(\\d{1,3}\\.){3}\\d{1,3}$");
        /** Strict IPv4 (0-255 per octet). Example: {@code 192.168.1.1}. */
        public static final Pattern IP_STRICT = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|1?\\d?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1?\\d?\\d)$");
        /** Network port. Example: {@code 8080}. */
        public static final Pattern PORT = Pattern.compile("^\\d{1,5}$");
        /** Year 1900-2099. Example: {@code 2024}. */
        public static final Pattern YEAR = Pattern.compile("^(19|20)\\d{2}$");
        /** Month 1-12. Example: {@code 12}. */
        public static final Pattern MONTH = Pattern.compile("^(0?[1-9]|1[0-2])$");
        /** Day 1-31. Example: {@code 31}. */
        public static final Pattern DAY = Pattern.compile("^(0?[1-9]|[12]\\d|3[01])$");
        /** Exactly 10 digits. Example: {@code 1234567890}. */
        public static final Pattern TEN_DIGITS = Pattern.compile("^\\d{10}$");
        /** 16-digit card number. Example: {@code 6037991234567890}. */
        public static final Pattern CREDIT_CARD_16 = Pattern.compile("^\\d{16}$");
        /** CVV (3-4 digits). Example: {@code 123}. */
        public static final Pattern CVV = Pattern.compile("^\\d{3,4}$");
        /** Large number with comma decimals. Example: {@code 1,234.56}. */
        public static final Pattern LARGE_NUMBER = Pattern.compile("^\\d{1,3}(,\\d{3})*(\\.\\d+)?$");
        /** International phone (10-15 digits, optional +). Example: {@code +989121234567}. */
        public static final Pattern INTERNATIONAL_PHONE = Pattern.compile("^\\+?\\d{10,15}$");
        /** Serial like XXX-XXXX. Example: {@code 123-4567}. */
        public static final Pattern SERIAL_NUMBER = Pattern.compile("^\\d{3}-\\d{4}$");
        /** US ZIP. Example: {@code 12345-6789}. */
        public static final Pattern US_ZIP = Pattern.compile("^\\d{5}(-\\d{4})?$");
        /** Signed percentage. Example: {@code -5%}. */
        public static final Pattern SIGNED_PERCENTAGE = Pattern.compile("^[+-]?\\d+(\\.\\d+)?%$");
        /** Passport number. Example: {@code A12345678}. */
        public static final Pattern PASSPORT_NUMBER = Pattern.compile("^[A-Z]\\d{8}$");
        /** Range like 5-10. Example: {@code 5-10}. */
        public static final Pattern RANGE_TWO_NUMBERS = Pattern.compile("^\\d+-\\d+$");
    }

    // ═══════════════════════════════════════════════════════════════════
    // 2. CHARACTERS (36-60)
    // ═══════════════════════════════════════════════════════════════════

    /** Character class patterns. */
    public static final class Characters {
        private Characters() { throw new AssertionError(); }

        /** English letters only. Example: {@code Hello}. */
        public static final Pattern LETTERS = Pattern.compile("^[a-zA-Z]+$");
        /** Lowercase only. Example: {@code hello}. */
        public static final Pattern LOWERCASE = Pattern.compile("^[a-z]+$");
        /** Uppercase only. Example: {@code HELLO}. */
        public static final Pattern UPPERCASE = Pattern.compile("^[A-Z]+$");
        /** Letters and digits. Example: {@code abc123}. */
        public static final Pattern ALPHANUMERIC = Pattern.compile("^[a-zA-Z0-9]+$");
        /** Word characters (ASCII). Example: {@code a_1}. */
        public static final Pattern WORD = Pattern.compile("^\\w+$");
        /** Non-word characters. Example: {@code !@#}. */
        public static final Pattern NON_WORD = Pattern.compile("^\\W+$");
        /** Whitespace only. Example: {@code \t\n}. */
        public static final Pattern WHITESPACE = Pattern.compile("^\\s+$");
        /** Non-whitespace. Example: {@code abc}. */
        public static final Pattern NON_WHITESPACE = Pattern.compile("^\\S+$");
        /** Printable ASCII. Example: {@code abc123}. */
        public static final Pattern ASCII = Pattern.compile("^[\\x00-\\x7F]+$");
        /** Control character. Example: {@code \n}. */
        public static final Pattern CONTROL_CHAR = Pattern.compile("[\\x00-\\x1F\\x7F]");
        /** Persian/Arabic letters. Example: {@code سلام}. */
        public static final Pattern PERSIAN_LETTERS = Pattern.compile("^[\\u0600-\\u06FF]+$");
        /** Arabic + extended block. Example: {@code سلام}. */
        public static final Pattern ARABIC_PERSIAN_LETTERS = Pattern.compile("^[\\u0600-\\u06FF\\u0750-\\u077F]+$");
        /** Emoji (surrogate pair). Example: {@code 😀}. */
        public static final Pattern EMOJI = Pattern.compile("[\\uD800-\\uDBFF][\\uDC00-\\uDFFF]");
        /** Word with 3+ chars. Example: {@code hello}. */
        public static final Pattern WORD_MIN_3 = Pattern.compile("\\b\\w{3,}\\b");
        /** Word starting with 'A'. Example: {@code Apple}. */
        public static final Pattern WORD_STARTING_A = Pattern.compile("\\bA\\w*\\b");
        /** Any char repeated twice. Example: {@code aa}. */
        public static final Pattern REPEATED_CHAR = Pattern.compile("(.)\\1");
        /** Char repeated 3 times. Example: {@code aaa}. */
        public static final Pattern CHAR_THREE_TIMES = Pattern.compile("(.)\\1{2}");
        /** Vowels only. Example: {@code aeiou}. */
        public static final Pattern VOWELS = Pattern.compile("^[aeiou]+$");
        /** Consonants only. Example: {@code bcdf}. */
        public static final Pattern CONSONANTS = Pattern.compile("^[bcdfghjklmnpqrstvwxyz]+$");
        /** Any letters. Example: {@code Test}. */
        public static final Pattern ONLY_LETTERS = Pattern.compile("^[a-zA-Z]+$");
        /** Persian letters + whitespace. Example: {@code سلام دنیا}. */
        public static final Pattern PERSIAN_WITH_SPACE = Pattern.compile("^[\\u0600-\\u06FF\\s]+$");
        /** HTML bold content (lazy). Example: {@code <b>bold</b>}. */
        public static final Pattern HTML_BOLD = Pattern.compile("<b>.*?</b>");
        /** Control characters. Example: {@code \x00}. */
        public static final Pattern CONTROL_CHARS = Pattern.compile("[\\x00-\\x1F]");
        /** Capitalized word. Example: {@code Hello}. */
        public static final Pattern WORD_CAPITALIZED = Pattern.compile("\\b[A-Z]\\w*\\b");
        /** Unicode letters (works for Persian). Example: {@code سلام}. */
        public static final Pattern UNICODE_LETTERS = Pattern.compile("^\\p{L}+$");
    }

    // ═══════════════════════════════════════════════════════════════════
    // 3. USERS (61-100)
    // ═══════════════════════════════════════════════════════════════════

    /** User account validation patterns. */
    public static final class Users {
        private Users() { throw new AssertionError(); }

        /** Simple email. Example: {@code a@b.com}. */
        public static final Pattern EMAIL_SIMPLE = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
        /** Practical email. Example: {@code ali@test.com}. */
        public static final Pattern EMAIL_PRACTICAL = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        /** Strict email (no leading/trailing dot, no consecutive dots). */
        public static final Pattern EMAIL_STRICT = Pattern.compile(
            "^[A-Za-z0-9](?:[A-Za-z0-9._-]{0,62}[A-Za-z0-9])?@" +
            "[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?" +
            "(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$");
        /** Username (3-16 word chars). Example: {@code ali_123}. */
        public static final Pattern USERNAME = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");
        /** Username not starting with a digit. Example: {@code ali123}. */
        public static final Pattern USERNAME_NOT_STARTING_DIGIT = Pattern.compile("^(?![0-9])\\w{3,16}$");
        /** Password min 8 chars. Example: {@code password}. */
        public static final Pattern PASSWORD_MIN_8 = Pattern.compile("^.{8,}$");
        /** Medium password: lower + upper + digit, min 8. Example: {@code Pass1234}. */
        public static final Pattern PASSWORD_MEDIUM = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
        /** Strong: lower+upper+digit+symbol, min 8. Example: {@code Pa$$1234}. */
        public static final Pattern PASSWORD_STRONG = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$");
        /** Strong with bounded length 8-20. Example: {@code Pa$$word1}. */
        public static final Pattern PASSWORD_STRONG_BOUNDED = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$");
        /** Iranian mobile. Example: {@code 09123456789}. */
        public static final Pattern IRAN_MOBILE = Pattern.compile("^09\\d{9}$");
        /** Iranian mobile in intl format. Example: {@code +989121234567}. */
        public static final Pattern IRAN_MOBILE_INTL = Pattern.compile("^\\+989\\d{9}$");
        /** Iranian landline. Example: {@code 021-12345678}. */
        public static final Pattern IRAN_LANDLINE = Pattern.compile("^0\\d{2,3}-?\\d{7,8}$");
        /** Iranian national code format (10 digits, no 000 prefix). */
        public static final Pattern NATIONAL_CODE_FORMAT = Pattern.compile("^(?!000)\\d{10}$");
        /** Iranian postal code (no leading 0, no 5 repeated digits). */
        public static final Pattern IRAN_POSTAL_CODE = Pattern.compile("^(?!0)(?!.*(\\d)\\1{4})\\d{10}$");
        /** IBAN/Sheba (IR + 24 digits). Example: {@code IR123...}. */
        public static final Pattern IRAN_SHEBA = Pattern.compile("^IR\\d{24}$");
        /** Simple URL. Example: {@code https://a.com}. */
        public static final Pattern URL_SIMPLE = Pattern.compile("^https?://[^\\s]+$");
        /** Detailed URL. Example: {@code https://a.com/x?y=1}. */
        public static final Pattern URL_DETAILED = Pattern.compile(
            "^https?://[\\w-]+(\\.[\\w-]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?$");
        /** Domain. Example: {@code sub.test.com}. */
        public static final Pattern DOMAIN = Pattern.compile("^([a-z0-9]+(-[a-z0-9]+)*\\.)+[a-z]{2,}$");
        /** Subdomain structure. Example: {@code a.b.c}. */
        public static final Pattern SUBDOMAIN = Pattern.compile(
            "^[a-z0-9]([a-z0-9-]{0,61}[a-z0-9])?(\\.[a-z0-9]([a-z0-9-]{0,61}[a-z0-9])?)*$");
        /** Canadian postal code. Example: {@code K1A 0B1}. */
        public static final Pattern CANADIAN_POSTAL = Pattern.compile("^[A-Za-z]\\d[A-Za-z] ?\\d[A-Za-z]\\d$");
        /** US SSN. Example: {@code 123-45-6789}. */
        public static final Pattern US_SSN = Pattern.compile("^\\d{3}-\\d{2}-\\d{4}$");
        /** English name. Example: {@code Ali Rezaei}. */
        public static final Pattern ENGLISH_NAME = Pattern.compile("^[A-Za-z][A-Za-z\\s'-]{1,49}$");
        /** Persian name with spaces. Example: {@code علی رضایی}. */
        public static final Pattern PERSIAN_NAME = Pattern.compile("^[\\u0600-\\u06FF\\s]{2,50}$");
        /** Birth date YYYY-MM-DD. Example: {@code 1990-05-20}. */
        public static final Pattern BIRTH_DATE = Pattern.compile(
            "^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$");
        /** OTP/code 4-6 digits. Example: {@code 1234}. */
        public static final Pattern OTP_4_6 = Pattern.compile("^\\d{4,6}$");
        /** ISBN-10. Example: {@code 0306406152}. */
        public static final Pattern ISBN_10 = Pattern.compile("^\\d{9}[\\dX]$");
        /** ISBN-13. Example: {@code 9780306406157}. */
        public static final Pattern ISBN_13 = Pattern.compile("^97[89]\\d{10}$");
        /** Visa card. Example: {@code 4111111111111111}. */
        public static final Pattern VISA = Pattern.compile("^4\\d{12}(\\d{3})?$");
        /** MasterCard. Example: {@code 5111111111111111}. */
        public static final Pattern MASTERCARD = Pattern.compile("^5[1-5]\\d{14}$");
        /** American Express. Example: {@code 378282246310005}. */
        public static final Pattern AMEX = Pattern.compile("^3[47]\\d{13}$");
        /** Currency code (ISO 4217). Example: {@code USD}. */
        public static final Pattern CURRENCY_CODE = Pattern.compile("^[A-Z]{3}$");
        /** Country code (ISO 3166-1). Example: {@code IR}. */
        public static final Pattern COUNTRY_CODE = Pattern.compile("^[A-Z]{2}$");
        /** Filename safe. Example: {@code my-file.txt}. */
        public static final Pattern FILENAME = Pattern.compile("^[\\w\\-. ]{1,255}$");
        /** Discount code. Example: {@code SAVE2024}. */
        public static final Pattern DISCOUNT_CODE = Pattern.compile("^[A-Z0-9]{6,12}$");
        /** Hashtag. Example: {@code #java}. */
        public static final Pattern HASHTAG = Pattern.compile("^#[A-Za-z0-9_]+$");
        /** Mention. Example: {@code @user}. */
        public static final Pattern MENTION = Pattern.compile("^@[A-Za-z0-9_]+$");
        /** URL slug. Example: {@code my-post-title}. */
        public static final Pattern SLUG = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");
        /** Persian capital city. Example: {@code تهران}. */
        public static final Pattern PERSIAN_CAPITAL = Pattern.compile("^تهران$");
        /** 6-digit OTP. Example: {@code 123456}. */
        public static final Pattern OTP_6 = Pattern.compile("^\\d{6}$");
        /** 4-digit PIN. Example: {@code 1234}. */
        public static final Pattern PIN_4 = Pattern.compile("^\\d{4}$");
    }

    // ═══════════════════════════════════════════════════════════════════
    // 4. DATE / TIME (101-120)
    // ═══════════════════════════════════════════════════════════════════

    /** Date and time patterns. */
    public static final class DateTime {
        private DateTime() { throw new AssertionError(); }

        /** Simple date. Example: {@code 2024-03-15}. */
        public static final Pattern DATE_ISO_SIMPLE = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
        /** Validated date. Example: {@code 2024-03-15}. */
        public static final Pattern DATE_VALID = Pattern.compile(
            "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$");
        /** Slash format. Example: {@code 2024/03/15}. */
        public static final Pattern DATE_SLASH = Pattern.compile("^\\d{4}/\\d{2}/\\d{2}$");
        /** DD/MM/YYYY. Example: {@code 15/03/2024}. */
        public static final Pattern DATE_DMY = Pattern.compile(
            "^(0[1-9]|[12]\\d|3[01])/(0[1-9]|1[0-2])/\\d{4}$");
        /** MM/DD/YYYY. Example: {@code 03/15/2024}. */
        public static final Pattern DATE_MDY = Pattern.compile(
            "^(0[1-9]|1[0-2])/(0[1-9]|[12]\\d|3[01])/\\d{4}$");
        /** Jalali date. Example: {@code 1403/05/22}. */
        public static final Pattern DATE_JALALI = Pattern.compile(
            "^1[34]\\d{2}[-/](0[1-9]|1[0-2])[-/](0[1-9]|[12]\\d|3[01])$");
        /** HH:MM. Example: {@code 23:59}. */
        public static final Pattern TIME_HH_MM = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d$");
        /** HH:MM:SS. Example: {@code 23:59:59}. */
        public static final Pattern TIME_HH_MM_SS = Pattern.compile(
            "^([01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d$");
        /** 12-hour time. Example: {@code 3:30 PM}. */
        public static final Pattern TIME_12H = Pattern.compile("^(0?[1-9]|1[0-2]):[0-5]\\d\\s?[AP]M$");
        /** ISO-8601 datetime. Example: {@code 2024-03-15T10:30:00Z}. */
        public static final Pattern ISO_8601 = Pattern.compile(
            "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(Z|[+-]\\d{2}:\\d{2})?$");
        /** Date + time. Example: {@code 2024-03-15 10:30:00}. */
        public static final Pattern DATETIME = Pattern.compile(
            "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$");
        /** Year only. Example: {@code 2024}. */
        public static final Pattern YEAR_4 = Pattern.compile("^(19|20|21)\\d{2}$");
        /** Month number. Example: {@code 12}. */
        public static final Pattern MONTH_NUM = Pattern.compile("^(0?[1-9]|1[0-2])$");
        /** Day number. Example: {@code 31}. */
        public static final Pattern DAY_NUM = Pattern.compile("^(0?[1-9]|[12]\\d|3[01])$");
        /** Time range. Example: {@code 09:00-17:00}. */
        public static final Pattern TIME_RANGE = Pattern.compile(
            "^([01]\\d|2[0-3]):[0-5]\\d\\s*-\\s*([01]\\d|2[0-3]):[0-5]\\d$");
        /** Relative date. Example: {@code 3 روز پیش}. */
        public static final Pattern RELATIVE_DATE = Pattern.compile("^\\d+\\s+(روز|هفته|ماه|سال)\\s+پیش$");
        /** Month name + date. Example: {@code March 15, 2024}. */
        public static final Pattern MONTH_NAME_DATE = Pattern.compile(
            "^(January|February|March|April|May|June|July|August|September|October|November|December)" +
            "\\s+\\d{1,2},?\\s+\\d{4}$");
        /** Duration. Example: {@code 1h 30m}. */
        public static final Pattern DURATION = Pattern.compile("^(\\d+h)?\\s*(\\d+m)?\\s*(\\d+s)?$");
        /** Millisecond timestamp (13 digits). Example: {@code 1700000000000}. */
        public static final Pattern TIMESTAMP_13 = Pattern.compile("^\\d{13}$");
        /** Second timestamp (10 digits). Example: {@code 1700000000}. */
        public static final Pattern TIMESTAMP_10 = Pattern.compile("^\\d{10}$");
    }

    // ═══════════════════════════════════════════════════════════════════
    // 5. IRANIAN (121-145)
    // ═══════════════════════════════════════════════════════════════════

    /** Iran-specific validation patterns. */
    public static final class Iranian {
        private Iranian() { throw new AssertionError(); }

        /** 10-digit national code (format only). Example: {@code 0499370899}. */
        public static final Pattern NATIONAL_CODE = Pattern.compile("^(?!000)\\d{10}$");
        /** Mobile simple. Example: {@code 09123456789}. */
        public static final Pattern MOBILE = Pattern.compile("^09\\d{9}$");
        /** Mobile with optional country code. Example: {@code +989121234567}. */
        public static final Pattern MOBILE_FULL = Pattern.compile("^(\\+98|0098|98|0)?9\\d{9}$");
        /** Mobile with optional spaces/dashes. Example: {@code 0912-345-6789}. */
        public static final Pattern MOBILE_SPACED = Pattern.compile(
            "^(\\+98|0098|98|0)?[\\s-]?9\\d{2}[\\s-]?\\d{3}[\\s-]?\\d{4}$");
        /** Tehran landline. Example: {@code 02112345678}. */
        public static final Pattern TEHRAN_PHONE = Pattern.compile("^021\\d{8}$");
        /** Tehran landline with dash. Example: {@code 021-12345678}. */
        public static final Pattern TEHRAN_PHONE_DASH = Pattern.compile("^021-?\\d{8}$");
        /** All-Iran landline. Example: {@code 031-12345678}. */
        public static final Pattern LANDLINE = Pattern.compile("^0\\d{2,3}-?\\d{7,8}$");
        /** Postal code (10 digits). Example: {@code 1234567890}. */
        public static final Pattern POSTAL_10 = Pattern.compile("^\\d{10}$");
        /** Postal (no leading 0). Example: {@code 1234567890}. */
        public static final Pattern POSTAL_NO_LEADING_ZERO = Pattern.compile("^(?!0)\\d{10}$");
        /** Postal strict (no 5 repeated digits). Example: {@code 1234567890}. */
        public static final Pattern POSTAL_STRICT = Pattern.compile("^(?!0)(?!.*(\\d)\\1{4})\\d{10}$");
        /** Sheba. Example: {@code IR123456789012345678901234}. */
        public static final Pattern SHEBA = Pattern.compile("^IR\\d{24}$");
        /** Sheba with spaces. */
        public static final Pattern SHEBA_SPACED = Pattern.compile("^IR[\\d\\s]{24,30}$");
        /** 16-digit card. Example: {@code 6037991234567890}. */
        public static final Pattern CARD_16 = Pattern.compile("^\\d{16}$");
        /** Card with spaces. Example: {@code 6037 9912 3456 7890}. */
        public static final Pattern CARD_SPACED = Pattern.compile("^\\d{4}\\s?\\d{4}\\s?\\d{4}\\s?\\d{4}$");
        /** CVV2. Example: {@code 123}. */
        public static final Pattern CVV2 = Pattern.compile("^\\d{3,4}$");
        /** Card expiry MM/YY. Example: {@code 12/25}. */
        public static final Pattern CARD_EXPIRY = Pattern.compile("^(0[1-9]|1[0-2])/\\d{2}$");
        /** Economic code (14 digits). Example: {@code 12345678901234}. */
        public static final Pattern ECONOMIC_CODE = Pattern.compile("^\\d{14}$");
        /** National ID (11 digits). Example: {@code 12345678901}. */
        public static final Pattern NATIONAL_ID = Pattern.compile("^\\d{11}$");
        /** National code or ID. Example: {@code 1234567890}. */
        public static final Pattern NATIONAL_CODE_OR_ID = Pattern.compile("^\\d{10,11}$");
        /** Car plate with Persian letters. Example: {@code 12 ب 345 ی 67}. */
        public static final Pattern CAR_PLATE_IR = Pattern.compile(
            "^\\d{2}\\s?[ب-ی]\\s?\\d{3}\\s?[ب-ی]{2}\\s?\\d{2}$");
        /** Car plate (no spaces). Example: {@code 12ب345یی67}. */
        public static final Pattern CAR_PLATE_SIMPLE = Pattern.compile("^\\d{2}[ب-ی]\\d{3}[ب-ی]{2}\\d{2}$");
        /** Stock trading code. Example: {@code ABCD123456}. */
        public static final Pattern STOCK_CODE = Pattern.compile("^[A-Z0-9]{8,12}$");
        /** Insurance number. Example: {@code 1234567890}. */
        public static final Pattern INSURANCE_NUMBER = Pattern.compile("^\\d{10}$");
    }

    // ═══════════════════════════════════════════════════════════════════
    // 6. FILES & URLS (146-170)
    // ═══════════════════════════════════════════════════════════════════

    /** File path, URL, and network patterns. */
    public static final class Files {
        private Files() { throw new AssertionError(); }

        /** Unix simple path. Example: {@code /home/user}. */
        public static final Pattern UNIX_PATH_SIMPLE = Pattern.compile("^(/[^/]+)+$");
        /** Unix full path. Example: {@code /home/user/file.txt}. */
        public static final Pattern UNIX_PATH = Pattern.compile("^/([\\w-]+/)*[\\w-]*$");
        /** Windows path. Example: {@code C:\Users\a.txt}. */
        public static final Pattern WINDOWS_PATH = Pattern.compile(
            "^[A-Za-z]:\\\\(?:[^\\\\/:*?\"<>|\\r\\n]+\\\\)*[^\\\\/:*?\"<>|\\r\\n]*$");
        /** Filename without separators. Example: {@code myfile.txt}. */
        public static final Pattern FILENAME_SAFE = Pattern.compile("^[^/\\\\:*?\"<>|]+$");
        /** Limited filename. Example: {@code my-file.txt}. */
        public static final Pattern FILENAME_LIMITED = Pattern.compile("^[\\w\\-. ]{1,255}$");
        /** .txt extension. Example: {@code a.txt}. */
        public static final Pattern EXT_TXT = Pattern.compile("\\.txt$");
        /** Image extension. Example: {@code a.jpg}. */
        public static final Pattern EXT_IMAGE = Pattern.compile("\\.(jpg|jpeg|png|gif|bmp|webp)$");
        /** Video extension. Example: {@code a.mp4}. */
        public static final Pattern EXT_VIDEO = Pattern.compile("\\.(mp4|avi|mkv|mov|wmv)$");
        /** Audio extension. Example: {@code a.mp3}. */
        public static final Pattern EXT_AUDIO = Pattern.compile("\\.(mp3|wav|flac|aac|ogg)$");
        /** Document extension. Example: {@code a.pdf}. */
        public static final Pattern EXT_DOCUMENT = Pattern.compile(
            "\\.(pdf|doc|docx|xls|xlsx|ppt|pptx)$");
        /** Archive extension. Example: {@code a.zip}. */
        public static final Pattern EXT_ARCHIVE = Pattern.compile("\\.(zip|rar|7z|tar|gz)$");
        /** Any extension. Example: {@code a.pdf}. */
        public static final Pattern ANY_EXTENSION = Pattern.compile("^.+\\.[a-zA-Z0-9]+$");
        /** HTTP(S) URL. Example: {@code https://a.com}. */
        public static final Pattern URL_HTTP = Pattern.compile("^https?://.+$");
        /** URL with port. Example: {@code http://a.com:8080/x}. */
        public static final Pattern URL_WITH_PORT = Pattern.compile("^https?://[^:/]+(:\\d+)?(/.*)?$");
        /** FTP URL. Example: {@code ftp://a.com}. */
        public static final Pattern URL_FTP = Pattern.compile("^ftp://.+$");
        /** URL with query. Example: {@code http://a.com?x=1}. */
        public static final Pattern URL_WITH_QUERY = Pattern.compile(
            "^https?://[^?]+\\?([^=]+=[^&]*&?)+$");
        /** URL hash fragment. Example: {@code http://a.com#sec}. */
        public static final Pattern URL_HASH = Pattern.compile("#[^#]*$");
        /** mailto link. Example: {@code mailto:a@b.com}. */
        public static final Pattern URL_MAILTO = Pattern.compile("^mailto:.+@.+$");
        /** SSH URL. Example: {@code ssh://a.com}. */
        public static final Pattern URL_SSH = Pattern.compile("^ssh://.+$");
        /** .com domain. Example: {@code google.com}. */
        public static final Pattern DOMAIN_COM = Pattern.compile("\\.com$");
        /** .ir domain. Example: {@code test.ir}. */
        public static final Pattern DOMAIN_IR = Pattern.compile("\\.ir$");
        /** IP:Port. Example: {@code 192.168.1.1:80}. */
        public static final Pattern IP_PORT = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}:\\d{1,5}$");
        /** Simple IPv6. Example: {@code 2001:db8::1}. */
        public static final Pattern IPV6_SIMPLE = Pattern.compile(
            "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");
        /** MAC address. Example: {@code 00:11:22:33:44:55}. */
        public static final Pattern MAC_ADDRESS = Pattern.compile(
            "^([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2}$");
    }

    // ═══════════════════════════════════════════════════════════════════
    // 7. MARKUP (HTML / XML / JSON) (171-190)
    // ═══════════════════════════════════════════════════════════════════

    /** HTML / XML / JSON patterns (basic; use a parser for anything serious). */
    public static final class Markup {
        private Markup() { throw new AssertionError(); }

        /** Any HTML tag. Example: {@code <div>}. */
        public static final Pattern HTML_TAG = Pattern.compile("<[^>]+>");
        /** Open tag. Example: {@code <div>}. */
        public static final Pattern HTML_OPEN_TAG = Pattern.compile("<\\w+>");
        /** Close tag. Example: {@code </div>}. */
        public static final Pattern HTML_CLOSE_TAG = Pattern.compile("</\\w+>");
        /** Tag with attributes. Example: {@code <a href="x">}. */
        public static final Pattern HTML_TAG_WITH_ATTRS = Pattern.compile(
            "<\\w+(\\s+\\w+=\"[^\"]*\")*\\s*/?>");
        /** Strip-all-tags fragment. Example: {@code </p>}. */
        public static final Pattern HTML_STRIP_TAGS = Pattern.compile("<[^>]*>");
        /** href value. Example: {@code href="x.com"}. */
        public static final Pattern HTML_HREF = Pattern.compile("href=\"([^\"]*)\"");
        /** Anchor text. Example: {@code <a>click</a>}. */
        public static final Pattern HTML_LINK_TEXT = Pattern.compile("<a[^>]*>(.*?)</a>");
        /** Image src. Example: {@code <img src="a.jpg">}. */
        public static final Pattern HTML_IMG_SRC = Pattern.compile("<img[^>]*src=\"([^\"]*)\"[^>]*>");
        /** script block. */
        public static final Pattern HTML_SCRIPT = Pattern.compile("<script[^>]*>[\\s\\S]*?</script>");
        /** style block. */
        public static final Pattern HTML_STYLE = Pattern.compile("<style[^>]*>[\\s\\S]*?</style>");
        /** HTML comment. Example: {@code <!-- x -->}. */
        public static final Pattern HTML_COMMENT = Pattern.compile("<!--[\\s\\S]*?-->");
        /** XML declaration. */
        public static final Pattern XML_DECLARATION = Pattern.compile("<\\?xml[^>]*\\?>");
        /** CDATA block. */
        public static final Pattern XML_CDATA = Pattern.compile("<!\\[CDATA\\[[\\s\\S]*?\\]\\]>");
        /** JSON string. */
        public static final Pattern JSON_STRING = Pattern.compile("\"(?:[^\"\\\\]|\\\\.)*\"");
        /** JSON number. */
        public static final Pattern JSON_NUMBER = Pattern.compile("-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?");
        /** JSON key. Example: {@code "name":}. */
        public static final Pattern JSON_KEY = Pattern.compile("\"(?:[^\"\\\\]|\\\\.)*\"\\s*:");
        /** JSON boolean. */
        public static final Pattern JSON_BOOLEAN = Pattern.compile("\\b(true|false)\\b");
        /** JSON null. */
        public static final Pattern JSON_NULL = Pattern.compile("\\bnull\\b");
        /** JSON flat array. Example: {@code [1,2,3]}. */
        public static final Pattern JSON_ARRAY = Pattern.compile("\\[[^\\[\\]]*\\]");
        /** JSON flat object. Example: {@code {"a":1}}. */
        public static final Pattern JSON_OBJECT = Pattern.compile("\\{[^{}]*\\}");
    }

    // ═══════════════════════════════════════════════════════════════════
    // 8. LOGS (191-210)
    // ═══════════════════════════════════════════════════════════════════

    /** Log parsing patterns. */
    public static final class Logs {
        private Logs() { throw new AssertionError(); }

        /** IP at start of log line. */
        public static final Pattern LOG_IP_PREFIX = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}");
        /** Log timestamp. Example: {@code [2024-03-15 10:30:00]}. */
        public static final Pattern LOG_TIMESTAMP = Pattern.compile(
            "\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\]");
        /** Log level. */
        public static final Pattern LOG_LEVEL = Pattern.compile("\\b(DEBUG|INFO|WARN|ERROR|FATAL)\\b");
        /** HTTP method. */
        public static final Pattern HTTP_METHOD = Pattern.compile("\\b(GET|POST|PUT|DELETE|PATCH)\\b");
        /** HTTP status. Example: {@code 404}. */
        public static final Pattern HTTP_STATUS = Pattern.compile("\\b([1-5]\\d{2})\\b");
        /** Quoted user agent. */
        public static final Pattern USER_AGENT = Pattern.compile("\"([^\"]*)\"\\s*$");
        /** URL in log. */
        public static final Pattern LOG_URL = Pattern.compile("(GET|POST)\\s+(\\S+)");
        /** JSON embedded in log. */
        public static final Pattern LOG_JSON = Pattern.compile("\\{.*\\}");
        /** Exception class name. */
        public static final Pattern EXCEPTION_NAME = Pattern.compile("\\b\\w+Exception\\b");
        /** Stack trace line. */
        public static final Pattern STACK_TRACE = Pattern.compile("\\bat [\\w.$]+\\([\\w.]+:\\d+\\)");
        /** CVE identifier. Example: {@code CVE-2024-12345}. */
        public static final Pattern CVE = Pattern.compile("CVE-\\d{4}-\\d{4,}");
        /** UUID. Example: {@code 550e8400-e29b-41d4-a716-446655440000}. */
        public static final Pattern UUID = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
        /** MD5 hash. */
        public static final Pattern MD5_HASH = Pattern.compile("^[a-f0-9]{32}$");
        /** SHA1 hash. */
        public static final Pattern SHA1_HASH = Pattern.compile("^[a-f0-9]{40}$");
        /** SHA256 hash. */
        public static final Pattern SHA256_HASH = Pattern.compile("^[a-f0-9]{64}$");
        /** SHA512 hash. */
        public static final Pattern SHA512_HASH = Pattern.compile("^[a-f0-9]{128}$");
        /** Base64. Example: {@code SGVsbG8=}. */
        public static final Pattern BASE64 = Pattern.compile("^[A-Za-z0-9+/]*={0,2}$");
        /** Hex string. Example: {@code deadbeef}. */
        public static final Pattern HEX_STRING = Pattern.compile("^[0-9a-fA-F]+$");
        /** Blank line (whitespace only). */
        public static final Pattern EMPTY_LINE = Pattern.compile("^\\s*$");
        /** Simple sentence ending with . ! ?. */
        public static final Pattern SENTENCE = Pattern.compile("^[A-Z][^.!?]*[.!?]$");
    }

    // ═══════════════════════════════════════════════════════════════════
    // 9. CODE (211-235)
    // ═══════════════════════════════════════════════════════════════════

    /** Source code tokenizing patterns. */
    public static final class Code {
        private Code() { throw new AssertionError(); }

        /** Java identifier. Example: {@code myVar_1}. */
        public static final Pattern JAVA_IDENTIFIER = Pattern.compile("^[a-zA-Z_$][a-zA-Z0-9_$]*$");
        /** Class name (UpperCamelCase). Example: {@code MyClass}. */
        public static final Pattern CLASS_NAME = Pattern.compile("^[A-Z][a-zA-Z0-9]*$");
        /** Method name (lowerCamelCase). Example: {@code getUser}. */
        public static final Pattern METHOD_NAME = Pattern.compile("^[a-z][a-zA-Z0-9]*$");
        /** Constant name (UPPER_SNAKE). Example: {@code MAX_SIZE}. */
        public static final Pattern CONSTANT_NAME = Pattern.compile("^[A-Z][A-Z0-9_]*$");
        /** import statement. */
        public static final Pattern JAVA_IMPORT = Pattern.compile("^import\\s+[\\w.]+;?$");
        /** package statement. */
        public static final Pattern JAVA_PACKAGE = Pattern.compile("^package\\s+[\\w.]+;?$");
        /** Class declaration. */
        public static final Pattern CLASS_DEFINITION = Pattern.compile("\\bclass\\s+(\\w+)");
        /** Method declaration. */
        public static final Pattern METHOD_DEFINITION = Pattern.compile(
            "\\b(public|private|protected)?\\s*(static\\s+)?\\w+\\s+(\\w+)\\s*\\(");
        /** Line comment. */
        public static final Pattern LINE_COMMENT = Pattern.compile("//.*$");
        /** Block comment. */
        public static final Pattern BLOCK_COMMENT = Pattern.compile("/\\*[\\s\\S]*?\\*/");
        /** Single-quoted string. */
        public static final Pattern SINGLE_QUOTE_STRING = Pattern.compile("'([^'\\\\]|\\\\.)*'");
        /** Double-quoted string. */
        public static final Pattern DOUBLE_QUOTE_STRING = Pattern.compile("\"([^\"\\\\]|\\\\.)*\"");
        /** Numeric literal in code. */
        public static final Pattern NUMBER_IN_CODE = Pattern.compile("\\b\\d+(\\.\\d+)?([eE][+-]?\\d+)?\\b");
        /** Java annotation. */
        public static final Pattern ANNOTATION = Pattern.compile("@\\w+(\\([^)]*\\))?");
        /** Generic type. */
        public static final Pattern GENERIC_TYPE = Pattern.compile("<[A-Z]\\w*(,\\s*[A-Z]\\w*)*>");
        /** Lambda. */
        public static final Pattern LAMBDA = Pattern.compile("\\([^)]*\\)\\s*->\\s*.+");
        /** Ternary expression. */
        public static final Pattern TERNARY = Pattern.compile("\\?[^:]+:");
        /** SQL SELECT. */
        public static final Pattern SQL_SELECT = Pattern.compile("(?i)^\\s*SELECT\\s+.+\\s+FROM\\s+");
        /** SQL INSERT. */
        public static final Pattern SQL_INSERT = Pattern.compile("(?i)^\\s*INSERT\\s+INTO\\s+");
        /** SQL UPDATE. */
        public static final Pattern SQL_UPDATE = Pattern.compile("(?i)^\\s*UPDATE\\s+\\w+\\s+SET\\s+");
        /** SQL DELETE. */
        public static final Pattern SQL_DELETE = Pattern.compile("(?i)^\\s*DELETE\\s+FROM\\s+");
        /** SQL table name after FROM/JOIN. */
        public static final Pattern SQL_TABLE_NAME = Pattern.compile("(?:FROM|JOIN)\\s+(\\w+)");
        /** XML/HTML tag name. */
        public static final Pattern XML_TAG_NAME = Pattern.compile("</?[a-zA-Z_][\\w.-]*");
        /** CSS class selector. */
        public static final Pattern CSS_CLASS = Pattern.compile("\\.[\\w-]+\\s*\\{");
        /** CSS id selector. */
        public static final Pattern CSS_ID = Pattern.compile("#[\\w-]+\\s*\\{");
    }

    // ═══════════════════════════════════════════════════════════════════
    // 10. LOOKAROUND / BACKREFERENCE (236-265)
    // ═══════════════════════════════════════════════════════════════════

    /** Zero-width assertion and backreference patterns. */
    public static final class Lookaround {
        private Lookaround() { throw new AssertionError(); }

        /** Has at least one digit. */
        public static final Pattern HAS_DIGIT = Pattern.compile("^(?=.*\\d).+$");
        /** Has at least one uppercase letter. */
        public static final Pattern HAS_UPPERCASE = Pattern.compile("^(?=.*[A-Z]).+$");
        /** Has at least one symbol. */
        public static final Pattern HAS_SYMBOL = Pattern.compile("^(?=.*[!@#$%^&*]).+$");
        /** No whitespace anywhere. */
        public static final Pattern NO_WHITESPACE = Pattern.compile("^(?!.*\\s).+$");
        /** No char repeated 3+ times. */
        public static final Pattern NO_TRIPLE_CHAR = Pattern.compile("^(?!.*(.)\\1{2}).+$");
        /** Not prefixed with "admin". */
        public static final Pattern NOT_ADMIN = Pattern.compile("^(?!admin).+$");
        /** Not a reserved word. */
        public static final Pattern NOT_RESERVED_WORD = Pattern.compile(
            "^(?!(admin|root|test|guest)$).+$");
        /** Not ending with .exe. */
        public static final Pattern NOT_EXE = Pattern.compile("^(?!.*\\.exe$).+$");
        /** Digits after {@code $}. Example: in {@code $100} → {@code 100}. */
        public static final Pattern AFTER_DOLLAR = Pattern.compile("(?<=\\$)\\d+");
        /** Digits after pound sign. */
        public static final Pattern AFTER_POUND = Pattern.compile("(?<=\u00A3)\\d+");
        /** Word after {@code @}. */
        public static final Pattern AFTER_AT = Pattern.compile("(?<=@)\\w+");
        /** Word after {@code #}. */
        public static final Pattern AFTER_HASH = Pattern.compile("(?<=#)\\w+");
        /** Char before dot. */
        public static final Pattern BEFORE_DOT = Pattern.compile(".(?=\\.)");
        /** Word before colon. */
        public static final Pattern BEFORE_COLON = Pattern.compile("\\w+(?=:)");
        /** Char repeated 3 times. */
        public static final Pattern CHAR_3_REPEATED = Pattern.compile("(.)\\1{2}");
        /** Char repeated 4 times. */
        public static final Pattern CHAR_4_REPEATED = Pattern.compile("(.)\\1{3}");
        /** Duplicate word. Example: {@code hello hello}. */
        public static final Pattern DUPLICATE_WORD = Pattern.compile("\\b(\\w+)\\s+\\1\\b");
        /** 3-char palindrome. */
        public static final Pattern PALINDROME_3 = Pattern.compile("^(\\w)\\w?\\1$");
        /** 5-char palindrome. */
        public static final Pattern PALINDROME_5 = Pattern.compile("^(\\w)(\\w)\\w?\\2\\1$");
        /** Word with a repeated letter. */
        public static final Pattern REPEATED_LETTER_IN_WORD = Pattern.compile("\\b\\w*(\\w)\\1\\w*\\b");
        /** Last char before end. */
        public static final Pattern LAST_CHAR_BEFORE_END = Pattern.compile("\\w(?=$)");
        /** No 3 repeated digits. */
        public static final Pattern NO_TRIPLE_DIGIT = Pattern.compile("^(?!.*(\\d)\\1{2}).+$");
        /** 5-digit zip not starting with 0000. */
        public static final Pattern ZIP_NO_LEADING_0000 = Pattern.compile("^(?!0000)\\d{5}$");
        /** Not starting with www. */
        public static final Pattern NOT_WWW = Pattern.compile("^(?!www\\.).+$");
        /** Not Feb 30/31. */
        public static final Pattern NO_INVALID_FEB = Pattern.compile("^(?!.*02-30)(?!.*02-31).+$");
        /** Positive integer without leading zero. */
        public static final Pattern NO_LEADING_ZERO = Pattern.compile("^(?!0)\\d+$");
        /** Any number not starting with zero. */
        public static final Pattern NUMBER_NO_LEADING_ZERO = Pattern.compile("^(?!0)\\d+$");
    }

    // ═══════════════════════════════════════════════════════════════════
    // 11. SECURITY (266-290)
    // ═══════════════════════════════════════════════════════════════════

    /** Security and token patterns. */
    public static final class Security {
        private Security() { throw new AssertionError(); }

        /** Captcha 4-digit. */
        public static final Pattern CAPTCHA_4 = Pattern.compile("^\\d{4}$");
        /** Password has uppercase. */
        public static final Pattern PASSWORD_HAS_UPPER = Pattern.compile("^(?=.*[A-Z]).+$");
        /** No common sequence. */
        public static final Pattern NO_SEQUENCE = Pattern.compile("^(?!.*(?:123|abc|qwe)).+$");
        /** No 4-digit year. */
        public static final Pattern NO_YEAR = Pattern.compile("^(?!.*\\d{4}).+$");
        /** Strong password 12+ with symbol. */
        public static final Pattern PASSWORD_STRONG_12 = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w]).{12,}$");
        /** JWT token. */
        public static final Pattern JWT = Pattern.compile(
            "^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$");
        /** 32-char API key. */
        public static final Pattern API_KEY_32 = Pattern.compile("^[A-Za-z0-9]{32}$");
        /** Bearer token. */
        public static final Pattern BEARER_TOKEN = Pattern.compile(
            "^Bearer\\s+[\\w-]+\\.?[\\w-]*\\.?[\\w-]*$");
        /** Basic auth header. */
        public static final Pattern BASIC_AUTH = Pattern.compile("^Basic\\s+[A-Za-z0-9+/=]+$");
        /** CSRF token. */
        public static final Pattern CSRF_TOKEN = Pattern.compile("^[a-f0-9]{32,64}$");
        /** Hex color. Example: {@code #FF0000}. */
        public static final Pattern HEX_COLOR = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
        /** RGB color. */
        public static final Pattern RGB_COLOR = Pattern.compile(
            "^rgb\\(\\s*\\d{1,3}\\s*,\\s*\\d{1,3}\\s*,\\s*\\d{1,3}\\s*\\)$");
        /** RGBA color. */
        public static final Pattern RGBA_COLOR = Pattern.compile(
            "^rgba\\(\\s*\\d{1,3}\\s*,\\s*\\d{1,3}\\s*,\\s*\\d{1,3}\\s*,\\s*(0|1|0?\\.\\d+)\\s*\\)$");
        /** HSL color. */
        public static final Pattern HSL_COLOR = Pattern.compile(
            "^hsl\\(\\s*\\d{1,3}\\s*,\\s*\\d{1,3}%\\s*,\\s*\\d{1,3}%\\s*\\)$");
        /** URL slug (duplicate location for grouping). */
        public static final Pattern SLUG = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");
        /** UUID v4. */
        public static final Pattern UUID_V4 = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");
        /** MD5 hash. */
        public static final Pattern MD5 = Pattern.compile("^[a-f0-9]{32}$");
        /** SHA-256 hash. */
        public static final Pattern SHA256 = Pattern.compile("^[a-f0-9]{64}$");
        /** Strict Base64. */
        public static final Pattern BASE64_STRICT = Pattern.compile(
            "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$");
        /** WhatsApp number. */
        public static final Pattern WHATSAPP = Pattern.compile("^\\+?\\d{10,15}$");
        /** Telegram username. */
        public static final Pattern TELEGRAM_USERNAME = Pattern.compile("^@?[A-Za-z0-9_]{5,32}$");
        /** Bitcoin address (basic). */
        public static final Pattern BITCOIN_ADDRESS = Pattern.compile(
            "^[13][a-km-zA-HJ-NP-Z1-9]{25,34}$");
    }

    // ═══════════════════════════════════════════════════════════════════
    // 12. DAILY (291-300)
    // ═══════════════════════════════════════════════════════════════════

    /** Common everyday patterns. */
    public static final class Daily {
        private Daily() { throw new AssertionError(); }

        /** Hashtag in text. */
        public static final Pattern HASHTAG_IN_TEXT = Pattern.compile("#[A-Za-z0-9_]+");
        /** Mention in text. */
        public static final Pattern MENTION_IN_TEXT = Pattern.compile("@[A-Za-z0-9_]+");
        /** Single emoji. */
        public static final Pattern EMOJI_CHAR = Pattern.compile(
            "[\\uD800-\\uDBFF][\\uDC00-\\uDFFF]");
        /** Simple time HH:MM. */
        public static final Pattern TIME_SIMPLE = Pattern.compile("\\d{1,2}:\\d{2}");
        /** Dollar price. Example: {@code $99.99}. */
        public static final Pattern DOLLAR_PRICE = Pattern.compile("\\$\\d+(\\.\\d{2})?");
        /** Percent value. */
        public static final Pattern PERCENT_VALUE = Pattern.compile("\\d+(\\.\\d+)?%");
        /** Country code prefix. Example: {@code +98}. */
        public static final Pattern COUNTRY_CODE_PREFIX = Pattern.compile("\\+\\d{1,3}");
        /** Sentence ending with dot. */
        public static final Pattern SENTENCE_ENDING_DOT = Pattern.compile("^[^.]*\\.$");
        /** Question. */
        public static final Pattern QUESTION = Pattern.compile("^.+\\?$");
        /** Exclamation. */
        public static final Pattern EXCLAMATION = Pattern.compile("^.+!$");
    }
}
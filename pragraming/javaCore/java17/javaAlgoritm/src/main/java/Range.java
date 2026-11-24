public record Range(int start, int end) {
    public Range(int start, int end) {
        this.start = start;
        this.end = end;
        if (start > end) {
            throw new IllegalArgumentException("start > end");
        }
    }

}

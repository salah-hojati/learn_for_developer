public record Range(int start,int end) {

    public Range(){
        if(start>end){
            throw new IllegalArgumentException("start > end");
        }
    }
}

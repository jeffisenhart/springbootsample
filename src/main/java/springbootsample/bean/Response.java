package springbootsample.bean;

public class Response<M> {
	
	private Meta meta;
	private int nextOffset;
    private long total;
    private M result;
	public Response() {
		setMeta(new Meta());
		getMeta().setCode(200);
	}
	public Meta getMeta() {
		return meta;
	}
	public void setMeta(Meta meta) {
		this.meta = meta;
	}
	public int getNextOffset() {
		return nextOffset;
	}
	public void setNextOffset(int nextOffset) {
		this.nextOffset = nextOffset;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public M getResult() {
		return result;
	}
	public void setResult(M result) {
		this.result = result;
	}
	
	
}
package at.ac.univie.clustering.method.bang;

public class DirectoryEntry {
	
	private DirectoryEntry left = null;
	private DirectoryEntry right = null;
	private DirectoryEntry back = null;
	private TupleRegion region = null;

	public DirectoryEntry getLeft() {
		return left;
	}

	public void setLeft(DirectoryEntry left) {
		this.left = left;
	}

	public DirectoryEntry getRight() {
		return right;
	}

	public void setRight(DirectoryEntry right) {
		this.right = right;
	}

	public DirectoryEntry getBack() {
		return back;
	}

	public void setBack(DirectoryEntry back) {
		this.back = back;
	}

	public TupleRegion getRegion() {
		return region;
	}

	public void setRegion(TupleRegion region) {
		this.region = region;
	}

}

package cn.keepfight.tc.libsvm;

public class svm_problem implements java.io.Serializable
{
	public int l;
	public double[] y;
	public cn.keepfight.tc.libsvm.svm_node[][] x;
}

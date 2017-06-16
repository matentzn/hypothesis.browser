package owl.cs.hypothesis.browser;

public class KnowledgeComponentViewFilter {
	
	int minAssumption=0;
	double minLift=0;
	int minSupport=0;
	double minPrecision=0;
	int maxUsage = 0;
	int maxHypotheses = 0;
	
	public int getMinAssumption() {
		return minAssumption;
	}
	public void setMinAssumption(int minAssumption) {
		this.minAssumption = minAssumption;
	}
	public double getMinLift() {
		return minLift;
	}
	public void setMinLift(double minLift) {
		this.minLift = minLift;
	}
	public int getMinSupport() {
		return minSupport;
	}
	public void setMinSupport(int minSupport) {
		this.minSupport = minSupport;
	}
	public double getMinPrecision() {
		return minPrecision;
	}
	public void setMinPrecision(double minPrecision) {
		this.minPrecision = minPrecision;
	}
	public int getMaxUsage() {
		return maxUsage;
	}
	public void setMaxUsage(int maxUsage) {
		this.maxUsage = maxUsage;
	}
	public int getMaxHypotheses() {
		return maxHypotheses;
	}
	public void setMaxHypotheses(int maxHypotheses) {
		this.maxHypotheses = maxHypotheses;
	}
	
	
}

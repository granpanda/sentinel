package gp.e3.sentinel.domain.constants;

public enum FailureCauses {
	
	TIMEOUT("TimeOut"),
	HEALTHCHECK("HealthCheck");
	
	private final String name;

	private FailureCauses(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
package gp.e3.sentinel.domain.entities;

public class System {
	
	private final long id;
	private final String name;
	private final String url;
	
	public System(long id, String name, String url) {
		
		this.id = id;
		this.name = name;
		this.url = url;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
}
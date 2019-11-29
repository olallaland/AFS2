package alpha.id;

import alpha.Id;

import java.io.Serializable;

public class StringId implements Id, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5682627581288256691L;
	private String id;
	public StringId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}
}

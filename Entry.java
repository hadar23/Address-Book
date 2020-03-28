class Entry {
	private String name;
	private String street;
	private String city;
	private String state;
	private int zip;

	public Entry(String name, String street, String city, String state, String zip) {
		setName(name);
		setStreet(street);
		setCity(city);
		setState(state);
		setZip(zip);
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public String getName() {
		return this.name;
	}

	public void setStreet(String street) {
		this.street = street.trim();
	}

	public String getStreet() {
		return this.street;
	}

	public void setCity(String city) {
		this.city = city.trim();
	}

	public String getCity() {
		return this.city;
	}

	public void setState(String state) {
		this.state = state.trim();
	}

	public String getState() {
		return this.state;
	}

	public void setZip(String zip) {
		try {
			this.zip = Integer.parseInt(zip.trim());
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}
	}

	public int getZip() {
		return this.zip;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getStreet() == null) ? 0 : getStreet().hashCode());
		result = prime * result + ((getCity() == null) ? 0 : getCity().hashCode());
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result + ((getState() == null) ? 0 : getState().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof Entry))
			return false;
		Entry ent = (Entry) obj;
		if (!this.getName().equals(ent.getName()))
			return false;
		if (!this.getStreet().equals(ent.getStreet()))
			return false;
		if (!this.getCity().equals(ent.getCity()))
			return false;
		if (!this.getState().equals(ent.getState()))
			return false;
		return true;
	}
}
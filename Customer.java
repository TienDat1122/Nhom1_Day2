import java.io.Serializable;
import java.util.Objects;

class Customer implements Serializable {
	private static final long serialVersionUID = 102L;
	private String customerId;
	private String name;
	private String idCard;
	private String phoneNumber;

	public Customer(String customerId, String name, String idCard, String phoneNumber) {
		this.customerId = customerId;
		this.name = name;
		this.idCard = idCard;
		this.phoneNumber = phoneNumber;
	}

	public String getCustomerId() {
		return customerId;
	}

	public String getName() {
		return name;
	}

	public String getIdCard() {
		return idCard;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Customer customer = (Customer) o;
		return Objects.equals(customerId, customer.customerId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(customerId);
	}

	@Override
	public String toString() {
		return name + " (ID: " + customerId.substring(0, 4) + " - CMND: " + idCard + ")";
	}
}
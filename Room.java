
// ========================================================================
// Data Model Classes
// ========================================================================

import java.io.Serializable;
import java.util.Objects;

class Room implements Serializable {
	private static final long serialVersionUID = 101L;
	private String roomNumber;
	private RoomType type;
	private double pricePerNight;
	private RoomStatus status;

	public Room(String roomNumber, RoomType type, double pricePerNight) {
		this.roomNumber = roomNumber;
		this.type = type;
		this.pricePerNight = pricePerNight;
		this.status = RoomStatus.AVAILABLE;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public RoomType getType() {
		return type;
	}

	public double getPricePerNight() {
		return pricePerNight;
	}

	public RoomStatus getStatus() {
		return status;
	}

	public void setType(RoomType type) {
		this.type = type;
	}

	public void setPricePerNight(double pricePerNight) {
		this.pricePerNight = pricePerNight;
	}

	public void setStatus(RoomStatus status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Room room = (Room) o;
		return Objects.equals(roomNumber, room.roomNumber);
	}

	@Override
	public int hashCode() {
		return Objects.hash(roomNumber);
	}

	@Override
	public String toString() {
		return roomNumber + " (" + type.getDisplayName() + " - " + String.format("%,.0f", pricePerNight) + ")";
	}
}
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

class Booking implements Serializable {
	private static final long serialVersionUID = 103L;
	private String bookingId;
	private String customerId;
	private String roomNumber;
	private LocalDateTime checkInDateTime;
	private LocalDateTime checkOutDateTime;
	private BookingStatus bookingStatus;
	private double pricePerNightAtBooking;
	private double totalPrice;

	public Booking(String bookingId, String customerId, String roomNumber, LocalDateTime checkInDateTime,
			LocalDateTime checkOutDateTime, double pricePerNightAtBooking) {
		this.bookingId = bookingId;
		this.customerId = customerId;
		this.roomNumber = roomNumber;
		this.checkInDateTime = checkInDateTime;
		this.checkOutDateTime = checkOutDateTime;
		this.pricePerNightAtBooking = pricePerNightAtBooking;
		this.bookingStatus = BookingStatus.CONFIRMED;
		this.totalPrice = calculateTotalPriceInternal();
	}

	public String getBookingId() {
		return bookingId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public LocalDateTime getCheckInDateTime() {
		return checkInDateTime;
	}

	public LocalDateTime getCheckOutDateTime() {
		return checkOutDateTime;
	}

	public BookingStatus getBookingStatus() {
		return bookingStatus;
	}

	public double getPricePerNightAtBooking() {
		return pricePerNightAtBooking;
	}

	public double getTotalPrice() {
		this.totalPrice = calculateTotalPriceInternal();
		return totalPrice;
	} // Luôn tính lại khi get

	public void setCheckInDateTime(LocalDateTime checkInDateTime) {
		this.checkInDateTime = checkInDateTime;
		this.totalPrice = calculateTotalPriceInternal();
	}

	public void setCheckOutDateTime(LocalDateTime checkOutDateTime) {
		this.checkOutDateTime = checkOutDateTime;
		this.totalPrice = calculateTotalPriceInternal();
	}

	public void setBookingStatus(BookingStatus bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	} // Cho phép ghi đè tổng tiền cuối cùng

	public long calculateNights() { // Public để dialog checkout có thể dùng nếu cần
		if (checkInDateTime == null || checkOutDateTime == null || !checkOutDateTime.isAfter(checkInDateTime))
			return 0;
		long nights = ChronoUnit.DAYS.between(checkInDateTime.toLocalDate(), checkOutDateTime.toLocalDate());
		if (nights <= 0 && checkOutDateTime.isAfter(checkInDateTime))
			return 1;
		// Xem xét lại logic cộng thêm ngày nếu giờ checkout > giờ checkin
		if (checkOutDateTime.toLocalTime().isAfter(checkInDateTime.toLocalTime()) && nights >= 0) {
			// nights++; // Tạm thời không cộng thêm ngày chỉ vì quá giờ, có thể tính phụ
			// thu
		}
		return nights < 0 ? 0 : nights; // Trả về 0 nếu nights âm
	}

	private double calculateTotalPriceInternal() {
		long nights = calculateNights();
		return nights * pricePerNightAtBooking;
	}

	public boolean overlaps(LocalDateTime otherCheckIn, LocalDateTime otherCheckOut) {
		if (otherCheckIn == null || otherCheckOut == null || !otherCheckOut.isAfter(otherCheckIn))
			return false;
		return this.checkInDateTime.isBefore(otherCheckOut) && this.checkOutDateTime.isAfter(otherCheckIn);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Booking booking = (Booking) o;
		return Objects.equals(bookingId, booking.bookingId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(bookingId);
	}

	@Override
	public String toString() {
		return "Booking [ID=" + bookingId + ", KH_ID=" + customerId.substring(0, 4) + ", Phòng=" + roomNumber
				+ ", Status=" + bookingStatus + "]";
	}
}
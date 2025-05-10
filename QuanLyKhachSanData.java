
// ========================================================================
// Data Management Class
// ========================================================================

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

class QuanLyKhachSanData implements Serializable {
	private static final long serialVersionUID = 200L;
	private List<Room> rooms;
	private List<Customer> customers;
	private List<Booking> bookings;
	private transient String dataFilePath;

	public QuanLyKhachSanData(String dataFilePath) {
		this.rooms = new ArrayList<>();
		this.customers = new ArrayList<>();
		this.bookings = new ArrayList<>();
		this.dataFilePath = dataFilePath;
	}

	public static String generateId() {
		return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

	// Room Methods
	public List<Room> getAllRooms() {
		return new ArrayList<>(rooms);
	}

	public Optional<Room> findRoomByNumber(String roomNumber) {
		if (roomNumber == null || roomNumber.trim().isEmpty())
			return Optional.empty();
		return rooms.stream().filter(r -> r.getRoomNumber().equalsIgnoreCase(roomNumber.trim())).findFirst();
	}

	public synchronized boolean addRoom(Room room) {
		if (room == null || findRoomByNumber(room.getRoomNumber()).isPresent())
			return false;
		return rooms.add(room);
	}

	public synchronized boolean updateRoom(Room updatedRoom) {
		Optional<Room> existingOpt = findRoomByNumber(updatedRoom.getRoomNumber());
		if (existingOpt.isPresent()) {
			Room existing = existingOpt.get();
			existing.setType(updatedRoom.getType());
			existing.setPricePerNight(updatedRoom.getPricePerNight());
			existing.setStatus(updatedRoom.getStatus());
			return true;
		}
		return false;
	}

	public synchronized boolean deleteRoom(String roomNumber) {
		return rooms.removeIf(r -> r.getRoomNumber().equalsIgnoreCase(roomNumber));
	}

	public List<Room> searchRooms(String keyword, RoomType type, RoomStatus status) {
		String lowerKeyword = (keyword != null) ? keyword.trim().toLowerCase() : "";
		return rooms.stream()
				.filter(r -> lowerKeyword.isEmpty() || r.getRoomNumber().toLowerCase().contains(lowerKeyword))
				.filter(r -> type == null || r.getType() == type).filter(r -> status == null || r.getStatus() == status)
				.collect(Collectors.toList());
	}

	public boolean isRoomCurrentlyBookedOrOccupied(String roomNumber) {
		if (roomNumber == null)
			return false;
		return bookings.stream().anyMatch(
				b -> b.getRoomNumber().equalsIgnoreCase(roomNumber) && (b.getBookingStatus() == BookingStatus.CONFIRMED
						|| b.getBookingStatus() == BookingStatus.CHECKED_IN));
	}

	public synchronized boolean updateRoomStatus(String roomNumber, RoomStatus newStatus) {
		Optional<Room> roomOpt = findRoomByNumber(roomNumber);
		if (roomOpt.isPresent()) {
			Room room = roomOpt.get();
			room.setStatus(newStatus);
			System.out.println("Đã cập nhật trạng thái phòng " + roomNumber + " thành " + newStatus);
			return true;
		}
		System.err.println("Cập nhật trạng thái phòng lỗi: Không tìm thấy phòng " + roomNumber);
		return false;
	}

	// Customer Methods
	public List<Customer> getAllCustomers() {
		return new ArrayList<>(customers);
	}

	public Optional<Customer> findCustomerById(String customerId) {
		if (customerId == null)
			return Optional.empty();
		return customers.stream().filter(c -> c.getCustomerId().equals(customerId)).findFirst();
	}

	public Optional<Customer> findCustomerByIdCard(String idCard) {
		if (idCard == null || idCard.trim().isEmpty())
			return Optional.empty();
		return customers.stream().filter(c -> c.getIdCard().equalsIgnoreCase(idCard.trim())).findFirst();
	}

	public synchronized boolean addCustomer(Customer customer) {
		if (customer == null || findCustomerById(customer.getCustomerId()).isPresent())
			return false;
		if (findCustomerByIdCard(customer.getIdCard()).isPresent()) {
			System.err.println("Cảnh báo khi thêm KH: CMND/CCCD " + customer.getIdCard() + " đã tồn tại.");
		}
		return customers.add(customer);
	}

	public synchronized boolean updateCustomer(Customer updatedCustomer) {
		Optional<Customer> existingOpt = findCustomerById(updatedCustomer.getCustomerId());
		if (existingOpt.isPresent()) {
			Customer existing = existingOpt.get();
			if (!existing.getIdCard().equalsIgnoreCase(updatedCustomer.getIdCard())) {
				Optional<Customer> otherCust = findCustomerByIdCard(updatedCustomer.getIdCard());
				if (otherCust.isPresent() && !otherCust.get().getCustomerId().equals(existing.getCustomerId())) {
					System.err.println(
							"Cập nhật KH lỗi: CMND/CCCD '" + updatedCustomer.getIdCard() + "' đã thuộc về KH khác.");
					return false;
				}
			}
			existing.setName(updatedCustomer.getName());
			existing.setIdCard(updatedCustomer.getIdCard());
			existing.setPhoneNumber(updatedCustomer.getPhoneNumber());
			return true;
		}
		return false;
	}

	public synchronized boolean deleteCustomer(String customerId) {
		return customers.removeIf(c -> c.getCustomerId().equals(customerId));
	}

	public List<Customer> searchCustomers(String keyword) {
		if (keyword == null || keyword.trim().isEmpty())
			return getAllCustomers();
		String lowerKeyword = keyword.trim().toLowerCase();
		String phoneKeyword = keyword.trim();
		return customers.stream()
				.filter(c -> c.getName().toLowerCase().contains(lowerKeyword)
						|| c.getIdCard().toLowerCase().contains(lowerKeyword)
						|| c.getPhoneNumber().contains(phoneKeyword))
				.collect(Collectors.toList());
	}

	public boolean doesCustomerHaveActiveBookings(String customerId) {
		if (customerId == null)
			return false;
		return bookings.stream()
				.anyMatch(b -> b.getCustomerId().equals(customerId) && (b.getBookingStatus() == BookingStatus.CONFIRMED
						|| b.getBookingStatus() == BookingStatus.CHECKED_IN));
	}

	// Booking Methods
	public List<Booking> getAllBookings() {
		return new ArrayList<>(bookings);
	}

	public Optional<Booking> findBookingById(String bookingId) {
		if (bookingId == null)
			return Optional.empty();
		return bookings.stream().filter(b -> b.getBookingId().equals(bookingId)).findFirst();
	}

	public boolean isRoomAvailable(String roomNumber, LocalDateTime checkIn, LocalDateTime checkOut) {
		if (roomNumber == null || checkIn == null || checkOut == null || !checkOut.isAfter(checkIn))
			return false;
		Optional<Room> roomOpt = findRoomByNumber(roomNumber);
		if (roomOpt.isEmpty() || roomOpt.get().getStatus() == RoomStatus.MAINTENANCE) {
			System.out.println("Debug: Phòng " + roomNumber + " không tồn tại hoặc đang bảo trì.");
			return false;
		}
		return bookings.stream().filter(b -> b.getRoomNumber().equalsIgnoreCase(roomNumber))
				.filter(b -> b.getBookingStatus() == BookingStatus.CONFIRMED
						|| b.getBookingStatus() == BookingStatus.CHECKED_IN)
				.noneMatch(existingBooking -> existingBooking.overlaps(checkIn, checkOut));
	}

	public synchronized String addBooking(String customerId, String roomNumber, LocalDateTime checkIn,
			LocalDateTime checkOut) {
		Optional<Customer> custOpt = findCustomerById(customerId);
		Optional<Room> roomOpt = findRoomByNumber(roomNumber);
		if (custOpt.isEmpty() || roomOpt.isEmpty()) {
			System.err.println("Thêm booking lỗi: KH hoặc phòng không tồn tại.");
			return null;
		}
		if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
			System.err.println("Thêm booking lỗi: Thời gian không hợp lệ.");
			return null;
		}
		if (!isRoomAvailable(roomNumber, checkIn, checkOut)) {
			System.err.println("Thêm booking lỗi: Phòng " + roomNumber + " không trống.");
			return null;
		}
		Room room = roomOpt.get();
		String bookingId = generateId();
		Booking newBooking = new Booking(bookingId, customerId, roomNumber, checkIn, checkOut, room.getPricePerNight());
		if (bookings.add(newBooking)) {
			System.out.println("Booking mới đã được thêm: " + bookingId);
			return bookingId;
		} else {
			System.err.println("Thêm booking lỗi: Không thể thêm vào danh sách.");
			return null;
		}
	}

	public synchronized boolean cancelBooking(String bookingId) {
		Optional<Booking> bookingOpt = findBookingById(bookingId);
		if (bookingOpt.isPresent()) {
			Booking booking = bookingOpt.get();
			if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
				booking.setBookingStatus(BookingStatus.CANCELLED);
				System.out.println("Đã hủy booking: " + bookingId);
				return true;
			} else {
				System.err.println("Hủy booking lỗi: Booking " + bookingId + " không ở trạng thái CONFIRMED.");
				return false;
			}
		}
		System.err.println("Hủy booking lỗi: Không tìm thấy booking ID " + bookingId);
		return false;
	}

	public synchronized boolean checkOutBooking(String bookingId, LocalDateTime actualCheckOutTime) {
		Optional<Booking> bookingOpt = findBookingById(bookingId);

		if (bookingOpt.isPresent()) { // <<< Outer IF: Nếu tìm thấy booking
			Booking booking = bookingOpt.get();
			// Kiểm tra trạng thái có phù hợp để check-out không
			if (booking.getBookingStatus() == BookingStatus.CONFIRMED
					|| booking.getBookingStatus() == BookingStatus.CHECKED_IN) {
				// Cập nhật thông tin booking
				booking.setCheckOutDateTime(actualCheckOutTime);
				booking.setBookingStatus(BookingStatus.CHECKED_OUT);
				System.out.println("Đã check-out booking: " + bookingId + " lúc "
						+ actualCheckOutTime.format(KhachSanProGUI.DATE_TIME_FORMAT));
				return true; // Check-out thành công
			} else {
				// Trạng thái không hợp lệ để check-out
				System.err.println("Check-out lỗi: Booking " + bookingId
						+ " không ở trạng thái CONFIRMED hoặc CHECKED_IN (hiện tại: " + booking.getBookingStatus()
						+ ")");
				return false; // Check-out thất bại do trạng thái
			}
		} else { // <<< Outer ELSE: Nếu KHÔNG tìm thấy booking
			System.err.println("Check-out lỗi: Không tìm thấy booking ID " + bookingId);
			return false; // Check-out thất bại do không tìm thấy
		}
		// Không cần return gì ở đây nữa vì mọi trường hợp đã được xử lý trong if/else
	}

	public List<Booking> searchBookings(String keyword, BookingStatus status) {
		String lowerKeyword = (keyword != null) ? keyword.trim().toLowerCase() : "";
		return bookings.stream()
				.filter(b -> lowerKeyword.isEmpty() || b.getRoomNumber().toLowerCase().contains(lowerKeyword)
						|| findCustomerById(b.getCustomerId())
								.map(c -> c.getName().toLowerCase().contains(lowerKeyword)).orElse(false))
				.filter(b -> status == null || b.getBookingStatus() == status).collect(Collectors.toList());
	}

	// Statistics Methods
	public double calculateMonthlyRevenue(YearMonth month) {
		LocalDateTime start = month.atDay(1).atStartOfDay();
		LocalDateTime end = month.atEndOfMonth().atTime(23, 59, 59, 999999999);
		return bookings.stream().filter(b -> b.getBookingStatus() == BookingStatus.CHECKED_OUT).filter(
				b -> !b.getCheckOutDateTime().isBefore(start) && b.getCheckOutDateTime().isBefore(end.plusNanos(1)))
				.mapToDouble(Booking::getTotalPrice).sum();
	}

	public double calculateYearlyRevenue(int year) {
		LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0, 0);
		LocalDateTime end = LocalDateTime.of(year, 12, 31, 23, 59, 59, 999999999);
		return bookings.stream().filter(b -> b.getBookingStatus() == BookingStatus.CHECKED_OUT).filter(
				b -> !b.getCheckOutDateTime().isBefore(start) && b.getCheckOutDateTime().isBefore(end.plusNanos(1)))
				.mapToDouble(Booking::getTotalPrice).sum();
	}

	// File I/O Methods
	public static QuanLyKhachSanData taiDuLieu(String filePath) {
		File f = new File(filePath);
		if (!f.exists()) {
			System.out.println("Thông báo: File dữ liệu '" + filePath + "' không tồn tại.");
			return null;
		}
		try (FileInputStream fis = new FileInputStream(f); ObjectInputStream ois = new ObjectInputStream(fis)) {
			Object obj = ois.readObject();
			if (obj instanceof QuanLyKhachSanData) {
				QuanLyKhachSanData data = (QuanLyKhachSanData) obj;
				data.dataFilePath = filePath;
				System.out.println("Dữ liệu khách sạn (" + data.rooms.size() + "p, " + data.customers.size() + "k, "
						+ data.bookings.size() + "b) đã tải từ: " + filePath);
				return data;
			} else {
				System.err.println("Lỗi tải: File '" + filePath + "' không hợp lệ.");
				return null;
			}
		} catch (FileNotFoundException e) {
			System.out.println("Thông báo: File '" + filePath + "' không tìm thấy.");
			return null;
		} catch (IOException | ClassNotFoundException | ClassCastException e) {
			System.err.println("Lỗi nghiêm trọng khi tải dữ liệu từ " + filePath + ": " + e.getMessage());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Lỗi đọc file dữ liệu cũ:\n" + filePath + "\nLỗi: " + e.getMessage()
					+ "\nBắt đầu với dữ liệu trống.", "Lỗi Tải Dữ Liệu", JOptionPane.WARNING_MESSAGE);
			return null;
		}
	}

	public synchronized boolean luuDuLieu() {
		if (dataFilePath == null || dataFilePath.trim().isEmpty()) {
			System.err.println("Lỗi lưu: Đường dẫn file không hợp lệ.");
			JOptionPane.showMessageDialog(null, "Không thể lưu: Đường dẫn file chưa được thiết lập.", "Lỗi Lưu Dữ Liệu",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		try {
			Files.createDirectories(Paths.get(dataFilePath).getParent());
		} catch (IOException e) {
			System.err.println("Lỗi tạo thư mục lưu: " + Paths.get(dataFilePath).getParent());
			JOptionPane.showMessageDialog(null, "Không thể tạo thư mục lưu file:\n"
					+ Paths.get(dataFilePath).getParent() + "\nLỗi: " + e.getMessage(), "Lỗi Lưu Dữ Liệu",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		try (FileOutputStream fos = new FileOutputStream(dataFilePath);
				ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(this);
			System.out.println("Dữ liệu (" + rooms.size() + "p, " + customers.size() + "k, " + bookings.size()
					+ "b) đã lưu vào: " + dataFilePath);
			return true;
		} catch (IOException e) {
			System.err.println("Lỗi nghiêm trọng khi lưu dữ liệu vào " + dataFilePath + ": " + e.getMessage());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Không thể lưu dữ liệu vào file:\n" + dataFilePath + "\nLỗi: " + e.getMessage(), "Lỗi Lưu Dữ Liệu",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

	}
	 public synchronized boolean checkInBooking(String bookingId) {
	        Optional<Booking> bookingOpt = findBookingById(bookingId);
	        if (bookingOpt.isPresent()) {
	            Booking booking = bookingOpt.get();
	            if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
	                // Cập nhật trạng thái booking
	                booking.setBookingStatus(BookingStatus.CHECKED_IN);

	                // Cập nhật trạng thái phòng
	                // Lưu ý: Cần xử lý trường hợp không tìm thấy phòng (dù ít khả năng xảy ra nếu dữ liệu nhất quán)
	                boolean roomUpdated = updateRoomStatus(booking.getRoomNumber(), RoomStatus.OCCUPIED);
	                if (!roomUpdated) {
	                    // Có thể xảy ra nếu phòng bị xóa sau khi đặt nhưng trước khi checkin? Hoặc lỗi logic khác.
	                    // Nên ghi log lại trường hợp này để kiểm tra. Booking đã CHECKED_IN nhưng phòng chưa OCCUPIED.
	                     System.err.println("Cảnh báo Check-in: Đặt phòng " + bookingId + " đã chuyển sang CHECKED_IN, nhưng không thể cập nhật trạng thái phòng " + booking.getRoomNumber() + " thành OCCUPIED.");
	                    // Dù sao vẫn trả về true vì booking đã được check-in
	                }

	                System.out.println("Check-in thành công cho đặt phòng: " + bookingId + ". Phòng: " + booking.getRoomNumber() + " -> OCCUPIED.");
	                return true; // Check-in thành công
	            } else {
	                // Trạng thái không hợp lệ để check-in
	                System.err.println("Check-in lỗi: Đặt phòng " + bookingId + " không ở trạng thái 'Đã xác nhận' (Trạng thái hiện tại: " + booking.getBookingStatus() + ")");
	                return false; // Check-in thất bại do trạng thái
	            }
	        } else {
	            // Không tìm thấy booking
	            System.err.println("Check-in lỗi: Không tìm thấy đặt phòng ID " + bookingId);
	            return false; // Check-in thất bại do không tìm thấy
	        }
	    }


	    // ... (checkOutBooking, searchBookings, statistics, file I/O giữ nguyên) ...
	}


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter; // Thêm để sắp xếp bảng
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

// ========================================================================
// Lớp Chính - Giao Diện Người Dùng
// ========================================================================
public class KhachSanProGUI extends JFrame {
	private static final long serialVersionUID = 1L;

	// --- Constants ---
	private static final DateTimeFormatter LOG_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"); // public
																												// để
																												// dialog
																												// dùng
	private static final String BASE_DIR_PATH = System.getProperty("user.dir");
	private static final String DATA_DIR_PATH = BASE_DIR_PATH + "/HotelProData";
	private static final String LOG_FILE_PATH = DATA_DIR_PATH + "/hotel_log.txt";
	private static final String DATA_FILE_PATH = DATA_DIR_PATH + "/hotel_data.dat";

	// --- Data Management ---
	private QuanLyKhachSanData qlData;

	// --- UI Components ---
	private JTabbedPane tabbedPane;

	// Room Tab Components
	private DefaultTableModel roomTableModel;
	private JTable roomTable;
	private JTextField roomSearchField;
	private JComboBox<Object> roomTypeFilterCombo; // Dùng Object để chứa "Tất cả"
	private JComboBox<Object> roomStatusFilterCombo;// Dùng Object để chứa "Tất cả"

	// Customer Tab Components
	private DefaultTableModel customerTableModel;
	private JTable customerTable;
	private JTextField customerSearchField;

	// Booking Tab Components
	private DefaultTableModel bookingTableModel;
	private JTable bookingTable;
	private JTextField bookingSearchField; // Tìm theo tên KH hoặc số phòng
	private JComboBox<Object> bookingStatusFilterCombo;// Dùng Object để chứa "Tất cả"

	// Statistics Tab Components
	private JTextArea statisticsTextArea;
	private JSpinner monthSpinner; // Chọn tháng
	private JSpinner yearSpinner; // Chọn năm

	public KhachSanProGUI() {
		// 1. Setup Data Directory and Load Data
		setupDirectories();
		qlData = QuanLyKhachSanData.taiDuLieu(DATA_FILE_PATH);
		if (qlData == null) {
			// Nếu tải thất bại (file chưa có, lỗi, ...) thì tạo đối tượng quản lý mới
			qlData = new QuanLyKhachSanData(DATA_FILE_PATH);
			ghiLogChung("Không thể tải dữ liệu từ file hoặc file chưa tồn tại. Bắt đầu với dữ liệu mới.");
		}

		// 2. Setup JFrame
		setTitle("Hệ Thống Quản Lý Khách Sạn Chuyên Nghiệp (Demo)");
		setSize(1000, 700); // Tăng kích thước
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Để xử lý lưu khi đóng
		setLocationRelativeTo(null);

		// Save data on close using WindowListener
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (qlData.luuDuLieu()) {
					ghiLogChung("Ứng dụng đóng. Đã lưu dữ liệu.");
				} else {
					// Thông báo lỗi lưu đã được hiển thị trong hàm luuDuLieu()
					ghiLogChung("Ứng dụng đóng. Có lỗi xảy ra khi lưu dữ liệu.");
				}
				System.exit(0); // Thoát ứng dụng
			}
		});

		// 3. Create UI Components
		tabbedPane = new JTabbedPane();

		// Add Tabs
		tabbedPane.addTab("Quản Lý Phòng", createRoomPanel());
		tabbedPane.addTab("Quản Lý Khách Hàng", createCustomerPanel());
		tabbedPane.addTab("Quản Lý Đặt Phòng", createBookingPanel());
		tabbedPane.addTab("Thống Kê & Báo Cáo", createStatisticsPanel());

		add(tabbedPane);

		// 4. Initial data load into tables
		refreshRoomTable();
		refreshCustomerTable();
		refreshBookingTable();

		setVisible(true);
		ghiLogChung("Hệ thống quản lý khách sạn đã khởi động.");
	}

	// --- Directory Setup ---
	private void setupDirectories() {
		try {
			Files.createDirectories(Paths.get(DATA_DIR_PATH));
			System.out.println("Thư mục dữ liệu được kiểm tra/tạo tại: " + DATA_DIR_PATH);
		} catch (IOException e) {
			System.err.println("Lỗi nghiêm trọng khi tạo thư mục dữ liệu: " + DATA_DIR_PATH);
			JOptionPane.showMessageDialog(this,
					"Không thể tạo thư mục lưu trữ dữ liệu:\n" + DATA_DIR_PATH + "\nLỗi: " + e.getMessage()
							+ "\nChương trình có thể không lưu được dữ liệu.",
					"Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
		}
	}

	// ========================================================================
	// Panel Creation Methods
	// ========================================================================

	// --- Create Room Panel ---
	private JPanel createRoomPanel() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		// --- Top: Search/Filter ---
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(new JLabel("Tìm số phòng:"));
		roomSearchField = new JTextField(15);
		topPanel.add(roomSearchField);

		topPanel.add(new JLabel("Loại phòng:"));
		Vector<Object> roomTypes = new Vector<>();
		roomTypes.add("Tất cả"); // Add "All" option
		roomTypes.addAll(List.of(RoomType.values()));
		roomTypeFilterCombo = new JComboBox<>(roomTypes);
		topPanel.add(roomTypeFilterCombo);

		topPanel.add(new JLabel("Trạng thái:"));
		Vector<Object> roomStatuses = new Vector<>();
		roomStatuses.add("Tất cả"); // Add "All" option
		roomStatuses.addAll(List.of(RoomStatus.values()));
		roomStatusFilterCombo = new JComboBox<>(roomStatuses);
		topPanel.add(roomStatusFilterCombo);

		JButton searchRoomBtn = new JButton("Tìm kiếm / Làm mới");
		searchRoomBtn.addActionListener(e -> searchRoomsAction());
		topPanel.add(searchRoomBtn);

		// --- Center: Table ---
		String[] roomColumns = { "Số Phòng", "Loại", "Giá/Đêm", "Trạng Thái" };
		roomTableModel = new DefaultTableModel(roomColumns, 0) {
			private static final long serialVersionUID = 1L; // Add serialVersionUID for Serializable class

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		roomTable = new JTable(roomTableModel);
		roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		roomTable.setRowSorter(new TableRowSorter<>(roomTableModel)); // Enable sorting
		JScrollPane roomScrollPane = new JScrollPane(roomTable);

		// --- Bottom: Buttons ---
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton addRoomBtn = new JButton("Thêm Phòng");
		addRoomBtn.addActionListener(e -> addRoomAction());
		bottomPanel.add(addRoomBtn);

		JButton editRoomBtn = new JButton("Sửa Phòng");
		editRoomBtn.addActionListener(e -> editRoomAction());
		bottomPanel.add(editRoomBtn);

		JButton deleteRoomBtn = new JButton("Xóa Phòng");
		deleteRoomBtn.addActionListener(e -> deleteRoomAction());
		bottomPanel.add(deleteRoomBtn);

		panel.add(topPanel, BorderLayout.NORTH);
		panel.add(roomScrollPane, BorderLayout.CENTER);
		panel.add(bottomPanel, BorderLayout.SOUTH);

		return panel;
	}

	// --- Create Customer Panel ---
	private JPanel createCustomerPanel() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		// --- Top: Search ---
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(new JLabel("Tìm Tên/CMND/SĐT:"));
		customerSearchField = new JTextField(20);
		topPanel.add(customerSearchField);

		JButton searchCustomerBtn = new JButton("Tìm kiếm / Làm mới");
		searchCustomerBtn.addActionListener(e -> searchCustomersAction());
		topPanel.add(searchCustomerBtn);

		// --- Center: Table ---
		String[] customerColumns = { "Mã KH", "Họ Tên", "CMND/CCCD", "Số Điện Thoại" };
		customerTableModel = new DefaultTableModel(customerColumns, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		customerTable = new JTable(customerTableModel);
		customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		customerTable.setRowSorter(new TableRowSorter<>(customerTableModel)); // Enable sorting
		JScrollPane customerScrollPane = new JScrollPane(customerTable);

		// --- Bottom: Buttons ---
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton addCustomerBtn = new JButton("Thêm Khách Hàng");
		addCustomerBtn.addActionListener(e -> addCustomerAction());
		bottomPanel.add(addCustomerBtn);

		JButton editCustomerBtn = new JButton("Sửa Thông Tin KH");
		editCustomerBtn.addActionListener(e -> editCustomerAction());
		bottomPanel.add(editCustomerBtn);

		JButton deleteCustomerBtn = new JButton("Xóa Khách Hàng");
		deleteCustomerBtn.addActionListener(e -> deleteCustomerAction());
		bottomPanel.add(deleteCustomerBtn);

		panel.add(topPanel, BorderLayout.NORTH);
		panel.add(customerScrollPane, BorderLayout.CENTER);
		panel.add(bottomPanel, BorderLayout.SOUTH);

		return panel;
	}

	// --- Create Booking Panel ---
	private JPanel createBookingPanel() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		// --- Top: Search/Filter ---
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(new JLabel("Tìm Tên KH/Phòng:"));
		bookingSearchField = new JTextField(20);
		topPanel.add(bookingSearchField);

		topPanel.add(new JLabel("Trạng thái Đặt:"));
		Vector<Object> bookingStatuses = new Vector<>();
		bookingStatuses.add("Tất cả"); // Add "All" option
		bookingStatuses.addAll(List.of(BookingStatus.values()));
		bookingStatusFilterCombo = new JComboBox<>(bookingStatuses);
		topPanel.add(bookingStatusFilterCombo);

		JButton searchBookingBtn = new JButton("Tìm kiếm / Làm mới");
		searchBookingBtn.addActionListener(e -> searchBookingsAction());
		topPanel.add(searchBookingBtn);

		// --- Center: Table ---
		String[] bookingColumns = { "Mã Đặt", "Tên Khách Hàng", "Số Phòng", "Check-in", "Check-out", "Trạng Thái",
				"Tổng Tiền" };
		bookingTableModel = new DefaultTableModel(bookingColumns, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		bookingTable = new JTable(bookingTableModel);
		bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		bookingTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Tên KH rộng hơn
		bookingTable.getColumnModel().getColumn(3).setPreferredWidth(130); // Check-in
		bookingTable.getColumnModel().getColumn(4).setPreferredWidth(130); // Check-out
		bookingTable.setRowSorter(new TableRowSorter<>(bookingTableModel)); // Enable sorting
		JScrollPane bookingScrollPane = new JScrollPane(bookingTable);

		// --- Bottom: Buttons ---
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton addBookingBtn = new JButton("Tạo Đặt Phòng Mới");
		addBookingBtn.addActionListener(e -> addBookingAction());
		bottomPanel.add(addBookingBtn);

		JButton cancelBookingBtn = new JButton("Hủy Đặt Phòng");
		cancelBookingBtn.addActionListener(e -> cancelBookingAction());
		bottomPanel.add(cancelBookingBtn);

		// Nút Check-in (Tạm thời vô hiệu hóa, cần thêm logic)
		  JButton checkInBtn = new JButton("Check-in");
	        checkInBtn.setEnabled(true); // **** KÍCH HOẠT NÚT ****
	        checkInBtn.addActionListener(e -> checkInAction()); // **** THÊM ACTION LISTENER ****
	        bottomPanel.add(checkInBtn);

		// Nút Check-out (Đã kích hoạt và thêm action)
		JButton checkOutBtn = new JButton("Check-out & Tính Tiền");
		checkOutBtn.addActionListener(e -> checkOutAction()); // Thêm ActionListener
		bottomPanel.add(checkOutBtn); // Thêm nút vào panel

		panel.add(topPanel, BorderLayout.NORTH);
		panel.add(bookingScrollPane, BorderLayout.CENTER);
		panel.add(bottomPanel, BorderLayout.SOUTH);

		return panel;
	}

	// --- Create Statistics Panel ---
	private JPanel createStatisticsPanel() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		// --- Top: Controls ---
		JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		controlPanel.add(new JLabel("Thống kê Doanh thu Tháng:"));
		SpinnerModel monthModel = new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1);
		monthSpinner = new JSpinner(monthModel);
		controlPanel.add(monthSpinner);

		controlPanel.add(new JLabel("Năm:"));
		SpinnerModel yearModel = new SpinnerNumberModel(LocalDate.now().getYear(), LocalDate.now().getYear() - 5,
				LocalDate.now().getYear() + 5, 1);
		yearSpinner = new JSpinner(yearModel);
		JSpinner.NumberEditor yearEditor = new JSpinner.NumberEditor(yearSpinner, "#");
		yearSpinner.setEditor(yearEditor);
		controlPanel.add(yearSpinner);

		JButton revenueBtn = new JButton("Xem Doanh Thu Tháng/Năm");
		revenueBtn.addActionListener(e -> showRevenueAction());
		controlPanel.add(revenueBtn);

		JButton customerListBtn = new JButton("Danh Sách Khách Hàng");
		customerListBtn.addActionListener(e -> showCustomerListAction());
		controlPanel.add(customerListBtn);

		JButton roomListBtn = new JButton("Danh Sách Phòng");
		roomListBtn.addActionListener(e -> showRoomListAction());
		controlPanel.add(roomListBtn);

		// --- Center: Display Area ---
		statisticsTextArea = new JTextArea(15, 70);
		statisticsTextArea.setEditable(false);
		statisticsTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		JScrollPane scrollPane = new JScrollPane(statisticsTextArea);

		panel.add(controlPanel, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}

	// ========================================================================
	// Action Methods
	// ========================================================================

	// --- Room Actions ---
	private void refreshRoomTable(List<Room> roomsToShow) {
		roomTableModel.setRowCount(0);
		if (roomsToShow != null) {
			for (Room room : roomsToShow) {
				roomTableModel.addRow(new Object[] { room.getRoomNumber(), room.getType(),
						String.format("%,.0f", room.getPricePerNight()), room.getStatus() });
			}
		}
	}
	 private void checkInAction() {
	        int selectedRow = bookingTable.getSelectedRow();
	        if (selectedRow == -1) {
	            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đặt phòng từ bảng để check-in.", "Chưa Chọn Đặt Phòng", JOptionPane.WARNING_MESSAGE);
	            return;
	        }

	        try {
	            int modelRow = bookingTable.convertRowIndexToModel(selectedRow);
	            String bookingId = (String) bookingTableModel.getValueAt(modelRow, 0);
	            Optional<Booking> bookingOpt = qlData.findBookingById(bookingId); // Kiểm tra lại lần nữa phòng trường hợp dữ liệu thay đổi

	            if (bookingOpt.isPresent()) {
	                Booking booking = bookingOpt.get();
	                String roomNumber = booking.getRoomNumber();
	                 String customerName = qlData.findCustomerById(booking.getCustomerId())
	                                           .map(Customer::getName)
	                                           .orElse("KH#" + booking.getCustomerId().substring(0,4));

	                // Chỉ cho phép check-in khi trạng thái là CONFIRMED
	                if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
	                    JOptionPane.showMessageDialog(this,
	                        "Không thể check-in đặt phòng này.\nTrạng thái hiện tại: " + booking.getBookingStatus() + "\nChỉ có thể check-in khi trạng thái là 'Đã xác nhận'.",
	                        "Trạng Thái Không Hợp Lệ", JOptionPane.WARNING_MESSAGE);
	                    return;
	                }

	                // Xác nhận hành động (tùy chọn nhưng nên có)
	                int confirm = JOptionPane.showConfirmDialog(this,
	                    String.format("Xác nhận check-in cho khách '%s' vào phòng '%s'?", customerName, roomNumber),
	                    "Xác Nhận Check-in", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

	                if (confirm == JOptionPane.YES_OPTION) {
	                    // Gọi hàm checkInBooking trong qlData
	                    boolean checkInSuccess = qlData.checkInBooking(bookingId);

	                    if (checkInSuccess) {
	                        qlData.luuDuLieu(); // Lưu thay đổi
	                        refreshBookingTable(); // Cập nhật bảng booking (trạng thái -> CHECKED_IN)
	                        refreshRoomTable();    // Cập nhật bảng phòng (trạng thái -> OCCUPIED)

	                        ghiLogChung("Check-in thành công cho đặt phòng: " + bookingId + " của KH: " + customerName + ". Phòng: " + roomNumber);
	                        JOptionPane.showMessageDialog(this, "Check-in thành công cho đặt phòng " + bookingId + ".\nPhòng " + roomNumber + " đã chuyển sang trạng thái 'Đang có khách'.", "Check-in Thành Công", JOptionPane.INFORMATION_MESSAGE);

	                    } else {
	                        // qlData.checkInBooking đã ghi lỗi vào console, chỉ cần thông báo người dùng
	                        JOptionPane.showMessageDialog(this, "Không thể thực hiện check-in. Vui lòng kiểm tra lại trạng thái đặt phòng hoặc liên hệ quản trị viên.", "Lỗi Check-in", JOptionPane.ERROR_MESSAGE);
	                        refreshBookingTable(); // Cập nhật lại bảng đề phòng có thay đổi ngầm
	                    }
	                } else {
	                    ghiLogChung("Người dùng đã hủy thao tác check-in cho đặt phòng: " + bookingId);
	                }

	            } else {
	                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin đặt phòng đã chọn (có thể đã bị xóa).", "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
	                refreshBookingTable();
	            }
	        } catch (Exception ex) {
	            ghiLogError("Lỗi nghiêm trọng trong quá trình check-in", ex);
	            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không mong muốn khi thực hiện check-in.", "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	private void refreshRoomTable() {
		refreshRoomTable(qlData.getAllRooms());
	}

	private void searchRoomsAction() {
		String keyword = roomSearchField.getText().trim().toLowerCase();
		Object selectedTypeItem = roomTypeFilterCombo.getSelectedItem();
		Object selectedStatusItem = roomStatusFilterCombo.getSelectedItem();

		RoomType typeFilter = (selectedTypeItem instanceof RoomType) ? (RoomType) selectedTypeItem : null;
		RoomStatus statusFilter = (selectedStatusItem instanceof RoomStatus) ? (RoomStatus) selectedStatusItem : null;

		List<Room> filteredRooms = qlData.searchRooms(keyword, typeFilter, statusFilter);
		refreshRoomTable(filteredRooms);
		ghiLogChung(
				"Tìm kiếm phòng với từ khóa: '" + keyword + "', Loại: " + (typeFilter != null ? typeFilter : "Tất cả")
						+ ", Trạng thái: " + (statusFilter != null ? statusFilter : "Tất cả"));
	}

	private void addRoomAction() {
		RoomDialog dialog = new RoomDialog(this, qlData, null);
		dialog.setVisible(true);
		if (dialog.isSaved()) {
			refreshRoomTable();
			ghiLogChung("Đã mở dialog thêm phòng và lưu thành công.");
		}
	}

	private void editRoomAction() {
		int selectedRow = roomTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng từ bảng để sửa.", "Chưa chọn phòng",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			int modelRow = roomTable.convertRowIndexToModel(selectedRow);
			String roomNumber = (String) roomTableModel.getValueAt(modelRow, 0);
			Optional<Room> roomOpt = qlData.findRoomByNumber(roomNumber);

			if (roomOpt.isPresent()) {
				RoomDialog dialog = new RoomDialog(this, qlData, roomOpt.get());
				dialog.setVisible(true);
				if (dialog.isSaved()) {
					refreshRoomTable();
					ghiLogChung("Đã mở dialog sửa phòng '" + roomNumber + "' và lưu thành công.");
				}
			} else {
				JOptionPane.showMessageDialog(this,
						"Không tìm thấy phòng '" + roomNumber + "' trong dữ liệu. Có thể phòng đã bị xóa.",
						"Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
				refreshRoomTable();
			}
		} catch (Exception ex) {
			ghiLogError("Lỗi khi chuẩn bị mở dialog sửa phòng", ex);
			JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi lấy thông tin phòng để sửa.", "Lỗi Hệ Thống",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void deleteRoomAction() {
		int selectedRow = roomTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng từ bảng để xóa.", "Chưa chọn phòng",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			int modelRow = roomTable.convertRowIndexToModel(selectedRow);
			String roomNumber = (String) roomTableModel.getValueAt(modelRow, 0);

			if (qlData.isRoomCurrentlyBookedOrOccupied(roomNumber)) {
				JOptionPane.showMessageDialog(this, "Không thể xóa phòng '" + roomNumber
						+ "' vì đang có đặt phòng chưa hoàn thành hoặc khách đang ở.\nVui lòng hoàn tất hoặc hủy các đặt phòng liên quan trước.",
						"Xóa Bị Chặn", JOptionPane.ERROR_MESSAGE);
				ghiLogChung("Xóa phòng '" + roomNumber + "' thất bại do đang sử dụng.");
				return;
			}

			int confirm = JOptionPane.showConfirmDialog(this,
					"Bạn có chắc muốn xóa vĩnh viễn phòng '" + roomNumber
							+ "' không?\nHành động này không thể hoàn tác.",
					"Xác Nhận Xóa Phòng", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

			if (confirm == JOptionPane.YES_OPTION) {
				if (qlData.deleteRoom(roomNumber)) {
					ghiLogChung("Đã xóa phòng: " + roomNumber);
					refreshRoomTable();
					JOptionPane.showMessageDialog(this, "Đã xóa phòng '" + roomNumber + "' thành công.", "Thành Công",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(this,
							"Xóa phòng '" + roomNumber + "' thất bại. Phòng có thể không tồn tại.", "Lỗi Logic",
							JOptionPane.ERROR_MESSAGE);
					refreshRoomTable();
				}
			} else {
				ghiLogChung("Người dùng đã hủy thao tác xóa phòng '" + roomNumber + "'.");
			}
		} catch (Exception ex) {
			ghiLogError("Lỗi trong quá trình xóa phòng", ex);
			JOptionPane.showMessageDialog(this, "Có lỗi hệ thống xảy ra khi xóa phòng.", "Lỗi Hệ Thống",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// --- Customer Actions ---
	private void refreshCustomerTable(List<Customer> customersToShow) {
		customerTableModel.setRowCount(0);
		if (customersToShow != null) {
			for (Customer cust : customersToShow) {
				customerTableModel.addRow(
						new Object[] { cust.getCustomerId(), cust.getName(), cust.getIdCard(), cust.getPhoneNumber() });
			}
		}
	}

	private void refreshCustomerTable() {
		refreshCustomerTable(qlData.getAllCustomers());
	}

	private void searchCustomersAction() {
		String keyword = customerSearchField.getText().trim();
		List<Customer> filteredCustomers = qlData.searchCustomers(keyword);
		refreshCustomerTable(filteredCustomers);
		ghiLogChung("Tìm kiếm khách hàng với từ khóa: '" + keyword + "'");
	}

	private void addCustomerAction() {
		CustomerDialog dialog = new CustomerDialog(this, qlData, null);
		dialog.setVisible(true);
		if (dialog.isSaved()) {
			refreshCustomerTable();
			ghiLogChung("Đã mở dialog thêm khách hàng và lưu thành công.");
		}
	}

	private void editCustomerAction() {
		int selectedRow = customerTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng từ bảng để sửa.", "Chưa chọn khách hàng",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			int modelRow = customerTable.convertRowIndexToModel(selectedRow);
			String customerId = (String) customerTableModel.getValueAt(modelRow, 0);
			Optional<Customer> custOpt = qlData.findCustomerById(customerId);

			if (custOpt.isPresent()) {
				CustomerDialog dialog = new CustomerDialog(this, qlData, custOpt.get());
				dialog.setVisible(true);
				if (dialog.isSaved()) {
					refreshCustomerTable();
					refreshBookingTable(); // Cập nhật bảng booking vì tên KH có thể đã đổi
					ghiLogChung("Đã mở dialog sửa khách hàng '" + customerId + "' và lưu thành công.");
				}
			} else {
				JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng mã '" + customerId + "'.", "Lỗi Dữ Liệu",
						JOptionPane.ERROR_MESSAGE);
				refreshCustomerTable();
			}
		} catch (Exception ex) {
			ghiLogError("Lỗi khi chuẩn bị mở dialog sửa khách hàng", ex);
			JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi lấy thông tin khách hàng để sửa.", "Lỗi Hệ Thống",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void deleteCustomerAction() {
		int selectedRow = customerTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng từ bảng để xóa.", "Chưa chọn khách hàng",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			int modelRow = customerTable.convertRowIndexToModel(selectedRow);
			String customerId = (String) customerTableModel.getValueAt(modelRow, 0);
			String customerName = (String) customerTableModel.getValueAt(modelRow, 1);

			if (qlData.doesCustomerHaveActiveBookings(customerId)) {
				JOptionPane.showMessageDialog(this, "Không thể xóa khách hàng '" + customerName + "' (Mã: " + customerId
						+ ") vì họ đang có đặt phòng chưa hoàn thành (Đã xác nhận hoặc Đã check-in).\nVui lòng hoàn tất hoặc hủy các đặt phòng đó trước.",
						"Xóa Bị Chặn", JOptionPane.ERROR_MESSAGE);
				ghiLogChung("Xóa KH '" + customerId + "' thất bại do có booking hoạt động.");
				return;
			}

			int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa khách hàng '" + customerName
					+ "' (Mã: " + customerId
					+ ") không?\nLưu ý: Hành động này chỉ xóa thông tin khách hàng, các đặt phòng cũ (nếu có) sẽ không bị xóa nhưng có thể hiển thị 'Không tìm thấy KH'.",
					"Xác Nhận Xóa Khách Hàng", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

			if (confirm == JOptionPane.YES_OPTION) {
				if (qlData.deleteCustomer(customerId)) {
					ghiLogChung("Đã xóa khách hàng: " + customerId + " - " + customerName);
					refreshCustomerTable();
					refreshBookingTable(); // Cập nhật bảng booking để hiển thị "Không tìm thấy KH"
					JOptionPane.showMessageDialog(this, "Đã xóa khách hàng '" + customerName + "' thành công.",
							"Thành Công", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(this,
							"Xóa khách hàng '" + customerName + "' thất bại. Khách hàng có thể không tồn tại.",
							"Lỗi Logic", JOptionPane.ERROR_MESSAGE);
					refreshCustomerTable();
				}
			} else {
				ghiLogChung("Người dùng đã hủy thao tác xóa khách hàng '" + customerId + "'.");
			}
		} catch (Exception ex) {
			ghiLogError("Lỗi trong quá trình xóa khách hàng", ex);
			JOptionPane.showMessageDialog(this, "Có lỗi hệ thống xảy ra khi xóa khách hàng.", "Lỗi Hệ Thống",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// --- Booking Actions ---
	private void refreshBookingTable(List<Booking> bookingsToShow) {
		bookingTableModel.setRowCount(0);
		if (bookingsToShow != null) {
			for (Booking booking : bookingsToShow) {
				String customerName = qlData.findCustomerById(booking.getCustomerId()).map(Customer::getName)
						.orElse("KH#" + booking.getCustomerId().substring(0, 4) + " (Đã xóa?)");

				bookingTableModel.addRow(new Object[] { booking.getBookingId(), customerName, booking.getRoomNumber(),
						booking.getCheckInDateTime().format(DATE_TIME_FORMAT),
						booking.getCheckOutDateTime().format(DATE_TIME_FORMAT), booking.getBookingStatus(),
						String.format("%,.0f", booking.getTotalPrice()) });
			}
		}
	}

	private void refreshBookingTable() {
		refreshBookingTable(qlData.getAllBookings());
	}

	private void searchBookingsAction() {
		String keyword = bookingSearchField.getText().trim();
		Object selectedStatusItem = bookingStatusFilterCombo.getSelectedItem();
		BookingStatus statusFilter = (selectedStatusItem instanceof BookingStatus) ? (BookingStatus) selectedStatusItem
				: null;

		List<Booking> filteredBookings = qlData.searchBookings(keyword, statusFilter);
		refreshBookingTable(filteredBookings);
		ghiLogChung("Tìm kiếm đặt phòng với từ khóa: '" + keyword + "', Trạng thái: "
				+ (statusFilter != null ? statusFilter : "Tất cả"));
	}

	private void addBookingAction() {
		List<Customer> customers = qlData.getAllCustomers();
		List<Room> rooms = qlData.getAllRooms();

		if (customers.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Chưa có khách hàng nào trong hệ thống.\nVui lòng thêm khách hàng trước khi tạo đặt phòng.",
					"Thiếu Dữ Liệu", JOptionPane.WARNING_MESSAGE);
			tabbedPane.setSelectedIndex(1);
			return;
		}
		if (rooms.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Chưa có phòng nào trong hệ thống.\nVui lòng thêm phòng trước khi tạo đặt phòng.", "Thiếu Dữ Liệu",
					JOptionPane.WARNING_MESSAGE);
			tabbedPane.setSelectedIndex(0);
			return;
		}

		BookingDialog dialog = new BookingDialog(this, qlData);
		dialog.setVisible(true);
		if (dialog.isSaved()) {
			refreshBookingTable();
			// Không cần refresh roomTable ngay lập tức vì trạng thái phòng chưa đổi khi chỉ
			// mới đặt
			// refreshRoomTable();
			ghiLogChung("Đã mở dialog tạo đặt phòng và lưu thành công.");
		}
	}

	private void cancelBookingAction() {
		int selectedRow = bookingTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một đặt phòng từ bảng để hủy.", "Chưa chọn đặt phòng",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			int modelRow = bookingTable.convertRowIndexToModel(selectedRow);
			String bookingId = (String) bookingTableModel.getValueAt(modelRow, 0);
			Optional<Booking> bookingOpt = qlData.findBookingById(bookingId);

			if (bookingOpt.isPresent()) {
				Booking booking = bookingOpt.get();
				if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
					int confirm = JOptionPane.showConfirmDialog(this,
							"Bạn có chắc muốn hủy đặt phòng mã '" + bookingId + "' không?", "Xác Nhận Hủy Đặt Phòng",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

					if (confirm == JOptionPane.YES_OPTION) {
						if (qlData.cancelBooking(bookingId)) {
							ghiLogChung("Đã hủy đặt phòng: " + bookingId);
							refreshBookingTable();
							JOptionPane.showMessageDialog(this, "Đã hủy đặt phòng '" + bookingId + "' thành công.",
									"Thành Công", JOptionPane.INFORMATION_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(this, "Hủy đặt phòng '" + bookingId + "' thất bại.",
									"Lỗi Logic", JOptionPane.ERROR_MESSAGE);
						}
					} else {
						ghiLogChung("Người dùng đã hủy thao tác hủy đặt phòng '" + bookingId + "'.");
					}
				} else {
					JOptionPane.showMessageDialog(this,
							"Chỉ có thể hủy các đặt phòng đang ở trạng thái 'Đã xác nhận'.\nĐặt phòng này hiện tại là: "
									+ booking.getBookingStatus(),
							"Không Thể Hủy", JOptionPane.WARNING_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, "Không tìm thấy đặt phòng mã '" + bookingId + "'.", "Lỗi Dữ Liệu",
						JOptionPane.ERROR_MESSAGE);
				refreshBookingTable();
			}
		} catch (Exception ex) {
			ghiLogError("Lỗi trong quá trình hủy đặt phòng", ex);
			JOptionPane.showMessageDialog(this, "Có lỗi hệ thống xảy ra khi hủy đặt phòng.", "Lỗi Hệ Thống",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// --- Check-out Action (Đã implement) ---
	private void checkOutAction() {
		int selectedRow = bookingTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một đặt phòng từ bảng để check-out.",
					"Chưa Chọn Đặt Phòng", JOptionPane.WARNING_MESSAGE);
			return;
		}

		try {
			int modelRow = bookingTable.convertRowIndexToModel(selectedRow);
			String bookingId = (String) bookingTableModel.getValueAt(modelRow, 0);
			Optional<Booking> bookingOpt = qlData.findBookingById(bookingId);

			if (bookingOpt.isPresent()) {
				Booking booking = bookingOpt.get();

				// *** CHỈNH SỬA LOGIC: Cho phép check-out cả khi CONFIRMED hoặc CHECKED_IN ***
				// Điều này linh hoạt hơn, ví dụ khách đặt rồi đến thẳng quầy trả phòng luôn mà
				// không qua bước check-in trên hệ thống.
				// Hoặc đơn giản hóa quy trình.
				if (booking.getBookingStatus() != BookingStatus.CONFIRMED
						&& booking.getBookingStatus() != BookingStatus.CHECKED_IN) {
					JOptionPane.showMessageDialog(this,
							"Không thể check-out đặt phòng này.\nTrạng thái hiện tại: " + booking.getBookingStatus()
									+ "\nChỉ có thể check-out khi trạng thái là 'Đã xác nhận' hoặc 'Đã nhận phòng'.",
							"Trạng Thái Không Hợp Lệ", JOptionPane.WARNING_MESSAGE);
					return;
				}

				LocalDateTime actualCheckOutTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES); // Thời điểm
																										// check-out là
																										// bây giờ (làm
																										// tròn phút)
				String customerName = qlData.findCustomerById(booking.getCustomerId()).map(Customer::getName)
						.orElse("N/A");
				Optional<Room> roomOpt = qlData.findRoomByNumber(booking.getRoomNumber());
				String roomType = roomOpt.map(r -> r.getType().getDisplayName()).orElse("N/A");
				double pricePerNight = booking.getPricePerNightAtBooking();

				// --- Tính toán lại số đêm và tổng tiền dựa trên thời gian check-out thực tế
				// ---
				// Tạo một booking tạm để tính toán mà không ảnh hưởng booking gốc trước khi xác
				// nhận
				Booking tempBookingForCalc = new Booking(booking.getBookingId(), booking.getCustomerId(),
						booking.getRoomNumber(), booking.getCheckInDateTime(), actualCheckOutTime, pricePerNight);
				long nights = tempBookingForCalc.calculateNights();
				double finalTotalPrice = tempBookingForCalc.getTotalPrice();
				// Nếu cần logic phụ thu phức tạp, tính ở đây và cập nhật finalTotalPrice

				// --- Hiển thị hóa đơn và xác nhận ---
				String billDetails = String.format("--- HÓA ĐƠN THANH TOÁN ---\n\n" + "Mã đặt phòng: %s\n"
						+ "Khách hàng: %s (Mã KH: %s)\n" + "Phòng: %s (Loại: %s)\n" + "Giá/Đêm: %,.0f VND\n\n"
						+ "Check-in dự kiến: %s\n" + "Check-out thực tế: %s\n" + // Nhấn mạnh đây là giờ thực tế
						"Số đêm tính tiền: %d\n\n" + // Ghi rõ số đêm tính tiền
						"----------------------------------\n" + "TỔNG CỘNG: %,.0f VND\n" + // Hiển thị tổng tiền cuối
																							// cùng
						"----------------------------------\n\n" + "Xác nhận thanh toán và trả phòng?",
						booking.getBookingId(), customerName, booking.getCustomerId().substring(0, 4),
						booking.getRoomNumber(), roomType, pricePerNight,
						booking.getCheckInDateTime().format(DATE_TIME_FORMAT),
						actualCheckOutTime.format(DATE_TIME_FORMAT), nights, // Số đêm đã tính lại
						finalTotalPrice // Tổng tiền đã tính lại
				);

				int confirm = JOptionPane.showConfirmDialog(this, billDetails, "Xác Nhận Check-out và Thanh Toán",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (confirm == JOptionPane.YES_OPTION) {
					// Gọi hàm checkOutBooking trong qlData để cập nhật booking
					// Hàm này chỉ đổi trạng thái và thời gian checkout, không tính lại tiền bên
					// trong nó nữa
					boolean checkoutSuccess = qlData.checkOutBooking(bookingId, actualCheckOutTime);

					if (checkoutSuccess) {
						// Cập nhật tổng tiền cuối cùng cho booking (nếu có thay đổi)
						booking.setTotalPrice(finalTotalPrice); // Cập nhật lại total price nếu cần

						// Cập nhật trạng thái phòng thành CLEANING
						boolean roomUpdateSuccess = qlData.updateRoomStatus(booking.getRoomNumber(),
								RoomStatus.CLEANING);
						if (!roomUpdateSuccess) {
							ghiLogError("Không thể cập nhật trạng thái phòng " + booking.getRoomNumber()
									+ " thành CLEANING sau khi check-out.", null);
						}

						qlData.luuDuLieu(); // Lưu tất cả thay đổi
						refreshBookingTable(); // Cập nhật bảng booking
						refreshRoomTable(); // Cập nhật bảng phòng

						ghiLogChung("Check-out thành công cho đặt phòng: " + bookingId + " của KH: " + customerName
								+ ". Tổng tiền: " + finalTotalPrice);
						JOptionPane.showMessageDialog(this,
								"Check-out và thanh toán thành công cho đặt phòng " + bookingId + ".",
								"Check-out Thành Công", JOptionPane.INFORMATION_MESSAGE);

					} else {
						JOptionPane.showMessageDialog(this,
								"Không thể thực hiện check-out. Trạng thái đặt phòng có thể đã thay đổi.",
								"Lỗi Check-out", JOptionPane.ERROR_MESSAGE);
						refreshBookingTable();
					}
				} else {
					ghiLogChung("Người dùng đã hủy thao tác check-out cho đặt phòng: " + bookingId);
				}

			} else {
				JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin đặt phòng đã chọn (có thể đã bị xóa).",
						"Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
				refreshBookingTable();
			}
		} catch (Exception ex) {
			ghiLogError("Lỗi nghiêm trọng trong quá trình check-out", ex);
			JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không mong muốn khi thực hiện check-out.",
					"Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
		}
	}

	// --- Statistics Actions ---
	private void showRevenueAction() {
		try {
			int month = (int) monthSpinner.getValue();
			int year = (int) yearSpinner.getValue();
			YearMonth yearMonth = YearMonth.of(year, month);

			double monthlyRevenue = qlData.calculateMonthlyRevenue(yearMonth);
			double yearlyRevenue = qlData.calculateYearlyRevenue(year);

			String report = String.format("BÁO CÁO DOANH THU\n" + "=================================\n"
					+ "Tháng/Năm được chọn: %02d/%d\n\n" + "Doanh thu Tháng %02d/%d:\n   %,.0f VND\n\n"
					+ "Doanh thu Năm %d:\n   %,.0f VND\n" + "=================================\n"
					+ "(Doanh thu được tính dựa trên tổng tiền của các đặt phòng\n"
					+ " có trạng thái 'Đã trả phòng' và ngày check-out nằm trong\n" + " khoảng thời gian tương ứng.)",
					month, year, month, year, monthlyRevenue, year, yearlyRevenue);
			statisticsTextArea.setText(report);
			statisticsTextArea.setCaretPosition(0);
			ghiLogChung("Đã xem báo cáo doanh thu tháng " + month + "/" + year);
		} catch (Exception ex) {
			ghiLogError("Lỗi khi tính toán hoặc hiển thị doanh thu", ex);
			statisticsTextArea.setText("Lỗi khi tạo báo cáo doanh thu:\n" + ex.getMessage());
		}
	}

	private void showCustomerListAction() {
		try {
			List<Customer> customers = qlData.getAllCustomers();
			StringBuilder report = new StringBuilder();
			report.append("DANH SÁCH KHÁCH HÀNG (" + customers.size() + " khách)\n");
			report.append("===================================================================\n");
			report.append(
					String.format("%-10s | %-25s | %-15s | %-15s\n", "Mã KH", "Họ Tên", "CMND/CCCD", "Số Điện Thoại"));
			report.append("-------------------------------------------------------------------\n");
			if (customers.isEmpty()) {
				report.append("                   Không có khách hàng nào.\n");
			} else {
				for (Customer c : customers) {
					report.append(String.format("%-10s | %-25s | %-15s | %-15s\n", c.getCustomerId(),
							c.getName().length() > 25 ? c.getName().substring(0, 22) + "..." : c.getName(),
							c.getIdCard(), c.getPhoneNumber()));
				}
			}
			report.append("===================================================================\n");
			statisticsTextArea.setText(report.toString());
			statisticsTextArea.setCaretPosition(0);
			ghiLogChung("Đã xem danh sách khách hàng.");
		} catch (Exception ex) {
			ghiLogError("Lỗi khi lấy hoặc hiển thị danh sách khách hàng", ex);
			statisticsTextArea.setText("Lỗi khi tạo danh sách khách hàng:\n" + ex.getMessage());
		}
	}

	private void showRoomListAction() {
		try {
			List<Room> rooms = qlData.getAllRooms();
			StringBuilder report = new StringBuilder();
			report.append("DANH SÁCH PHÒNG (" + rooms.size() + " phòng)\n");
			report.append("=================================================================\n");
			report.append(String.format("%-10s | %-15s | %-15s | %-15s\n", "Số Phòng", "Loại Phòng", "Giá/Đêm (VND)",
					"Trạng Thái"));
			report.append("-----------------------------------------------------------------\n");
			if (rooms.isEmpty()) {
				report.append("                     Không có phòng nào.\n");
			} else {
				for (Room r : rooms) {
					report.append(String.format("%-10s | %-15s | %,14.0f | %-15s\n", r.getRoomNumber(), r.getType(),
							r.getPricePerNight(), r.getStatus()));
				}
			}
			report.append("=================================================================\n");
			statisticsTextArea.setText(report.toString());
			statisticsTextArea.setCaretPosition(0);
			ghiLogChung("Đã xem danh sách phòng.");
		} catch (Exception ex) {
			ghiLogError("Lỗi khi lấy hoặc hiển thị danh sách phòng", ex);
			statisticsTextArea.setText("Lỗi khi tạo danh sách phòng:\n" + ex.getMessage());
		}
	}

	// --- Logging Methods ---
	private void ghiLogChung(String message) {
		if (LOG_FILE_PATH == null || LOG_FILE_PATH.trim().isEmpty()) {
			System.out.println("[INFO] Log: " + message);
			return;
		}
		try (FileWriter fw = new FileWriter(LOG_FILE_PATH, true); PrintWriter pw = new PrintWriter(fw)) {
			pw.println("[" + LocalDateTime.now().format(LOG_DATE_FORMAT) + "] [INFO] " + message);
		} catch (IOException e) {
			System.err.println("CRITICAL: Không thể ghi log chung vào file '" + LOG_FILE_PATH + "': " + e.getMessage());
		}
	}

	private void ghiLogError(String message, Exception ex) {
		if (LOG_FILE_PATH == null || LOG_FILE_PATH.trim().isEmpty()) {
			System.err.println("[ERROR] Log: " + message);
			if (ex != null)
				ex.printStackTrace(System.err);
			return;
		}
		try (FileWriter fw = new FileWriter(LOG_FILE_PATH, true); PrintWriter pw = new PrintWriter(fw)) {
			pw.println("[" + LocalDateTime.now().format(LOG_DATE_FORMAT) + "] [ERROR] " + message);
			if (ex != null) {
				ex.printStackTrace(pw);
			}
		} catch (IOException e) {
			System.err.println("CRITICAL: Không thể ghi log lỗi vào file '" + LOG_FILE_PATH + "': " + e.getMessage());
			System.err.println("----- Lỗi Gốc -----");
			System.err.println("[" + LocalDateTime.now().format(LOG_DATE_FORMAT) + "] [ERROR] " + message);
			if (ex != null)
				ex.printStackTrace(System.err);
			System.err.println("----- Kết thúc lỗi gốc -----");
		}
	}

	// --- Main Method ---
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Không thể cài đặt Look and Feel hệ thống. Sử dụng giao diện mặc định.");
		}

		SwingUtilities.invokeLater(() -> {
			new KhachSanProGUI();
		});
	}
}

// ========================================================================
// Enum Definitions
// ========================================================================
enum RoomType {
	SINGLE("Phòng Đơn"), DOUBLE("Phòng Đôi"), SUITE("Phòng Suite"), FAMILY("Phòng Gia Đình");

	private final String displayName;

	RoomType(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}

enum RoomStatus {
	AVAILABLE("Sẵn sàng"), OCCUPIED("Đang có khách"), CLEANING("Đang dọn dẹp"), MAINTENANCE("Bảo trì");

	private final String displayName;

	RoomStatus(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}

enum BookingStatus {
	CONFIRMED("Đã xác nhận"), CHECKED_IN("Đã nhận phòng"), CHECKED_OUT("Đã trả phòng"), CANCELLED("Đã hủy");

	private final String displayName;

	BookingStatus(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
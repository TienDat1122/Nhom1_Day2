import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

class BookingDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JComboBox<Customer> customerCombo;
	private JComboBox<Room> roomCombo;
	private JTextField checkInField, checkOutField;
	private JButton saveButton, cancelButton, checkAvailabilityButton;
	private JLabel priceLabel;
	private boolean saved = false;
	private QuanLyKhachSanData qlData;
	private final DateTimeFormatter formatter = KhachSanProGUI.DATE_TIME_FORMAT;

	public BookingDialog(Frame owner, QuanLyKhachSanData qlData) {
		super(owner, "Tạo Đặt Phòng Mới", true);
		this.qlData = qlData;
		initComponents();
		pack();
		setLocationRelativeTo(owner);
	}

	private void initComponents() {
		setLayout(new BorderLayout(10, 10));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));
		JPanel inputPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		inputPanel.add(new JLabel("Chọn Khách Hàng:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		customerCombo = new JComboBox<>(new Vector<>(qlData.getAllCustomers()));
		inputPanel.add(customerCombo, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		inputPanel.add(new JLabel("Check-in (dd/MM/yyyy HH:mm):"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		checkInField = new JTextField(16);
		checkInField.setText(LocalDateTime.now().plusMinutes(15).truncatedTo(ChronoUnit.MINUTES).format(formatter));
		inputPanel.add(checkInField, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		inputPanel.add(new JLabel("Check-out (dd/MM/yyyy HH:mm):"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		checkOutField = new JTextField(16);
		try {
			LocalDateTime suggestedCheckIn = LocalDateTime.parse(checkInField.getText(), formatter);
			checkOutField.setText(suggestedCheckIn.plusDays(1).format(formatter));
		} catch (Exception e) {
			checkOutField.setText(
					LocalDateTime.now().plusDays(1).plusMinutes(15).truncatedTo(ChronoUnit.MINUTES).format(formatter));
		}
		inputPanel.add(checkOutField, gbc);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 3;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		checkAvailabilityButton = new JButton("Kiểm Tra & Hiển Thị Phòng Trống");
		inputPanel.add(checkAvailabilityButton, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		inputPanel.add(new JLabel("Chọn Phòng Trống:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		roomCombo = new JComboBox<Room>();
		roomCombo.setEnabled(false);
		inputPanel.add(roomCombo, gbc);
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		inputPanel.add(new JLabel("Giá/đêm:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		priceLabel = new JLabel("Vui lòng kiểm tra và chọn phòng");
		priceLabel.setFont(priceLabel.getFont().deriveFont(Font.ITALIC));
		inputPanel.add(priceLabel, gbc);
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		saveButton = new JButton("Lưu Đặt Phòng");
		saveButton.setEnabled(false);
		cancelButton = new JButton("Hủy");
		buttonPanel.add(saveButton);
		buttonPanel.add(cancelButton);
		checkAvailabilityButton.addActionListener(e -> checkAndLoadAvailableRooms());
		roomCombo.addActionListener(e -> updatePriceInfo());
		saveButton.addActionListener(e -> saveBooking());
		cancelButton.addActionListener(e -> dispose());
		add(inputPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	private LocalDateTime parseDateTime(String text, String fieldName) {
		if (text == null || text.trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, fieldName + " không trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		try {
			return LocalDateTime.parse(text.trim(), formatter);
		} catch (DateTimeParseException e) {
			JOptionPane.showMessageDialog(this, "Định dạng Ngày/Giờ " + fieldName + " sai.\nPhải là: dd/MM/yyyy HH:mm",
					"Lỗi", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	private void checkAndLoadAvailableRooms() {
		LocalDateTime checkIn = parseDateTime(checkInField.getText(), "Check-in");
		LocalDateTime checkOut = parseDateTime(checkOutField.getText(), "Check-out");
		if (checkIn == null || checkOut == null) {
			roomCombo.setEnabled(false);
			saveButton.setEnabled(false);
			priceLabel.setText("Nhập thời gian hợp lệ");
			return;
		}
		if (!checkOut.isAfter(checkIn)) {
			JOptionPane.showMessageDialog(this, "Check-out phải sau Check-in.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			roomCombo.setEnabled(false);
			saveButton.setEnabled(false);
			priceLabel.setText("Thời gian không lệ");
			return;
		}
		List<Room> availableRooms = qlData.getAllRooms().stream()
				.filter(room -> room.getStatus() == RoomStatus.AVAILABLE || room.getStatus() == RoomStatus.CLEANING)
				.filter(room -> qlData.isRoomAvailable(room.getRoomNumber(), checkIn, checkOut))
				.collect(Collectors.toList());
		roomCombo.removeAllItems();
		if (availableRooms.isEmpty()) {
			roomCombo.addItem(null);
			roomCombo.setRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList<?> list, Object value, int index,
						boolean isSelected, boolean cellHasFocus) {
					JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
							cellHasFocus);
					label.setText("--- Không có phòng trống ---");
					label.setEnabled(false);
					return label;
				}
			});
			roomCombo.setEnabled(false);
			saveButton.setEnabled(false);
			priceLabel.setText("N/A");
			JOptionPane.showMessageDialog(this, "Không có phòng trống trong thời gian đã chọn.", "Thông Báo",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			roomCombo.setRenderer(new DefaultListCellRenderer());
			availableRooms.forEach(roomCombo::addItem);
			roomCombo.setEnabled(true);
			saveButton.setEnabled(true);
			updatePriceInfo();
			JOptionPane.showMessageDialog(this, "Đã tìm thấy " + availableRooms.size() + " phòng trống.", "Thông Báo",
					JOptionPane.INFORMATION_MESSAGE);
		}
		pack();
	}

	private void updatePriceInfo() {
		Object selectedItem = roomCombo.getSelectedItem();
		if (selectedItem instanceof Room) {
			Room selectedRoom = (Room) selectedItem;
			priceLabel.setText(String.format("%,.0f VND/đêm", selectedRoom.getPricePerNight()));
			priceLabel.setFont(priceLabel.getFont().deriveFont(Font.PLAIN));
		} else {
			priceLabel.setText("Vui lòng chọn phòng");
			priceLabel.setFont(priceLabel.getFont().deriveFont(Font.ITALIC));
		}
	}

	private void saveBooking() {
		Customer selectedCustomer = (Customer) customerCombo.getSelectedItem();
		Object selectedRoomObj = roomCombo.getSelectedItem();
		LocalDateTime checkIn = parseDateTime(checkInField.getText(), "Check-in");
		LocalDateTime checkOut = parseDateTime(checkOutField.getText(), "Check-out");
		if (selectedCustomer == null) {
			JOptionPane.showMessageDialog(this, "Chọn khách hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			customerCombo.requestFocus();
			return;
		}
		if (!(selectedRoomObj instanceof Room)) {
			JOptionPane.showMessageDialog(this, "Chọn phòng hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			if (!roomCombo.isEnabled())
				checkAvailabilityButton.requestFocus();
			else
				roomCombo.requestFocus();
			return;
		}
		Room selectedRoom = (Room) selectedRoomObj;
		if (checkIn == null || checkOut == null)
			return;
		if (!checkOut.isAfter(checkIn)) {
			JOptionPane.showMessageDialog(this, "Check-out phải sau Check-in.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			checkOutField.requestFocus();
			return;
		}
		if (!qlData.isRoomAvailable(selectedRoom.getRoomNumber(), checkIn, checkOut)) {
			JOptionPane.showMessageDialog(this,
					"Phòng '" + selectedRoom.getRoomNumber() + "' không còn trống. Vui lòng kiểm tra lại.", "Lỗi",
					JOptionPane.WARNING_MESSAGE);
			checkAndLoadAvailableRooms();
			saveButton.setEnabled(false);
			return;
		}
		try {
			String bookingId = qlData.addBooking(selectedCustomer.getCustomerId(), selectedRoom.getRoomNumber(),
					checkIn, checkOut);
			if (bookingId != null) {
				saved = true;
				qlData.luuDuLieu();
				dispose();
			} else {
				JOptionPane.showMessageDialog(this, "Không thể tạo đặt phòng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi hệ thống khi lưu đặt phòng:\n" + ex.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	public boolean isSaved() {
		return saved;
	}
}
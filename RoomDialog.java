
// ========================================================================
// Dialog Classes
// ========================================================================

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

class RoomDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTextField roomNumberField, priceField;
	private JComboBox<RoomType> typeCombo;
	private JComboBox<RoomStatus> statusCombo;
	private JButton saveButton, cancelButton;
	private boolean saved = false;
	private QuanLyKhachSanData qlData;
	private Room roomToEdit;

	public RoomDialog(Frame owner, QuanLyKhachSanData qlData, Room roomToEdit) {
		super(owner, (roomToEdit == null ? "Thêm Phòng Mới" : "Sửa Thông Tin Phòng"), true);
		this.qlData = qlData;
		this.roomToEdit = roomToEdit;
		initComponents();
		pack();
		setLocationRelativeTo(owner);
		if (roomToEdit != null)
			populateFields();
		else
			statusCombo.setSelectedItem(RoomStatus.AVAILABLE);
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
		inputPanel.add(new JLabel("Số Phòng:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		roomNumberField = new JTextField(15);
		inputPanel.add(roomNumberField, gbc);
		gbc.weightx = 0.0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		inputPanel.add(new JLabel("Loại Phòng:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		typeCombo = new JComboBox<>(RoomType.values());
		inputPanel.add(typeCombo, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		inputPanel.add(new JLabel("Giá/Đêm (VND):"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		priceField = new JTextField(15);
		inputPanel.add(priceField, gbc);
		gbc.gridx = 0;
		gbc.gridy = 3;
		inputPanel.add(new JLabel("Trạng Thái:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 3;
		statusCombo = new JComboBox<>(RoomStatus.values());
		inputPanel.add(statusCombo, gbc);
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		saveButton = new JButton("Lưu");
		cancelButton = new JButton("Hủy");
		buttonPanel.add(saveButton);
		buttonPanel.add(cancelButton);
		saveButton.addActionListener(e -> saveRoom());
		cancelButton.addActionListener(e -> dispose());
		add(inputPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	private void populateFields() {
		roomNumberField.setText(roomToEdit.getRoomNumber());
		roomNumberField.setEditable(false);
		typeCombo.setSelectedItem(roomToEdit.getType());
		priceField.setText(String.valueOf(roomToEdit.getPricePerNight()));
		statusCombo.setSelectedItem(roomToEdit.getStatus());
	}

	private void saveRoom() {
		String roomNumber = roomNumberField.getText().trim();
		if (roomNumber.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Số phòng không được để trống.", "Lỗi Nhập Liệu",
					JOptionPane.ERROR_MESSAGE);
			roomNumberField.requestFocus();
			return;
		}
		RoomType selectedType = (RoomType) typeCombo.getSelectedItem();
		if (selectedType == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn loại phòng.", "Lỗi Nhập Liệu",
					JOptionPane.ERROR_MESSAGE);
			typeCombo.requestFocus();
			return;
		}
		double price;
		try {
			price = Double.parseDouble(priceField.getText().trim());
			if (price <= 0) {
				JOptionPane.showMessageDialog(this, "Giá phòng phải là một số dương.", "Lỗi Nhập Liệu",
						JOptionPane.ERROR_MESSAGE);
				priceField.requestFocus();
				return;
			}
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Giá phòng phải là một số hợp lệ.", "Lỗi Nhập Liệu",
					JOptionPane.ERROR_MESSAGE);
			priceField.requestFocus();
			return;
		}
		RoomStatus selectedStatus = (RoomStatus) statusCombo.getSelectedItem();
		if (selectedStatus == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn trạng thái phòng.", "Lỗi Nhập Liệu",
					JOptionPane.ERROR_MESSAGE);
			statusCombo.requestFocus();
			return;
		}
		try {
			if (roomToEdit == null) {
				if (qlData.findRoomByNumber(roomNumber).isPresent()) {
					JOptionPane.showMessageDialog(this, "Số phòng '" + roomNumber + "' đã tồn tại.", "Lỗi Trùng Lặp",
							JOptionPane.ERROR_MESSAGE);
					roomNumberField.requestFocus();
					return;
				}
				Room newRoom = new Room(roomNumber, selectedType, price);
				newRoom.setStatus(selectedStatus);
				if (qlData.addRoom(newRoom)) {
					saved = true;
					qlData.luuDuLieu();
					dispose();
				} else {
					JOptionPane.showMessageDialog(this, "Không thể thêm phòng.", "Lỗi Hệ Thống",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				roomToEdit.setType(selectedType);
				roomToEdit.setPricePerNight(price);
				roomToEdit.setStatus(selectedStatus);
				if (qlData.updateRoom(roomToEdit)) {
					saved = true;
					qlData.luuDuLieu();
					dispose();
				} else {
					JOptionPane.showMessageDialog(this, "Không thể cập nhật phòng.", "Lỗi Hệ Thống",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi hệ thống khi lưu phòng:\n" + ex.getMessage(), "Lỗi Hệ Thống",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	public boolean isSaved() {
		return saved;
	}
}
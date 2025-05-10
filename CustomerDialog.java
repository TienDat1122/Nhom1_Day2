import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

class CustomerDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTextField customerIdField, nameField, idCardField, phoneField;
	private JButton saveButton, cancelButton;
	private boolean saved = false;
	private QuanLyKhachSanData qlData;
	private Customer customerToEdit;

	public CustomerDialog(Frame owner, QuanLyKhachSanData qlData, Customer customerToEdit) {
		super(owner, (customerToEdit == null ? "Thêm Khách Hàng Mới" : "Sửa Thông Tin Khách Hàng"), true);
		this.qlData = qlData;
		this.customerToEdit = customerToEdit;
		initComponents();
		pack();
		setLocationRelativeTo(owner);
		if (customerToEdit != null)
			populateFields();
		else
			customerIdField.setText(QuanLyKhachSanData.generateId());
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
		inputPanel.add(new JLabel("Mã KH:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		customerIdField = new JTextField(15);
		customerIdField.setEditable(false);
		inputPanel.add(customerIdField, gbc);
		gbc.weightx = 0.0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		inputPanel.add(new JLabel("Họ Tên:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		nameField = new JTextField(25);
		inputPanel.add(nameField, gbc);
		gbc.weightx = 0.0;
		gbc.gridx = 0;
		gbc.gridy = 2;
		inputPanel.add(new JLabel("CMND/CCCD:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.weightx = 1.0;
		idCardField = new JTextField(15);
		inputPanel.add(idCardField, gbc);
		gbc.weightx = 0.0;
		gbc.gridx = 0;
		gbc.gridy = 3;
		inputPanel.add(new JLabel("Số Điện Thoại:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.weightx = 1.0;
		phoneField = new JTextField(15);
		inputPanel.add(phoneField, gbc);
		gbc.weightx = 0.0;
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		saveButton = new JButton("Lưu");
		cancelButton = new JButton("Hủy");
		buttonPanel.add(saveButton);
		buttonPanel.add(cancelButton);
		saveButton.addActionListener(e -> saveCustomer());
		cancelButton.addActionListener(e -> dispose());
		add(inputPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	private void populateFields() {
		customerIdField.setText(customerToEdit.getCustomerId());
		nameField.setText(customerToEdit.getName());
		idCardField.setText(customerToEdit.getIdCard());
		phoneField.setText(customerToEdit.getPhoneNumber());
	}

	private void saveCustomer() {
		String custId = customerIdField.getText();
		String name = nameField.getText().trim();
		String idCard = idCardField.getText().trim();
		String phone = phoneField.getText().trim();
		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Tên KH không trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			nameField.requestFocus();
			return;
		}
		if (idCard.isEmpty()) {
			JOptionPane.showMessageDialog(this, "CMND/CCCD không trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			idCardField.requestFocus();
			return;
		}
		if (phone.isEmpty()) {
			JOptionPane.showMessageDialog(this, "SĐT không trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			phoneField.requestFocus();
			return;
		}
		if (!phone.matches("\\d+")) {
			JOptionPane.showMessageDialog(this, "SĐT chỉ chứa số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			phoneField.requestFocus();
			return;
		}
		try {
			if (customerToEdit == null) {
				Optional<Customer> existingCust = qlData.findCustomerByIdCard(idCard);
				if (existingCust.isPresent()) {
					JOptionPane.showMessageDialog(this,
							"CMND '" + idCard + "' đã tồn tại cho KH:\n" + existingCust.get().getName(), "Lỗi",
							JOptionPane.ERROR_MESSAGE);
					idCardField.requestFocus();
					return;
				}
				Customer newCustomer = new Customer(custId, name, idCard, phone);
				if (qlData.addCustomer(newCustomer)) {
					saved = true;
					qlData.luuDuLieu();
					dispose();
				} else {
					JOptionPane.showMessageDialog(this, "Không thể thêm KH.", "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				if (!customerToEdit.getIdCard().equalsIgnoreCase(idCard)) {
					Optional<Customer> otherCust = qlData.findCustomerByIdCard(idCard);
					if (otherCust.isPresent()
							&& !otherCust.get().getCustomerId().equals(customerToEdit.getCustomerId())) {
						JOptionPane.showMessageDialog(this,
								"CMND '" + idCard + "' đã tồn tại cho KH khác:\n" + otherCust.get().getName(), "Lỗi",
								JOptionPane.ERROR_MESSAGE);
						idCardField.requestFocus();
						return;
					}
				}
				customerToEdit.setName(name);
				customerToEdit.setIdCard(idCard);
				customerToEdit.setPhoneNumber(phone);
				if (qlData.updateCustomer(customerToEdit)) {
					saved = true;
					qlData.luuDuLieu();
					dispose();
				} else {
					JOptionPane.showMessageDialog(this, "Không thể cập nhật KH.", "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi hệ thống khi lưu KH:\n" + ex.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	public boolean isSaved() {
		return saved;
	}
}
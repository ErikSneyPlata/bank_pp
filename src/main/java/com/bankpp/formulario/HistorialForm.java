package com.bankpp.formulario;
import com.bankpp.model.*;import com.bankpp.repository.impl.*;import com.bankpp.service.*;import javax.swing.*;import javax.swing.table.DefaultTableModel;import java.awt.BorderLayout;
public final class HistorialForm extends JPanel {
    private JPanel panelPrincipal; private JComboBox<CuentaBancaria> cmbCuenta; private JTable tblMovimientos; private JButton btnActualizar; private JLabel lblEstado;
    private final Usuario usuario; private final CuentaService cuentas = new CuentaService(new CuentaRepositoryJdbc()); private final TransaccionService transacciones = new TransaccionService(new TransaccionRepositoryJdbc());
    public HistorialForm(Usuario usuario) {
        this.usuario = usuario; if (panelPrincipal == null || cmbCuenta == null || tblMovimientos == null || btnActualizar == null || lblEstado == null) crearInterfaz(); setLayout(new BorderLayout()); add(panelPrincipal, BorderLayout.CENTER);
        tblMovimientos.setModel(new DefaultTableModel(new Object[]{"Fecha", "Tipo", "Monto", "Descripción"}, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } });
        try { for (CuentaBancaria c : cuentas.listarCuentas(usuario.getId())) cmbCuenta.addItem(c); lblEstado.setText(cmbCuenta.getItemCount() == 0 ? "No hay cuenta aprobada para consultar movimientos." : "Selecciona una cuenta."); } catch (RuntimeException e) { lblEstado.setText("No se pudieron cargar las cuentas."); btnActualizar.setEnabled(false); }
        btnActualizar.addActionListener(e -> cargarMovimientos());
    }
    private void crearInterfaz() { panelPrincipal = new JPanel(new BorderLayout(8, 8)); cmbCuenta = new JComboBox<>(); tblMovimientos = new JTable(); btnActualizar = new JButton("Actualizar movimientos"); lblEstado = new JLabel(" "); JPanel superior = new JPanel(new BorderLayout(8,8)); superior.add(new JLabel("Cuenta:"), BorderLayout.WEST); superior.add(cmbCuenta, BorderLayout.CENTER); superior.add(btnActualizar, BorderLayout.EAST); JPanel norte = new JPanel(new BorderLayout()); norte.add(lblEstado, BorderLayout.NORTH); norte.add(superior, BorderLayout.CENTER); panelPrincipal.add(norte, BorderLayout.NORTH); panelPrincipal.add(new JScrollPane(tblMovimientos), BorderLayout.CENTER); }
    private void cargarMovimientos() { try { CuentaBancaria c = (CuentaBancaria) cmbCuenta.getSelectedItem(); if (c == null) return; DefaultTableModel m = (DefaultTableModel) tblMovimientos.getModel(); m.setRowCount(0); for (Transaccion t : transacciones.consultarMovimientos(c.getId(), usuario.getId())) m.addRow(new Object[]{t.getFecha(), t.getTipo(), t.getMonto(), t.getDescripcion()}); } catch (RuntimeException e) { JOptionPane.showMessageDialog(this, e.getMessage(), "Historial", JOptionPane.ERROR_MESSAGE); } }
}

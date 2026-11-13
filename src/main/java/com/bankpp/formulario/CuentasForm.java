package com.bankpp.formulario;
import com.bankpp.model.*;import com.bankpp.model.enums.EstadoSolicitudCuenta;import com.bankpp.model.enums.RolUsuario;import com.bankpp.repository.impl.CuentaRepositoryJdbc;import com.bankpp.repository.impl.SolicitudCuentaRepositoryJdbc;import com.bankpp.service.CuentaService;import com.bankpp.service.SolicitudCuentaService;import javax.swing.*;import javax.swing.table.DefaultTableModel;import java.awt.BorderLayout;
public final class CuentasForm extends JPanel {
    private JPanel panelPrincipal; private JTable tblCuentas; private JButton btnActualizar, btnSolicitar; private JLabel lblEstado;
    private final Usuario usuario; private final CuentaService servicio = new CuentaService(new CuentaRepositoryJdbc());
    private final SolicitudCuentaService solicitudes = new SolicitudCuentaService(new SolicitudCuentaRepositoryJdbc());
    public CuentasForm(Usuario usuario) {
        this.usuario = usuario; if (panelPrincipal == null || tblCuentas == null || btnActualizar == null || btnSolicitar == null || lblEstado == null) crearInterfaz();
        setLayout(new BorderLayout()); add(panelPrincipal, BorderLayout.CENTER);
        tblCuentas.setModel(new DefaultTableModel(new Object[]{"Número", "Saldo", "Moneda", "Estado"}, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } });
        btnActualizar.addActionListener(e -> cargarCuentas()); btnSolicitar.addActionListener(e -> solicitarCuenta()); cargarCuentas();
    }
    private void crearInterfaz() { panelPrincipal = new JPanel(new BorderLayout(8, 8)); tblCuentas = new JTable(); lblEstado = new JLabel(" "); btnActualizar = new JButton("Actualizar cuentas"); btnSolicitar = new JButton("Solicitar apertura de cuenta"); JPanel superior = new JPanel(new BorderLayout(8, 8)); superior.add(lblEstado, BorderLayout.CENTER); superior.add(btnSolicitar, BorderLayout.EAST); panelPrincipal.add(superior, BorderLayout.NORTH); panelPrincipal.add(new JScrollPane(tblCuentas), BorderLayout.CENTER); JPanel inferior = new JPanel(new BorderLayout()); inferior.add(btnActualizar, BorderLayout.EAST); panelPrincipal.add(inferior, BorderLayout.SOUTH); }
    private void cargarCuentas() {
        try {
            DefaultTableModel m = (DefaultTableModel) tblCuentas.getModel(); m.setRowCount(0);
            var lista = servicio.listarCuentas(usuario.getId());
            for (CuentaBancaria c : lista) m.addRow(new Object[]{c.getNumero(), c.getSaldo(), c.getMoneda(), c.getEstado()});
            if (!lista.isEmpty()) {
                lblEstado.setText("Tu cuenta está abierta."); btnSolicitar.setEnabled(false); return;
            }
            if (usuario.getRol() != RolUsuario.CLIENTE) {
                lblEstado.setText("Este panel es para clientes."); btnSolicitar.setEnabled(false); return;
            }
            var ultima = solicitudes.consultarUltima(usuario);
            if (ultima.isPresent() && ultima.get().getEstado() == EstadoSolicitudCuenta.PENDIENTE) {
                lblEstado.setText("Solicitud pendiente de aprobación por un empleado."); btnSolicitar.setEnabled(false);
            } else if (ultima.isPresent() && ultima.get().getEstado() == EstadoSolicitudCuenta.RECHAZADA) {
                lblEstado.setText("La solicitud anterior fue rechazada. Puedes solicitar de nuevo."); btnSolicitar.setEnabled(true);
            } else {
                lblEstado.setText("Aún no tienes una cuenta abierta."); btnSolicitar.setEnabled(true);
            }
        } catch (RuntimeException e) { lblEstado.setText("No se pudieron cargar las cuentas."); btnSolicitar.setEnabled(false); JOptionPane.showMessageDialog(this, e.getMessage(), "Cuentas", JOptionPane.ERROR_MESSAGE); }
    }
    private void solicitarCuenta() {
        try { solicitudes.solicitar(usuario); cargarCuentas(); }
        catch (RuntimeException e) { JOptionPane.showMessageDialog(this, e.getMessage(), "Solicitud de cuenta", JOptionPane.ERROR_MESSAGE); }
    }
}

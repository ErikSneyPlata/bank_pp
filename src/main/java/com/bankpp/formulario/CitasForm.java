package com.bankpp.formulario;
import com.bankpp.model.*;import com.bankpp.repository.impl.AtencionRepositoryJdbc;import com.bankpp.service.AtencionService;import javax.swing.*;import javax.swing.table.DefaultTableModel;import java.awt.BorderLayout;import java.awt.GridLayout;import java.time.*;
public final class CitasForm extends JPanel {
    private JPanel panelPrincipal; private JTextField txtFechaHora, txtMotivo; private JTable tblCitas; private JButton btnSolicitar, btnCancelar, btnActualizar;
    private final Usuario usuario; private final AtencionService servicio = new AtencionService(new AtencionRepositoryJdbc());
    public CitasForm(Usuario usuario) {
        this.usuario = usuario; if (panelPrincipal == null) crearInterfaz(); setLayout(new BorderLayout()); add(panelPrincipal, BorderLayout.CENTER);
        tblCitas.setModel(new DefaultTableModel(new Object[]{"ID", "Fecha y hora", "Estado", "Motivo"}, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } });
        btnSolicitar.addActionListener(e -> solicitar()); btnCancelar.addActionListener(e -> cancelar()); btnActualizar.addActionListener(e -> cargarCitas()); cargarCitas();
    }
    private void crearInterfaz() { panelPrincipal = new JPanel(new BorderLayout(8, 8)); txtFechaHora = new JTextField(); txtMotivo = new JTextField(); tblCitas = new JTable(); btnSolicitar = new JButton("Solicitar cita"); btnCancelar = new JButton("Cancelar seleccionada"); btnActualizar = new JButton("Actualizar citas"); JPanel datos = new JPanel(new GridLayout(0, 2, 8, 8)); datos.add(new JLabel("Fecha y hora (aaaa-MM-ddTHH:mm):")); datos.add(txtFechaHora); datos.add(new JLabel("Motivo:")); datos.add(txtMotivo); JPanel botones = new JPanel(); botones.add(btnSolicitar); botones.add(btnCancelar); botones.add(btnActualizar); JPanel norte = new JPanel(new BorderLayout()); norte.add(datos, BorderLayout.CENTER); norte.add(botones, BorderLayout.SOUTH); panelPrincipal.add(norte, BorderLayout.NORTH); panelPrincipal.add(new JScrollPane(tblCitas), BorderLayout.CENTER); }
    private void solicitar() { try { LocalDateTime fecha = LocalDateTime.parse(txtFechaHora.getText().trim()); servicio.solicitarCita(usuario.getId(), fecha.atZone(ZoneId.systemDefault()).toInstant(), txtMotivo.getText()); cargarCitas(); } catch (RuntimeException e) { error(e); } }
    private void cancelar() { try { int row = tblCitas.getSelectedRow(); if (row < 0) throw new IllegalArgumentException("Selecciona una cita"); servicio.cancelarCita((java.util.UUID) tblCitas.getValueAt(row, 0), usuario.getId()); cargarCitas(); } catch (RuntimeException e) { error(e); } }
    private void cargarCitas() { try { DefaultTableModel m = (DefaultTableModel) tblCitas.getModel(); m.setRowCount(0); for (Cita c : servicio.listarCitas(usuario.getId())) m.addRow(new Object[]{c.getId(), c.getFechaHora(), c.getEstado(), c.getMotivo()}); } catch (RuntimeException e) { error(e); } }
    private void error(Exception e) { JOptionPane.showMessageDialog(this, "Usa fecha ISO: aaaa-MM-ddTHH:mm. " + e.getMessage(), "Citas", JOptionPane.ERROR_MESSAGE); }
}

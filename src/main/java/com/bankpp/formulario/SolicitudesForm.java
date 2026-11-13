package com.bankpp.formulario;
import com.bankpp.model.*;import com.bankpp.repository.impl.AtencionRepositoryJdbc;import com.bankpp.service.AtencionService;import javax.swing.*;import javax.swing.table.DefaultTableModel;import java.awt.BorderLayout;
public final class SolicitudesForm extends JPanel {
    private JPanel panelPrincipal; private JTextArea txtDescripcion; private JTable tblSolicitudes; private JButton btnEnviar, btnActualizar;
    private final Usuario usuario; private final AtencionService servicio = new AtencionService(new AtencionRepositoryJdbc());
    public SolicitudesForm(Usuario usuario) {
        this.usuario = usuario; if (panelPrincipal == null) crearInterfaz(); setLayout(new BorderLayout()); add(panelPrincipal, BorderLayout.CENTER);
        tblSolicitudes.setModel(new DefaultTableModel(new Object[]{"ID", "Descripción", "Estado"}, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } });
        btnEnviar.addActionListener(e -> crearSolicitud()); btnActualizar.addActionListener(e -> cargarSolicitudes()); cargarSolicitudes();
    }
    private void crearInterfaz() { panelPrincipal = new JPanel(new BorderLayout(8, 8)); txtDescripcion = new JTextArea(4, 30); tblSolicitudes = new JTable(); btnEnviar = new JButton("Enviar solicitud"); btnActualizar = new JButton("Actualizar solicitudes"); JPanel editor = new JPanel(new BorderLayout(8, 8)); editor.add(new JLabel("Descripción:"), BorderLayout.NORTH); editor.add(new JScrollPane(txtDescripcion), BorderLayout.CENTER); JPanel botones = new JPanel(); botones.add(btnEnviar); botones.add(btnActualizar); editor.add(botones, BorderLayout.SOUTH); panelPrincipal.add(editor, BorderLayout.NORTH); panelPrincipal.add(new JScrollPane(tblSolicitudes), BorderLayout.CENTER); }
    private void crearSolicitud() { try { servicio.crearSolicitud(usuario.getId(), txtDescripcion.getText()); txtDescripcion.setText(""); cargarSolicitudes(); } catch (RuntimeException e) { error(e); } }
    private void cargarSolicitudes() { try { DefaultTableModel m = (DefaultTableModel) tblSolicitudes.getModel(); m.setRowCount(0); for (SolicitudAtencion s : servicio.listarSolicitudes(usuario.getId())) m.addRow(new Object[]{s.getId(), s.getDescripcion(), s.getEstado()}); } catch (RuntimeException e) { error(e); } }
    private void error(Exception e) { JOptionPane.showMessageDialog(this, e.getMessage(), "Solicitudes", JOptionPane.ERROR_MESSAGE); }
}

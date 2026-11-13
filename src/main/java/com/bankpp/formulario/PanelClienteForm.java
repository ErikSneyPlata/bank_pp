package com.bankpp.formulario;
import com.bankpp.model.Usuario;import com.bankpp.service.AutenticacionService;import javax.swing.*;
import java.awt.BorderLayout;
public final class PanelClienteForm extends JFrame {
    private JPanel panelPrincipal; private JTabbedPane tabPrincipal; private JLabel lblBienvenida; private JButton btnCerrarSesion;
    private final Usuario usuario; private final AutenticacionService autenticacion;
    public PanelClienteForm(Usuario usuario, AutenticacionService autenticacion) {
        this.usuario = usuario; this.autenticacion = autenticacion;
        if (panelPrincipal == null) crearInterfaz();
        setTitle("Bank PP - Panel principal"); setContentPane(panelPrincipal); setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(940, 680); setLocationRelativeTo(null); lblBienvenida.setText("Bienvenido, " + usuario.getNombres() + " " + usuario.getApellidos());
        tabPrincipal.addTab("Cuentas", new CuentasForm(usuario)); tabPrincipal.addTab("Operaciones", new OperacionesForm(usuario));
        tabPrincipal.addTab("Historial", new HistorialForm(usuario)); tabPrincipal.addTab("Citas", new CitasForm(usuario));
        tabPrincipal.addTab("Solicitudes", new SolicitudesForm(usuario)); btnCerrarSesion.addActionListener(e -> cerrarSesion());
    }
    private void crearInterfaz() { panelPrincipal = new JPanel(new BorderLayout(8, 8)); lblBienvenida = new JLabel(); btnCerrarSesion = new JButton("Cerrar sesión"); tabPrincipal = new JTabbedPane(); panelPrincipal.add(lblBienvenida, BorderLayout.NORTH); panelPrincipal.add(tabPrincipal, BorderLayout.CENTER); panelPrincipal.add(btnCerrarSesion, BorderLayout.SOUTH); }
    private void cerrarSesion() { dispose(); new LoginForm(autenticacion).setVisible(true); }
}

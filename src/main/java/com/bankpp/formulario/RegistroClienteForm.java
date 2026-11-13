package com.bankpp.formulario;
import com.bankpp.service.AutenticacionService;import javax.swing.*;
import java.awt.GridLayout;
public final class RegistroClienteForm extends JFrame {
    private JPanel panelPrincipal;
    private JTextField txtDocumento, txtNombres, txtApellidos, txtCorreo, txtTelefono, txtDireccion;
    private JPasswordField pwdContrasena;
    private JButton btnRegistrar, btnVolver;
    private final AutenticacionService autenticacion;
    private final JFrame login;

    public RegistroClienteForm(AutenticacionService autenticacion, JFrame login) {
        this.autenticacion = autenticacion; this.login = login;
        if (panelPrincipal == null) crearInterfaz();
        setTitle("Bank PP - Registro de cliente"); setContentPane(panelPrincipal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); setSize(520, 560); setLocationRelativeTo(login);
        btnRegistrar.addActionListener(e -> registrarCliente()); btnVolver.addActionListener(e -> volver());
    }

    private void crearInterfaz() {
        panelPrincipal = new JPanel(new GridLayout(0, 2, 8, 8)); panelPrincipal.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        txtDocumento = new JTextField(); txtNombres = new JTextField(); txtApellidos = new JTextField(); txtCorreo = new JTextField();
        pwdContrasena = new JPasswordField(); txtTelefono = new JTextField(); txtDireccion = new JTextField();
        btnRegistrar = new JButton("Registrar cliente"); btnVolver = new JButton("Volver");
        panelPrincipal.add(new JLabel("Documento:")); panelPrincipal.add(txtDocumento);
        panelPrincipal.add(new JLabel("Nombres:")); panelPrincipal.add(txtNombres);
        panelPrincipal.add(new JLabel("Apellidos:")); panelPrincipal.add(txtApellidos);
        panelPrincipal.add(new JLabel("Correo:")); panelPrincipal.add(txtCorreo);
        panelPrincipal.add(new JLabel("Contraseña:")); panelPrincipal.add(pwdContrasena);
        panelPrincipal.add(new JLabel("Teléfono:")); panelPrincipal.add(txtTelefono);
        panelPrincipal.add(new JLabel("Dirección:")); panelPrincipal.add(txtDireccion);
        panelPrincipal.add(btnRegistrar); panelPrincipal.add(btnVolver);
    }

    private void registrarCliente() {
        try { autenticacion.registrarCliente(txtDocumento.getText(), txtNombres.getText(), txtApellidos.getText(), txtCorreo.getText(), new String(pwdContrasena.getPassword()), txtTelefono.getText(), txtDireccion.getText()); JOptionPane.showMessageDialog(this, "Registro guardado. Tu solicitud de apertura de cuenta quedó pendiente de aprobación por un empleado."); dispose(); }
        catch (RuntimeException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Bank PP", JOptionPane.ERROR_MESSAGE); }
    }
    private void volver() { dispose(); }
}

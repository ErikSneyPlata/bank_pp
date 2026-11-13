package com.bankpp.formulario;
import com.bankpp.model.Usuario;import com.bankpp.repository.impl.AuthRepositoryJdbc;import com.bankpp.service.AutenticacionService;import javax.swing.*;
import java.awt.GridLayout;
import com.bankpp.model.enums.RolUsuario;
public final class LoginForm extends JFrame {
    private JPanel panelPrincipal;
    private JTextField txtCorreo;
    private JPasswordField pwdContrasena;
    private JButton btnIniciar;
    private JButton btnRegistro;
    private JLabel lblMensaje;
    private final AutenticacionService autenticacion;

    public LoginForm() { this(new AutenticacionService(new AuthRepositoryJdbc())); }

    public LoginForm(AutenticacionService autenticacion) {
        this.autenticacion = autenticacion;
        if (panelPrincipal == null) crearInterfaz();
        setTitle("Bank PP - Iniciar sesión");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(460, 340);
        setLocationRelativeTo(null);
        btnIniciar.addActionListener(e -> iniciarSesion());
        btnRegistro.addActionListener(e -> abrirRegistro());
    }

    private void crearInterfaz() {
        panelPrincipal = new JPanel(new GridLayout(0, 2, 8, 8));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        txtCorreo = new JTextField();
        pwdContrasena = new JPasswordField();
        btnIniciar = new JButton("Iniciar sesión");
        btnRegistro = new JButton("Crear cliente");
        lblMensaje = new JLabel(" ");
        panelPrincipal.add(new JLabel("Correo:")); panelPrincipal.add(txtCorreo);
        panelPrincipal.add(new JLabel("Contraseña:")); panelPrincipal.add(pwdContrasena);
        panelPrincipal.add(btnIniciar); panelPrincipal.add(btnRegistro);
        panelPrincipal.add(new JLabel("Estado:")); panelPrincipal.add(lblMensaje);
    }

    private void iniciarSesion() {
        try {
            Usuario usuario = autenticacion.iniciarSesion(txtCorreo.getText(), new String(pwdContrasena.getPassword()));
            if (usuario == null) { lblMensaje.setText("Correo o contraseña incorrectos."); return; }
            JFrame panel = usuario.getRol() == RolUsuario.EMPLEADO
                    ? new EmpleadoFrame(usuario, autenticacion)
                    : new PanelClienteForm(usuario, autenticacion);
            panel.setVisible(true);
            dispose();
        } catch (RuntimeException ex) { mostrarError(ex); }
    }

    private void abrirRegistro() { new RegistroClienteForm(autenticacion, this).setVisible(true); }
    private void mostrarError(Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Bank PP", JOptionPane.ERROR_MESSAGE); }
}

package com.bankpp.formulario;

import com.bankpp.model.Usuario;
import com.bankpp.service.AutenticacionService;
import javax.swing.*;
import java.awt.BorderLayout;

public final class EmpleadoFrame extends JFrame {
    private JPanel panelPrincipal;
    private JTabbedPane tabPrincipal;
    private JLabel lblBienvenida;
    private JButton btnCerrarSesion;
    private final Usuario empleado;
    private final AutenticacionService autenticacion;

    public EmpleadoFrame(Usuario empleado, AutenticacionService autenticacion) {
        this.empleado = empleado;
        this.autenticacion = autenticacion;
        if (panelPrincipal == null) crearInterfaz();
        setTitle("Bank PP - Panel de empleado");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new java.awt.Dimension(900, 600));
        setLocationRelativeTo(null);
        lblBienvenida.setText("Empleado: " + empleado.getNombres() + " " + empleado.getApellidos());
        tabPrincipal.addTab("Clientes", new ClientesEmpleadoForm(empleado, autenticacion));
        tabPrincipal.addTab("Cuentas", new CuentasEmpleadoForm(empleado));
        tabPrincipal.addTab("Transacciones", new TransaccionesEmpleadoForm(empleado));
        tabPrincipal.addTab("Citas", new CitasEmpleadoForm(empleado));
        tabPrincipal.addTab("Atención", new AtencionEmpleadoForm(empleado));
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
    }

    private void crearInterfaz() {
        panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        lblBienvenida = new JLabel("Empleado");
        btnCerrarSesion = new JButton("Cerrar sesión");
        tabPrincipal = new JTabbedPane();
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.add(lblBienvenida, BorderLayout.WEST);
        encabezado.add(btnCerrarSesion, BorderLayout.EAST);
        panelPrincipal.add(encabezado, BorderLayout.NORTH);
        panelPrincipal.add(tabPrincipal, BorderLayout.CENTER);
    }

    private void cerrarSesion() {
        dispose();
        new LoginForm(autenticacion).setVisible(true);
    }
}

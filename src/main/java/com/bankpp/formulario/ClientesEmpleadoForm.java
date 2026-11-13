package com.bankpp.formulario;

import com.bankpp.model.Cliente;
import com.bankpp.model.DatosContacto;
import com.bankpp.model.Usuario;
import com.bankpp.repository.impl.ClienteRepositoryJdbc;
import com.bankpp.service.AutenticacionService;
import com.bankpp.service.ClienteService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public final class ClientesEmpleadoForm extends JPanel {
    private final Usuario empleado;
    private final AutenticacionService autenticacion;
    private final ClienteService servicio = new ClienteService(new ClienteRepositoryJdbc());
    private final JTable tabla = new JTable();
    private final JTextField txtBuscar = new JTextField(22);
    private List<Cliente> clientes = List.of();

    public ClientesEmpleadoForm(Usuario empleado, AutenticacionService autenticacion) {
        this.empleado = empleado; this.autenticacion = autenticacion;
        setLayout(new BorderLayout(8, 8));
        tabla.setModel(new DefaultTableModel(new Object[]{"ID", "Documento", "Nombres", "Apellidos", "Correo", "Teléfono", "Activo"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        });
        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton buscar = new JButton("Buscar"), actualizar = new JButton("Actualizar");
        busqueda.add(new JLabel("Buscar cliente:")); busqueda.add(txtBuscar); busqueda.add(buscar); busqueda.add(actualizar);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton nuevo = new JButton("Registrar cliente"), editar = new JButton("Editar seleccionado"), desactivar = new JButton("Desactivar seleccionado");
        acciones.add(nuevo); acciones.add(editar); acciones.add(desactivar);
        add(busqueda, BorderLayout.NORTH); add(new JScrollPane(tabla), BorderLayout.CENTER); add(acciones, BorderLayout.SOUTH);
        ocultarId(); buscar.addActionListener(e -> cargar()); actualizar.addActionListener(e -> {txtBuscar.setText("");cargar();});
        nuevo.addActionListener(e -> registrar()); editar.addActionListener(e -> editar()); desactivar.addActionListener(e -> desactivar());
        txtBuscar.addActionListener(e -> cargar()); cargar();
    }

    private void ocultarId() { var c=tabla.getColumnModel().getColumn(0); c.setMinWidth(0);c.setMaxWidth(0);c.setPreferredWidth(0); }
    private void cargar() {
        try { clientes=servicio.listar(empleado,txtBuscar.getText());DefaultTableModel m=(DefaultTableModel)tabla.getModel();m.setRowCount(0);
            for(Cliente c:clientes)m.addRow(new Object[]{c.getId(),c.getDocumento(),c.getNombres(),c.getApellidos(),c.getCorreo(),c.getDatosContacto().getTelefono(),c.isActivo()});
        } catch(RuntimeException e){error(e);}
    }
    private Cliente seleccionado() {int row=tabla.getSelectedRow();if(row<0)throw new IllegalArgumentException("Selecciona un cliente de la tabla");return clientes.get(tabla.convertRowIndexToModel(row));}
    private void registrar() {
        JTextField documento=new JTextField(),nombres=new JTextField(),apellidos=new JTextField(),correo=new JTextField(),telefono=new JTextField(),direccion=new JTextField();JPasswordField clave=new JPasswordField();
        Object[] form={"Documento",documento,"Nombres",nombres,"Apellidos",apellidos,"Correo",correo,"Contraseña inicial",clave,"Teléfono",telefono,"Dirección",direccion};
        if(JOptionPane.showConfirmDialog(this,form,"Registrar cliente",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)try{
            autenticacion.registrarCliente(documento.getText(),nombres.getText(),apellidos.getText(),correo.getText(),new String(clave.getPassword()),telefono.getText(),direccion.getText());
            JOptionPane.showMessageDialog(this,"Cliente registrado. Se creó una solicitud de apertura pendiente de aprobación.");cargar();
        }catch(RuntimeException e){error(e);}
    }
    private void editar() {
        try {Cliente c=seleccionado();JTextField nombres=new JTextField(c.getNombres()),apellidos=new JTextField(c.getApellidos()),correo=new JTextField(c.getCorreo()),telefono=new JTextField(c.getDatosContacto().getTelefono()),direccion=new JTextField(c.getDatosContacto().getDireccion());
            Object[] form={"Nombres",nombres,"Apellidos",apellidos,"Correo",correo,"Teléfono",telefono,"Dirección",direccion};
            if(JOptionPane.showConfirmDialog(this,form,"Editar cliente",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){Cliente actualizado=new Cliente(c.getId(),c.getDocumento(),nombres.getText(),apellidos.getText(),correo.getText(),c.isActivo(),new DatosContacto(telefono.getText(),direccion.getText()));servicio.actualizar(empleado,actualizado);cargar();}
        }catch(RuntimeException e){error(e);}
    }
    private void desactivar() {try{Cliente c=seleccionado();if(JOptionPane.showConfirmDialog(this,"¿Desactivar a "+c.getNombres()+" "+c.getApellidos()+"?","Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){servicio.desactivar(empleado,c.getId());cargar();}}catch(RuntimeException e){error(e);}}
    private void error(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Gestión de clientes",JOptionPane.ERROR_MESSAGE);}
}

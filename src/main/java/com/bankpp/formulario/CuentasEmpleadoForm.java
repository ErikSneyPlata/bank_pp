package com.bankpp.formulario;

import com.bankpp.model.CuentaBancaria;
import com.bankpp.model.SolicitudCuenta;
import com.bankpp.model.Usuario;
import com.bankpp.repository.impl.CuentaRepositoryJdbc;
import com.bankpp.repository.impl.SolicitudCuentaRepositoryJdbc;
import com.bankpp.service.CuentaService;
import com.bankpp.service.SolicitudCuentaService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class CuentasEmpleadoForm extends JPanel {
    private final Usuario empleado;
    private final CuentaService cuentas = new CuentaService(new CuentaRepositoryJdbc());
    private final SolicitudCuentaService solicitudes = new SolicitudCuentaService(new SolicitudCuentaRepositoryJdbc());
    private final JTable tablaCuentas = new JTable(), tablaSolicitudes = new JTable();
    private List<CuentaBancaria> listadoCuentas=List.of();
    private List<SolicitudCuenta> listadoSolicitudes=List.of();

    public CuentasEmpleadoForm(Usuario empleado) {
        this.empleado=empleado;setLayout(new BorderLayout());JTabbedPane tabs=new JTabbedPane();
        tablaCuentas.setModel(modelo("ID","Número","Cliente UUID","Saldo","Moneda","Estado"));
        JButton actualizarCuentas=new JButton("Actualizar"), bloquear=new JButton("Bloquear"), cerrar=new JButton("Cerrar cuenta");
        JPanel accionesCuentas=new JPanel(new FlowLayout(FlowLayout.RIGHT));accionesCuentas.add(actualizarCuentas);accionesCuentas.add(bloquear);accionesCuentas.add(cerrar);
        JPanel panelCuentas=new JPanel(new BorderLayout(8,8));panelCuentas.add(new JScrollPane(tablaCuentas),BorderLayout.CENTER);panelCuentas.add(accionesCuentas,BorderLayout.SOUTH);ocultarId(tablaCuentas);
        actualizarCuentas.addActionListener(e->cargarCuentas());bloquear.addActionListener(e->cambiarEstado(false));cerrar.addActionListener(e->cambiarEstado(true));
        tablaSolicitudes.setModel(modelo("ID","Documento","Cliente","Correo","Solicitada"));
        JButton actualizarSolicitudes=new JButton("Actualizar solicitudes"), aprobar=new JButton("Aprobar y abrir cuenta"), rechazar=new JButton("Rechazar");
        JPanel accionesSolicitudes=new JPanel(new FlowLayout(FlowLayout.RIGHT));accionesSolicitudes.add(actualizarSolicitudes);accionesSolicitudes.add(aprobar);accionesSolicitudes.add(rechazar);
        JPanel panelSolicitudes=new JPanel(new BorderLayout(8,8));panelSolicitudes.add(new JLabel("Solicitudes pendientes de apertura"),BorderLayout.NORTH);panelSolicitudes.add(new JScrollPane(tablaSolicitudes),BorderLayout.CENTER);panelSolicitudes.add(accionesSolicitudes,BorderLayout.SOUTH);ocultarId(tablaSolicitudes);
        actualizarSolicitudes.addActionListener(e->cargarSolicitudes());aprobar.addActionListener(e->resolver(true));rechazar.addActionListener(e->resolver(false));
        tabs.addTab("Cuentas existentes",panelCuentas);tabs.addTab("Aperturas pendientes",panelSolicitudes);add(tabs,BorderLayout.CENTER);cargarCuentas();cargarSolicitudes();
    }
    private DefaultTableModel modelo(String... columnas){return new DefaultTableModel(columnas,0){@Override public boolean isCellEditable(int r,int c){return false;}};}
    private void ocultarId(JTable t){var c=t.getColumnModel().getColumn(0);c.setMinWidth(0);c.setMaxWidth(0);c.setPreferredWidth(0);}
    private void cargarCuentas(){try{listadoCuentas=cuentas.listarTodas(empleado);DefaultTableModel m=(DefaultTableModel)tablaCuentas.getModel();m.setRowCount(0);for(CuentaBancaria c:listadoCuentas)m.addRow(new Object[]{c.getId(),c.getNumero(),c.getUsuarioId(),c.getSaldo(),c.getMoneda(),c.getEstado()});}catch(RuntimeException e){error(e);}}
    private void cargarSolicitudes(){try{listadoSolicitudes=solicitudes.listarPendientes(empleado);DefaultTableModel m=(DefaultTableModel)tablaSolicitudes.getModel();m.setRowCount(0);DateTimeFormatter f=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());for(SolicitudCuenta s:listadoSolicitudes)m.addRow(new Object[]{s.getId(),s.getDocumento(),s.getNombres()+" "+s.getApellidos(),s.getCorreo(),f.format(s.getCreadaEn())});}catch(RuntimeException e){error(e);}}
    private void cambiarEstado(boolean cerrar){try{int row=tablaCuentas.getSelectedRow();if(row<0)throw new IllegalArgumentException("Selecciona una cuenta");CuentaBancaria cuenta=listadoCuentas.get(tablaCuentas.convertRowIndexToModel(row));String accion=cerrar?"cerrar":"bloquear";if(JOptionPane.showConfirmDialog(this,"¿Deseas "+accion+" la cuenta "+cuenta.getNumero()+"?","Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){if(cerrar)cuentas.cerrar(cuenta.getId(),empleado);else cuentas.bloquear(cuenta.getId(),empleado);cargarCuentas();}}catch(RuntimeException e){error(e);}}
    private void resolver(boolean aprobar){try{int row=tablaSolicitudes.getSelectedRow();if(row<0)throw new IllegalArgumentException("Selecciona una solicitud pendiente");SolicitudCuenta s=listadoSolicitudes.get(tablaSolicitudes.convertRowIndexToModel(row));if(aprobar){String numero=solicitudes.aprobar(s.getId(),empleado);JOptionPane.showMessageDialog(this,"Apertura aprobada. Cuenta: "+numero);}else{solicitudes.rechazar(s.getId(),empleado);JOptionPane.showMessageDialog(this,"Solicitud rechazada.");}cargarSolicitudes();cargarCuentas();}catch(RuntimeException e){error(e);cargarSolicitudes();}}
    private void error(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Gestión de cuentas",JOptionPane.ERROR_MESSAGE);}
}

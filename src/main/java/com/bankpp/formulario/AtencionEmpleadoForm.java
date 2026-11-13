package com.bankpp.formulario;

import com.bankpp.model.SolicitudAtencion;
import com.bankpp.model.Usuario;
import com.bankpp.model.enums.EstadoSolicitud;
import com.bankpp.repository.impl.AtencionRepositoryJdbc;
import com.bankpp.service.AtencionService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public final class AtencionEmpleadoForm extends JPanel {
    private final Usuario empleado;
    private final AtencionService servicio=new AtencionService(new AtencionRepositoryJdbc());
    private final JTable tabla=new JTable();
    private final JComboBox<EstadoSolicitud> estado=new JComboBox<>(new EstadoSolicitud[]{EstadoSolicitud.EN_PROCESO,EstadoSolicitud.RESUELTA,EstadoSolicitud.CERRADA});
    private List<SolicitudAtencion> solicitudes=List.of();
    public AtencionEmpleadoForm(Usuario empleado){this.empleado=empleado;setLayout(new BorderLayout(8,8));tabla.setModel(new DefaultTableModel(new Object[]{"ID","Cliente UUID","Descripción","Estado"},0){@Override public boolean isCellEditable(int r,int c){return false;}});var c=tabla.getColumnModel().getColumn(0);c.setMinWidth(0);c.setMaxWidth(0);c.setPreferredWidth(0);JButton actualizar=new JButton("Actualizar"),tomar=new JButton("Tomar solicitud"),cambiar=new JButton("Guardar estado");JPanel acciones=new JPanel(new FlowLayout(FlowLayout.RIGHT));acciones.add(actualizar);acciones.add(new JLabel("Nuevo estado:"));acciones.add(estado);acciones.add(tomar);acciones.add(cambiar);add(new JLabel("Solicitudes de atención de clientes"),BorderLayout.NORTH);add(new JScrollPane(tabla),BorderLayout.CENTER);add(acciones,BorderLayout.SOUTH);actualizar.addActionListener(e->cargar());tomar.addActionListener(e->cambiar(EstadoSolicitud.EN_PROCESO));cambiar.addActionListener(e->cambiar((EstadoSolicitud)estado.getSelectedItem()));cargar();}
    private void cargar(){try{solicitudes=servicio.listarSolicitudesEmpleado(empleado);DefaultTableModel m=(DefaultTableModel)tabla.getModel();m.setRowCount(0);for(SolicitudAtencion s:solicitudes)m.addRow(new Object[]{s.getId(),s.getClienteId(),s.getDescripcion(),s.getEstado()});}catch(RuntimeException e){error(e);}}
    private void cambiar(EstadoSolicitud nuevo){try{int row=tabla.getSelectedRow();if(row<0)throw new IllegalArgumentException("Selecciona una solicitud");SolicitudAtencion s=solicitudes.get(tabla.convertRowIndexToModel(row));servicio.gestionarSolicitud(s.getId(),nuevo,empleado);cargar();}catch(RuntimeException e){error(e);}}
    private void error(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Atención al cliente",JOptionPane.ERROR_MESSAGE);}
}

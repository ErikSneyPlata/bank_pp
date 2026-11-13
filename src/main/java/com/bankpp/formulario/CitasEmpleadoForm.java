package com.bankpp.formulario;

import com.bankpp.model.Cita;
import com.bankpp.model.Usuario;
import com.bankpp.model.enums.EstadoCita;
import com.bankpp.repository.impl.AtencionRepositoryJdbc;
import com.bankpp.service.AtencionService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class CitasEmpleadoForm extends JPanel {
    private final Usuario empleado;
    private final AtencionService servicio=new AtencionService(new AtencionRepositoryJdbc());
    private final JTable tabla=new JTable();
    private List<Cita> citas=List.of();
    public CitasEmpleadoForm(Usuario empleado){this.empleado=empleado;setLayout(new BorderLayout(8,8));tabla.setModel(new DefaultTableModel(new Object[]{"ID","Cliente UUID","Fecha y hora","Motivo","Estado"},0){@Override public boolean isCellEditable(int r,int c){return false;}});var c=tabla.getColumnModel().getColumn(0);c.setMinWidth(0);c.setMaxWidth(0);c.setPreferredWidth(0);JButton actualizar=new JButton("Actualizar"),confirmar=new JButton("Confirmar"),cancelar=new JButton("Cancelar"),completar=new JButton("Completar");JPanel acciones=new JPanel(new FlowLayout(FlowLayout.RIGHT));acciones.add(actualizar);acciones.add(confirmar);acciones.add(cancelar);acciones.add(completar);add(new JLabel("Citas de clientes"),BorderLayout.NORTH);add(new JScrollPane(tabla),BorderLayout.CENTER);add(acciones,BorderLayout.SOUTH);actualizar.addActionListener(e->cargar());confirmar.addActionListener(e->gestionar(EstadoCita.CONFIRMADA));cancelar.addActionListener(e->gestionar(EstadoCita.CANCELADA));completar.addActionListener(e->gestionar(EstadoCita.COMPLETADA));cargar();}
    private void cargar(){try{citas=servicio.listarCitasEmpleado(empleado);DefaultTableModel m=(DefaultTableModel)tabla.getModel();m.setRowCount(0);DateTimeFormatter f=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());for(Cita c:citas)m.addRow(new Object[]{c.getId(),c.getClienteId(),f.format(c.getFechaHora()),c.getMotivo(),c.getEstado()});}catch(RuntimeException e){error(e);}}
    private void gestionar(EstadoCita estado){try{int row=tabla.getSelectedRow();if(row<0)throw new IllegalArgumentException("Selecciona una cita");Cita c=citas.get(tabla.convertRowIndexToModel(row));servicio.gestionarCita(c.getId(),estado,empleado);cargar();}catch(RuntimeException e){error(e);}}
    private void error(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Gestión de citas",JOptionPane.ERROR_MESSAGE);}
}

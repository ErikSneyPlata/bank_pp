package com.bankpp.formulario;

import com.bankpp.model.CuentaBancaria;
import com.bankpp.model.Transaccion;
import com.bankpp.model.Usuario;
import com.bankpp.repository.impl.CuentaRepositoryJdbc;
import com.bankpp.repository.impl.TransaccionRepositoryJdbc;
import com.bankpp.service.CuentaService;
import com.bankpp.service.TransaccionService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class TransaccionesEmpleadoForm extends JPanel {
    private final Usuario empleado;
    private final TransaccionService servicio=new TransaccionService(new TransaccionRepositoryJdbc());
    private final CuentaService cuentas=new CuentaService(new CuentaRepositoryJdbc());
    private final JTextField filtroCuenta=new JTextField(18), cuentaDeposito=new JTextField(18), montoDeposito=new JTextField(10);
    private final JTable tabla=new JTable();
    public TransaccionesEmpleadoForm(Usuario empleado){
        this.empleado=empleado;setLayout(new BorderLayout(8,8));
        tabla.setModel(new DefaultTableModel(new Object[]{"ID","Tipo","Cuenta origen","Cuenta destino","Monto","Fecha","Descripción"},0){@Override public boolean isCellEditable(int r,int c){return false;}});
        var id=tabla.getColumnModel().getColumn(0);id.setMinWidth(0);id.setMaxWidth(0);id.setPreferredWidth(0);
        JPanel busqueda=new JPanel(new FlowLayout(FlowLayout.LEFT));JButton filtrar=new JButton("Filtrar"),actualizar=new JButton("Ver todas");
        busqueda.add(new JLabel("Número de cuenta:"));busqueda.add(filtroCuenta);busqueda.add(filtrar);busqueda.add(actualizar);
        JPanel deposito=new JPanel(new FlowLayout(FlowLayout.LEFT));deposito.setBorder(BorderFactory.createTitledBorder("Registrar depósito"));JButton btnDepositar=new JButton("Acreditar depósito");
        deposito.add(new JLabel("Cuenta:"));deposito.add(cuentaDeposito);deposito.add(new JLabel("Monto COP:"));deposito.add(montoDeposito);deposito.add(btnDepositar);
        JPanel norte=new JPanel(new BorderLayout());norte.add(busqueda,BorderLayout.NORTH);norte.add(deposito,BorderLayout.SOUTH);
        add(norte,BorderLayout.NORTH);add(new JScrollPane(tabla),BorderLayout.CENTER);
        filtrar.addActionListener(e->cargar());actualizar.addActionListener(e->{filtroCuenta.setText("");cargar();});filtroCuenta.addActionListener(e->cargar());
        btnDepositar.addActionListener(e->depositar());cargar();
    }
    private void cargar(){try{List<Transaccion> rows=servicio.consultarTodas(empleado,filtroCuenta.getText());DefaultTableModel m=(DefaultTableModel)tabla.getModel();m.setRowCount(0);DateTimeFormatter f=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());for(Transaccion t:rows)m.addRow(new Object[]{t.getId(),t.getTipo(),t.getCuentaOrigenId(),t.getCuentaDestinoId(),t.getMonto(),f.format(t.getFecha()),t.getDescripcion()});}catch(RuntimeException e){error(e);}}
    private void depositar(){try{CuentaBancaria cuenta=cuentas.buscarPorNumero(cuentaDeposito.getText().trim()).orElseThrow(()->new IllegalArgumentException("No existe la cuenta indicada"));BigDecimal monto=new BigDecimal(montoDeposito.getText().trim());if(JOptionPane.showConfirmDialog(this,"¿Acreditar "+monto+" COP a la cuenta "+cuenta.getNumero()+"?","Confirmar depósito",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){servicio.depositar(cuenta.getId(),empleado,monto);cuentaDeposito.setText("");montoDeposito.setText("");cargar();JOptionPane.showMessageDialog(this,"Depósito registrado correctamente.");}}catch(RuntimeException e){error(e);}}
    private void error(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Transacciones y depósitos",JOptionPane.ERROR_MESSAGE);}
}

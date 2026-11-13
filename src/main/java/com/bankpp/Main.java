package com.bankpp;

import com.bankpp.formulario.LoginForm;

import javax.swing.*;

public final class Main{

    private Main(){}

    public static void main(String[]args){
        SwingUtilities.invokeLater(()->new LoginForm().setVisible(true));
    }
}

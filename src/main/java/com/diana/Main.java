package com.diana;

import com.diana.controller.MainController;
import com.diana.view.MainView;

import java.net.MalformedURLException;

public class Main {
    public static void main(String[] args) throws MalformedURLException {
//        SwingUtilities.invokeLater(() -> new MainView());

        new MainController(new MainView());
    }
}
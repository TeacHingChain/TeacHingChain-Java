package com.thc.blockchain.gui;

import com.thc.blockchain.util.Miner;

import javax.swing.*;
import java.awt.*;

import static com.thc.blockchain.util.Miner.hashRate;

public class MiningPane extends JFrame {

    public MiningPane() {
        super("Mining data");
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(10, 20, 10, 10);
        final JTextField hashRateText = new JTextField("Hash rate: " + hashRate);
        super.setSize(640, 640);
        super.setVisible(true);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setResizable(true);
        final JPanel newPanel = new JPanel(new GridBagLayout());
        if (Miner.iterator) {
            newPanel.add(hashRateText, constraints);
            pack();
        }
    }
}

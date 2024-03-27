package MatigasnaTubig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IntroPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    public IntroPanel() {
        setPreferredSize(new Dimension(400, 300));
        setBackground(Color.BLACK);

        JButton singlePlayerButton = new JButton("Single Player");
        singlePlayerButton.setForeground(Color.WHITE);
        singlePlayerButton.setBackground(Color.BLUE);
        singlePlayerButton.setFocusPainted(false);
        singlePlayerButton.setFont(new Font("Arial", Font.BOLD, 20));
        singlePlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSinglePlayerGame();
            }
        });

        JButton multiPlayerButton = new JButton("Multiplayer");
        multiPlayerButton.setForeground(Color.WHITE);
        multiPlayerButton.setBackground(Color.GREEN);
        multiPlayerButton.setFocusPainted(false);
        multiPlayerButton.setFont(new Font("Arial", Font.BOLD, 20));
        multiPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startMultiplayerGame();
            }
        });

        JButton zombieModeButton = new JButton("Zombie Mode");
        zombieModeButton.setForeground(Color.WHITE);
        zombieModeButton.setBackground(Color.RED);
        zombieModeButton.setFocusPainted(false);
        zombieModeButton.setFont(new Font("Arial", Font.BOLD, 20));
        zombieModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startZombieModeGame();
            }
        });

        setLayout(new GridLayout(3, 1));
        add(singlePlayerButton);
        add(multiPlayerButton);
        add(zombieModeButton);
    }

    private void startSinglePlayerGame() {
        // Close the intro panel
        JFrame introFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        introFrame.dispose();

        // Start the SinglePlayer game
        SinglePlayer game = new SinglePlayer();
        JFrame gameFrame = new JFrame("Single Player Mode");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setResizable(false);
        gameFrame.add(game);
        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null); // Position relative to the center
        gameFrame.setVisible(true);
        new Thread(game).start();
    }

    private void startMultiplayerGame() {
        // Start the multiplayer game directly by instantiating the MultiPlayer class
        new MultiPlayer();

        // Close the intro panel
        JFrame introFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        introFrame.dispose();
    }

    private void startZombieModeGame() {
        // Close the intro panel
        JFrame introFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        introFrame.dispose();

        // Start the Zombie Mode game
        ZombieMode zombieMode = new ZombieMode();
        JFrame gameFrame = new JFrame("Zombie Mode");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.getContentPane().add(zombieMode);
        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
        new Thread(zombieMode).start();
    }

    public static void main(String[] args) {
        JFrame introFrame = new JFrame("Intro");
        introFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        introFrame.getContentPane().add(new IntroPanel());
        introFrame.pack();
        introFrame.setLocationRelativeTo(null);
        introFrame.setVisible(true);
    }
}

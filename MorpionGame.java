import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class MorpionGame extends JFrame {
    private JButton[][] buttons = new JButton[3][3];
    private String player1, player2;
    private char player1Symbol, player2Symbol;
    private char currentPlayer;
    private int score1 = 0, score2 = 0;
    private boolean isBotMode = false;

    public MorpionGame() {
        showMenu(); // Affiche le menu principal au démarrage
    }

    private void showMenu() {
        getContentPane().removeAll();
        repaint();
        setTitle("Menu Principal");
        setSize(400, 200);
        setLayout(new GridLayout(3, 1));

        JLabel welcomeLabel = new JLabel("Bienvenue au jeu du Morpion !", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(welcomeLabel);

        JButton mode1v1 = new JButton("Mode 1v1");
        JButton modeBot = new JButton("Mode contre le bot");
        mode1v1.addActionListener(e -> setupPlayers(false));
        modeBot.addActionListener(e -> setupPlayers(true));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(mode1v1);
        buttonPanel.add(modeBot);
        add(buttonPanel);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setupPlayers(boolean botMode) {
        isBotMode = botMode;
        player1 = JOptionPane.showInputDialog(this, "Entrez le nom du joueur 1 :");
        if (isBotMode) {
            player2 = "Bot";
        } else {
            player2 = JOptionPane.showInputDialog(this, "Entrez le nom du joueur 2 :");
        }

        String[] options = {"X", "O"};
        int choice = JOptionPane.showOptionDialog(this, player1 + ", choisissez votre symbole :", 
            "Choix du Symbole", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        player1Symbol = (choice == 0) ? 'X' : 'O';
        player2Symbol = (player1Symbol == 'X') ? 'O' : 'X';

        startGame();
    }

    private void startGame() {
        getContentPane().removeAll();
        repaint();
        setTitle("Morpion : " + player1 + " vs " + player2);
        setSize(400, 400);
        setLayout(new GridLayout(3, 3));

        currentPlayer = player1Symbol;
        initializeBoard();

        setVisible(true);
    }

    private void initializeBoard() {
        buttons = new JButton[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 60));
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setBackground(Color.decode("#DBD2C3"));
                buttons[i][j].setBorder(BorderFactory.createLineBorder(Color.decode("#800020"), 3));
                buttons[i][j].addActionListener(new ButtonClickListener(i, j));
                add(buttons[i][j]);
            }
        }
    }

    private class ButtonClickListener implements ActionListener {
        private int row, col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttons[row][col].getText().equals("")) {
                buttons[row][col].setText(String.valueOf(currentPlayer));
                buttons[row][col].setForeground(currentPlayer == 'X' ? Color.BLUE : Color.RED);

                if (checkWin()) {
                    showWinMessage();
                } else if (isBoardFull()) {
                    showEndGameDialog("Match nul !");
                } else {
                    switchPlayer();
                    if (isBotMode && currentPlayer == player2Symbol) {
                        botMove();
                    }
                }
            }
        }
    }

    private void botMove() {
        Random rand = new Random();
        int row, col;
        do {
            row = rand.nextInt(3);
            col = rand.nextInt(3);
        } while (!buttons[row][col].getText().equals(""));
        buttons[row][col].doClick();
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == player1Symbol) ? player2Symbol : player1Symbol;
    }

    private boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().equals(String.valueOf(currentPlayer)) &&
                buttons[i][1].getText().equals(String.valueOf(currentPlayer)) &&
                buttons[i][2].getText().equals(String.valueOf(currentPlayer))) {
                highlightWin(buttons[i][0], buttons[i][1], buttons[i][2]);
                return true;
            }
            if (buttons[0][i].getText().equals(String.valueOf(currentPlayer)) &&
                buttons[1][i].getText().equals(String.valueOf(currentPlayer)) &&
                buttons[2][i].getText().equals(String.valueOf(currentPlayer))) {
                highlightWin(buttons[0][i], buttons[1][i], buttons[2][i]);
                return true;
            }
        }
        if (buttons[0][0].getText().equals(String.valueOf(currentPlayer)) &&
            buttons[1][1].getText().equals(String.valueOf(currentPlayer)) &&
            buttons[2][2].getText().equals(String.valueOf(currentPlayer))) {
            highlightWin(buttons[0][0], buttons[1][1], buttons[2][2]);
            return true;
        }
        if (buttons[0][2].getText().equals(String.valueOf(currentPlayer)) &&
            buttons[1][1].getText().equals(String.valueOf(currentPlayer)) &&
            buttons[2][0].getText().equals(String.valueOf(currentPlayer))) {
            highlightWin(buttons[0][2], buttons[1][1], buttons[2][0]);
            return true;
        }
        return false;
    }

    private void highlightWin(JButton b1, JButton b2, JButton b3) {
        b1.setBackground(Color.GREEN);
        b2.setBackground(Color.GREEN);
        b3.setBackground(Color.GREEN);
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("")) {
                    return false;
                }
            }
        }
        return true;
    }

    private void showWinMessage() {
        String winner = (currentPlayer == player1Symbol) ? player1 : player2;
        if (currentPlayer == player1Symbol) {
            score1++;
        } else {
            score2++;
        }
        showEndGameDialog(winner + " a gagné !");
    }

    private void showEndGameDialog(String message) {
        int choice = JOptionPane.showOptionDialog(
            this,
            message + "\nQue souhaitez-vous faire ?",
            "Fin de la partie",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            new String[]{"Rejouer", "Quitter"},
            "Rejouer"
        );

        if (choice == JOptionPane.YES_OPTION) {
            resetBoard();
        } else {
            System.exit(0);
        }
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setBackground(Color.decode("#DBD2C3"));
            }
        }
        currentPlayer = player1Symbol;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MorpionGame());
    }
}

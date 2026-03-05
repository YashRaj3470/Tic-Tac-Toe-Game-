import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.sound.midi.*;

public class TicTacToeUI extends JFrame {
    private TicTacToe game;
    private CellPanel[][] cells;
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private JComboBox<String> difficultyCombo;

    // Scores
    private int scoreX = 0;
    private int scoreO = 0;
    private int scoreDraws = 0;

    private boolean isVsComputer = true; // Default selected

    // Cyberpunk Futuristic Colors
    private final Color bgDark = new Color(5, 5, 10);
    private final Color gridLineColor = new Color(0, 255, 255, 30); // Faint cyan grid

    private final Color panelColor = new Color(15, 20, 25, 200); // Darker glass
    private final Color hoverColor = new Color(0, 255, 255, 40); // Cyan tint hover
    private final Color xColor = new Color(255, 0, 85); // Cyberpunk Pink
    private final Color xShadow = new Color(255, 0, 85, 150); // Glow
    private final Color oColor = new Color(0, 255, 255); // Cyberpunk Cyan
    private final Color oShadow = new Color(0, 255, 255, 150); // Glow
    private final Color winHighlightColor = new Color(0, 255, 128, 80); // Neon Green glow

    private final Color neonBlue = new Color(0, 200, 255);
    private final Color neonYellow = new Color(255, 220, 0);

    // Audio Engine
    private CyberSynthAudio audioManager;

    public TicTacToeUI() {
        game = new TicTacToe();
        audioManager = new CyberSynthAudio();

        setTitle("CYBER-TAC-TOE v3.0 // AUDIO ONLINE");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Custom Cyberpunk Background Panel
        BackgroundPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // Header Panel (Transparent)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(25, 20, 15, 20));

        statusLabel = new JLabel("[ SYS: X ACTIVE ]", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Monospaced", Font.BOLD, 36));
        statusLabel.setForeground(neonYellow);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreLabel = new JLabel("USR_X: 0   ||   TIE: 0   ||   AI_O: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        scoreLabel.setForeground(neonBlue);
        scoreLabel.setBorder(new EmptyBorder(10, 0, 15, 0));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Mode Toggle Panel
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        modePanel.setOpaque(false);

        JRadioButton pvpBtn = new JRadioButton("PVP_MODE");
        JRadioButton pvcBtn = new JRadioButton("PVE_MODE", true); // Default

        Font radioFont = new Font("Monospaced", Font.BOLD, 18);
        pvpBtn.setForeground(Color.GRAY);
        pvcBtn.setBackground(bgDark);
        pvcBtn.setForeground(neonBlue);
        pvcBtn.setBackground(bgDark);

        pvpBtn.setOpaque(false);
        pvcBtn.setOpaque(false);
        pvpBtn.setFont(radioFont);
        pvcBtn.setFont(radioFont);
        pvpBtn.setFocusPainted(false);
        pvcBtn.setFocusPainted(false);
        pvpBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pvcBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(pvcBtn);
        modeGroup.add(pvpBtn);

        // Difficulty Selector
        String[] difficulties = { "LVL: CASUAL", "LVL: STANDARD", "LVL: EXTREME" };
        difficultyCombo = new JComboBox<>(difficulties);
        difficultyCombo.setFont(new Font("Monospaced", Font.BOLD, 16));
        difficultyCombo.setFocusable(false);
        difficultyCombo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        difficultyCombo.setSelectedIndex(1); // Default to Medium

        // High visibility combo box
        difficultyCombo.setBackground(new Color(20, 30, 40));
        difficultyCombo.setForeground(neonYellow);
        difficultyCombo.setBorder(BorderFactory.createLineBorder(neonBlue, 2));

        JPanel difficultyPanel = new JPanel();
        difficultyPanel.setOpaque(false);
        JLabel diffLabel = new JLabel("SYS_DIFF: ");
        diffLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        diffLabel.setForeground(neonBlue);
        difficultyPanel.add(diffLabel);
        difficultyPanel.add(difficultyCombo);

        pvpBtn.addActionListener(e -> {
            audioManager.playSystemBeep();
            isVsComputer = false;
            pvpBtn.setForeground(neonBlue);
            pvcBtn.setForeground(Color.GRAY);
            difficultyPanel.setVisible(false); // Hide difficulty in PvP
            resetScores();
            resetGame();
            scoreLabel.setText("USR_X: " + scoreX + "   ||   TIE: " + scoreDraws + "   ||   USR_O: " + scoreO);
        });
        pvcBtn.addActionListener(e -> {
            audioManager.playSystemBeep();
            isVsComputer = true;
            pvcBtn.setForeground(neonBlue);
            pvpBtn.setForeground(Color.GRAY);
            difficultyPanel.setVisible(true); // Show difficulty in PvC
            resetScores();
            resetGame();
            scoreLabel.setText("USR_X: " + scoreX + "   ||   TIE: " + scoreDraws + "   ||   AI_O: " + scoreO);
        });

        difficultyCombo.addActionListener(e -> {
            audioManager.playSystemBeep();
            resetScores();
            resetGame();
        });

        modePanel.add(pvcBtn);
        modePanel.add(pvpBtn);

        headerPanel.add(statusLabel);
        headerPanel.add(scoreLabel);
        headerPanel.add(modePanel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(difficultyPanel);

        // Board Center Wrapper
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerWrapper.setOpaque(false);

        // Board Panel
        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 10, 10)); // Gap between cells
        boardPanel.setOpaque(false);
        boardPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        boardPanel.setPreferredSize(new Dimension(420, 420));

        cells = new CellPanel[3][3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                cells[row][col] = new CellPanel(row, col);
                boardPanel.add(cells[row][col]);
            }
        }
        centerWrapper.add(boardPanel);

        // Footer Panel
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(20, 20, 30, 20));

        // Highly visible Cyberpunk Button
        JButton resetBtn = new JButton(">> EXECUTE REBOOT <<") {
            private boolean isBtnHovered = false;
            {
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setForeground(Color.BLACK);
                setFont(new Font("Monospaced", Font.BOLD, 22));
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setPreferredSize(new Dimension(360, 60));

                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        isBtnHovered = true;
                        repaint();
                    }

                    public void mouseExited(MouseEvent e) {
                        isBtnHovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Outer Glow / Border
                if (isBtnHovered) {
                    g2.setColor(xColor);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    setForeground(Color.WHITE);
                } else {
                    g2.setColor(neonBlue);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    setForeground(Color.BLACK);
                }

                // Cyberpunk inner detail line
                g2.setColor(Color.WHITE);
                g2.drawRect(4, 4, getWidth() - 9, getHeight() - 9);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        resetBtn.addActionListener(e -> {
            audioManager.playSystemBeep();
            resetGame();
        });

        footerPanel.add(resetBtn);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        pack();
        setSize(540, 850);
        setLocationRelativeTo(null);

        // Boot background music loop
        audioManager.startBackgroundMusic();
    }

    // Futuristic Grid Background Painter
    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int w = getWidth(), h = getHeight();

            // Solid dark
            g2.setColor(bgDark);
            g2.fillRect(0, 0, w, h);

            // Draw perspective grid
            g2.setColor(gridLineColor);
            int gridSize = 40;

            // Vertical lines
            for (int i = 0; i < w; i += gridSize) {
                g2.drawLine(i, 0, i, h);
            }
            // Horizontal lines
            for (int j = 0; j < h; j += gridSize) {
                g2.drawLine(0, j, w, j);
            }

            // Edge glowing effects
            GradientPaint gpTop = new GradientPaint(0, 0, new Color(0, 255, 255, 40), 0, 100, new Color(0, 0, 0, 0));
            g2.setPaint(gpTop);
            g2.fillRect(0, 0, w, 100);

            GradientPaint gpBot = new GradientPaint(0, h, new Color(255, 0, 85, 40), 0, h - 100, new Color(0, 0, 0, 0));
            g2.setPaint(gpBot);
            g2.fillRect(0, h - 100, w, 100);
        }
    }

    private void resetScores() {
        scoreX = 0;
        scoreO = 0;
        scoreDraws = 0;
        updateScores();
    }

    private void updateScores() {
        if (isVsComputer) {
            scoreLabel.setText("USR_X: " + scoreX + "   ||   TIE: " + scoreDraws + "   ||   AI_O: " + scoreO);
        } else {
            scoreLabel.setText("USR_X: " + scoreX + "   ||   TIE: " + scoreDraws + "   ||   USR_O: " + scoreO);
        }
    }

    private void resetGame() {
        game.reset();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                cells[row][col].isWinningCell = false;
                cells[row][col].isHovered = false;
                cells[row][col].repaint();
            }
        }
        statusLabel.setText("[ SYS: X ACTIVE ]");
        statusLabel.setForeground(neonYellow);
    }

    private void processGameState() {
        if (game.isGameOver()) {
            if (game.getCurrentPlayer() == 'X') {
                statusLabel.setText("[ ! X WINS ! ]");
                statusLabel.setForeground(xColor);
                scoreX++;
                audioManager.playWinSound(); // Play victory anthem
            } else {
                statusLabel.setText(isVsComputer ? "[ ! AI WINS ! ]" : "[ ! O WINS ! ]");
                statusLabel.setForeground(oColor);
                scoreO++;
                if (isVsComputer)
                    audioManager.playLoseSound(); // Play defeat crash
                else
                    audioManager.playWinSound();
            }
            updateScores();
            highlightWinningCells();

            Timer timer = new Timer(1500, evt -> showGameOverDialog(
                    (game.getCurrentPlayer() == 'X' ? "SYSTEM OVERRIDE SUCCESS" : "CRITICAL FAILURE - DEFEAT")
                            + "\n< WINNER: " + game.getCurrentPlayer() + " >"));
            timer.setRepeats(false);
            timer.start();
        } else if (game.isBoardFull()) {
            statusLabel.setText("[ ! STALEMATE ! ]");
            statusLabel.setForeground(Color.LIGHT_GRAY);
            scoreDraws++;
            updateScores();

            audioManager.playErrorSound(); // Tie

            Timer timer = new Timer(1500, evt -> showGameOverDialog("STALEMATE REGISTERED. NO WINNER."));
            timer.setRepeats(false);
            timer.start();
        } else {
            game.switchPlayer();
            statusLabel.setText(game.getCurrentPlayer() == 'X' ? "[ SYS: X ACTIVE ]" : "[ SYS: O ACTIVE ]");
            statusLabel.setForeground(game.getCurrentPlayer() == 'X' ? neonYellow : Color.WHITE);
        }
    }

    private void showGameOverDialog(String message) {
        UIManager.put("OptionPane.background", new Color(10, 15, 20));
        UIManager.put("Panel.background", new Color(10, 15, 20));
        UIManager.put("OptionPane.messageForeground", neonYellow);
        UIManager.put("Button.background", neonBlue);
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Button.font", new Font("Monospaced", Font.BOLD, 14));

        Object[] options = { "> RECONNECT <", "> DISCONNECT <" };
        int n = JOptionPane.showOptionDialog(this,
                "<html><p style='text-align: center; font-family: Monospaced;'><b>"
                        + message.replace("\n", "<br>")
                        + "</b></p><br><p style='text-align: center;'>INITIATE NEW SEQUENCE?</p></html>",
                "SESSION TERMINATED",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]);

        if (n == JOptionPane.YES_OPTION) {
            audioManager.playSystemBeep();
            resetGame();
        } else if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
            System.exit(0);
        }
    }

    private void repaintAllCells() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                cells[row][col].repaint();
            }
        }
    }

    private void highlightWinningCells() {
        int[][] winLine = game.getWinningLine();
        if (winLine != null) {
            for (int[] pos : winLine) {
                cells[pos[0]][pos[1]].isWinningCell = true;
                cells[pos[0]][pos[1]].repaint();
            }
        }
    }

    private class CellPanel extends JPanel {
        private int row, col;
        protected boolean isWinningCell = false;
        protected boolean isHovered = false;

        public CellPanel(int row, int col) {
            this.row = row;
            this.col = col;
            setOpaque(false);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (game.isGameOver() || game.getBoard()[row][col] != ' ')
                        return;

                    // Human play (either X or O in PvP, or X in PvComputer)
                    if (game.getCurrentPlayer() == 'X' || !isVsComputer) {

                        audioManager.playUserMove(game.getCurrentPlayer() == 'X'); // user SFX

                        if (game.makeMove(row, col)) {
                            repaint();
                            processGameState();

                            // If it's computer's turn now
                            if (isVsComputer && !game.isGameOver() && game.getCurrentPlayer() == 'O') {
                                String difficulty = (String) difficultyCombo.getSelectedItem();
                                int thinkingTime = difficulty.contains("EXTREME") ? 800
                                        : (difficulty.contains("STANDARD") ? 500 : 300);

                                statusLabel.setText("[ AI: PROCESSING... ]");
                                statusLabel.setForeground(neonBlue);

                                Timer timer = new Timer(thinkingTime, evt -> {
                                    int[] compMove;

                                    if (difficulty.contains("EXTREME")) {
                                        compMove = game.pickBestMove();
                                    } else if (difficulty.contains("STANDARD")) {
                                        compMove = game.pickMediumMove();
                                    } else {
                                        compMove = game.pickRandomMove();
                                    }

                                    if (compMove != null) {
                                        audioManager.playUserMove(false); // AI SFX
                                        game.makeMove(compMove[0], compMove[1]);
                                        repaintAllCells();
                                        processGameState();
                                    }
                                });
                                timer.setRepeats(false);
                                timer.start();
                            }
                        }
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (game.getBoard()[row][col] == ' ' && !game.isGameOver()
                            && (game.getCurrentPlayer() == 'X' || !isVsComputer)) {
                        isHovered = true;
                        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Glowing base
            if (isWinningCell) {
                g2.setColor(winHighlightColor);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 0, 0); // Sharp corners for cyberpunk
            } else if (isHovered) {
                g2.setColor(hoverColor);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 0, 0);
            } else {
                g2.setColor(panelColor);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 0, 0);
            }

            // Cyberpunk border
            g2.setColor(neonBlue);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(0, 0, getWidth() - 2, getHeight() - 2);

            char symbol = game.getBoard()[row][col];
            if (symbol == ' ') {
                g2.dispose();
                return;
            }

            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            int padding = 30;
            int w = getWidth() - 2;
            int h = getHeight() - 2;

            if (symbol == 'X') {
                // Thicker Glow
                g2.setColor(xShadow);
                g2.setStroke(new BasicStroke(24, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
                g2.draw(new Line2D.Double(padding, padding, w - padding, h - padding));
                g2.draw(new Line2D.Double(w - padding, padding, padding, h - padding));

                // Actual Line (sharp edges)
                g2.setColor(xColor);
                g2.setStroke(new BasicStroke(10, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
                g2.draw(new Line2D.Double(padding, padding, w - padding, h - padding));
                g2.draw(new Line2D.Double(w - padding, padding, padding, h - padding));
            } else if (symbol == 'O') {
                // Thicker Glow
                g2.setColor(oShadow);
                g2.setStroke(new BasicStroke(24, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
                g2.draw(new Ellipse2D.Double(padding, padding, w - 2 * padding, h - 2 * padding));

                // Actual Line (sharp edges)
                g2.setColor(oColor);
                g2.setStroke(new BasicStroke(10, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
                g2.draw(new Ellipse2D.Double(padding, padding, w - 2 * padding, h - 2 * padding));
            }
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
            }
            new TicTacToeUI().setVisible(true);
        });
    }
}

/**
 * Uses Java's native MIDI library to generate pure synthesizer audio in
 * real-time
 * without needing external MP3s or WAVs. Perfect for Cyberpunk!
 */
class CyberSynthAudio {
    private Sequencer sequencer;
    private Synthesizer synth;
    private MidiChannel[] channels;

    public CyberSynthAudio() {
        try {
            synth = MidiSystem.getSynthesizer();
            synth.open();
            channels = synth.getChannels();

            // SFX Channel 1: Sci-Fi System Beeps
            channels[1].programChange(112); // Tinkle Bell / glitch
            // SFX Channel 2: Dramatic Synthesizer
            channels[2].programChange(81); // Sawtooth Wave

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startBackgroundMusic() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();

            Sequence sequence = new Sequence(Sequence.PPQ, 4);
            Track track = sequence.createTrack();

            // Instrument 38 = Synth Bass 1 on Channel 0
            ShortMessage sm = new ShortMessage();
            sm.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 38, 0);
            track.add(new MidiEvent(sm, 0));

            // A menacing, driving, cyclic arpeggio for the Cyberpunk tone
            int[] notes = { 36, 36, 43, 36, 39, 39, 46, 46 };
            int tick = 0;

            // Generate a looped track buffer
            for (int i = 0; i < 64; i++) {
                int note = notes[i % notes.length];

                ShortMessage noteOn = new ShortMessage();
                noteOn.setMessage(ShortMessage.NOTE_ON, 0, note, 80);
                track.add(new MidiEvent(noteOn, tick));

                tick += 2; // Duration

                ShortMessage noteOff = new ShortMessage();
                noteOff.setMessage(ShortMessage.NOTE_OFF, 0, note, 0);
                track.add(new MidiEvent(noteOff, tick));
            }

            sequencer.setSequence(sequence);
            sequencer.setTempoInBPM(130);
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY); // Infinite loop!
            sequencer.start();
        } catch (Exception e) {
        }
    }

    public void playUserMove(boolean isX) {
        if (channels == null)
            return;
        new Thread(() -> {
            int pitch = isX ? 72 : 60; // High frequency for X, Low frequency for O (AI)
            channels[1].noteOn(pitch, 110);
            try {
                Thread.sleep(120);
            } catch (Exception ignored) {
            }
            channels[1].noteOff(pitch);
        }).start();
    }

    public void playSystemBeep() {
        if (channels == null)
            return;
        new Thread(() -> {
            channels[1].noteOn(84, 90);
            try {
                Thread.sleep(60);
            } catch (Exception ignored) {
            }
            channels[1].noteOff(84);
        }).start();
    }

    public void playWinSound() {
        if (channels == null)
            return;
        new Thread(() -> {
            int[] arp = { 60, 64, 67, 72, 76, 79, 84, 88 }; // Uplifting synth chord run
            for (int note : arp) {
                channels[2].noteOn(note, 100);
                try {
                    Thread.sleep(60);
                } catch (Exception ignored) {
                }
                channels[2].noteOff(note);
            }
        }).start();
    }

    public void playLoseSound() {
        if (channels == null)
            return;
        new Thread(() -> {
            int[] loseSeq = { 50, 49, 48, 45, 40 }; // Dismal descending bass system crash
            for (int note : loseSeq) {
                channels[2].noteOn(note, 120);
                try {
                    Thread.sleep(180);
                } catch (Exception ignored) {
                }
                channels[2].noteOff(note);
            }
        }).start();
    }

    public void playErrorSound() {
        if (channels == null)
            return;
        new Thread(() -> {
            channels[2].noteOn(40, 110); // Heavy, dull buzz (Tie)
            try {
                Thread.sleep(400);
            } catch (Exception ignored) {
            }
            channels[2].noteOff(40);
        }).start();
    }
}

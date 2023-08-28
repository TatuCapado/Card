package GUI;

import models.Client;
import models.Card;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Interface {
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JPanel controlPanel;
    private JLabel statusLabel;
    private Socket socket;
    private ObjectOutputStream os;
    private DataInputStream din;
    private Map<String, java.io.Serializable> toServer;

    public Interface(Socket socket) throws IOException {
        this.socket = socket;
        os = new ObjectOutputStream(socket.getOutputStream());
        din = new DataInputStream(socket.getInputStream());
        prepareGUI();
    }


    private void prepareGUI() {
        mainFrame = new JFrame("RATB PASS");
        mainFrame.setLayout(new GridLayout(2, 1));

        headerLabel = new JLabel("", JLabel.CENTER);
        statusLabel = new JLabel("", JLabel.CENTER);

       // mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setBounds(100, 100, 550, 570);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                try {
                    os.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);
    }

    public void showContent() {
        headerLabel.setText("Choose an action:");
        JPanel panel = new JPanel();

        /**
         * Buttons area
         */
        // panel.setBackground(Color.darkGray);
        panel.setSize(500, 100);
        GridLayout layout = new GridLayout(1, 4);
        layout.setHgap(10);
        layout.setVgap(10);


        JButton addButton = new JButton("Add card");
        JButton rechargeButton = new JButton("Recharge card");
        JButton validateButton = new JButton("Validate card");
        JButton verifyButton = new JButton("Verify card");

        panel.setLayout(layout);
        panel.add(addButton);
        panel.add(rechargeButton);
        panel.add(validateButton);
        panel.add(verifyButton);
        controlPanel.add(panel);

        /**
         * Card Layout for input data
         */
        final JPanel contentPanel = new JPanel();
        contentPanel.setPreferredSize(new Dimension(500, 200));

        CardLayout layout2 = new CardLayout();
        layout2.setHgap(10);
        layout2.setVgap(10);
        contentPanel.setLayout(layout2);

        /**
         * Settings for cards
         */
        Border margin = new EmptyBorder(10, 10, 10, 10);
        GridBagLayout panelGridBagLayout = new GridBagLayout();
        panelGridBagLayout.columnWidths = new int[]{86, 86, 0};
        panelGridBagLayout.rowHeights = new int[]{20, 20, 20, 20, 20, 0};
        panelGridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        panelGridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 10;

        /**
         * First card (add)
         */
        JPanel addCardPanel = new JPanel();
        Border borderCardPanel = addCardPanel.getBorder();
        addCardPanel.setBorder(new CompoundBorder(borderCardPanel, margin));
        addCardPanel.setLayout(panelGridBagLayout);

        addLabel("First Name:", 0, addCardPanel);
        JTextField firstNameAC = addTextField(0, addCardPanel);
        addLabel("Last Name:", 1, addCardPanel);
        JTextField lastNameAC = addTextField(1, addCardPanel);
        JButton doneBtnAC = new JButton("Done");

        addCardPanel.add(doneBtnAC, c);


        doneBtnAC.addActionListener(e -> {
            toServer = new HashMap<>();
            String whatToDo = "addClient";
            Client aux = new Client();
            aux.setFirstName(firstNameAC.getText());
            aux.setLastName(lastNameAC.getText());
            toServer.put(whatToDo, aux);

            dataForServer();
            System.out.println("New card created");
            statusLabel.setForeground(Color.GREEN);
            statusLabel.setText("NEW CARD CREATED");
            toServer.clear();
        });

        /**
         * Second card (recharge)
         */
        JPanel rechargeCardPanel = new JPanel();
        Border borderRechargeCardPanel = rechargeCardPanel.getBorder();
        rechargeCardPanel.setBorder(new CompoundBorder(borderRechargeCardPanel, margin));
        rechargeCardPanel.setLayout(panelGridBagLayout);

        addLabel("First Name:", 0, rechargeCardPanel);
        JTextField firstNameRC = addTextField(0, rechargeCardPanel);
        addLabel("Last Name:", 1, rechargeCardPanel);
        JTextField lastNameRC = addTextField(1, rechargeCardPanel);

        addLabel("Pass Type:", 2, rechargeCardPanel);
        c.gridx = 1;
        String[] types = {"Rechargeable", "Monthly Pass", "Daily Pass"};

        JComboBox<String> passList = new JComboBox<>(types);
        passList.setSelectedIndex(0);
        rechargeCardPanel.add(passList, c);


        addLabel("Price/Money: ", 3, rechargeCardPanel);
        JTextField priceRC = addTextField(3, rechargeCardPanel);
        priceRC.setEditable(true);
        passList.addActionListener(e -> {
            String type = passList.getItemAt(passList.getSelectedIndex());

            switch (type) {
                case "Rechargeable":
                    priceRC.setText("");
                    priceRC.setEditable(true);
                    break;
                case "Monthly Pass":
                    priceRC.setText("25");
                    priceRC.setEditable(false);
                    break;
                case "Daily Pass":
                    priceRC.setText("10");
                    priceRC.setEditable(false);
                    break;
            }
        });
        JButton doneBtnRC = new JButton("Done");
        c.gridy = 4;
        c.gridx = 0;
        rechargeCardPanel.add(doneBtnRC, c);

        doneBtnRC.addActionListener(e -> {
            toServer = new HashMap<>();
            String whatToDo = "chargePass";
            Card card = new Card();
            Client person = new Client();
            person.setFirstName(firstNameRC.getText());
            person.setLastName(lastNameRC.getText());
            card.setPerson(person);
            card.setPass_type(passList.getItemAt(passList.getSelectedIndex()));
            card.setPass_price(Integer.parseInt(priceRC.getText()));

            toServer.put(whatToDo, card);
            dataForServer();
            System.out.println("Recharge made");
            statusLabel.setForeground(Color.GREEN);
            statusLabel.setText("TRANSFER SUCCESSFUL!");
            toServer.clear();
        });

        /**
         * Third card(validate)
         */
        JPanel validateCardPanel = new JPanel();
        Border borderValidateCardPanel = validateCardPanel.getBorder();
        validateCardPanel.setBorder(new CompoundBorder(borderValidateCardPanel, margin));
        validateCardPanel.setLayout(panelGridBagLayout);

        addLabel("First Name:", 0, validateCardPanel);
        JTextField firstNameVC = addTextField(0, validateCardPanel);
        addLabel("Last Name:", 1, validateCardPanel);
        JTextField lastNameVC = addTextField(1, validateCardPanel);

        addLabel("Line:", 2, validateCardPanel);
        c.gridy = 2;
        c.gridx = 1;
        String[] lines = {"41", "336", "783"};

        JComboBox<String> linesList = new JComboBox<>(lines);
        linesList.setSelectedIndex(0);
        validateCardPanel.add(linesList, c);

        JButton doneBtnVC = new JButton("Done");
        c.gridy = 3;
        c.gridx = 0;
        validateCardPanel.add(doneBtnVC, c);

        doneBtnVC.addActionListener(e -> {
            toServer = new HashMap();
            String whatToDo = "validateCard";
            Card card = new Card();
            Client person = new Client();
            person.setFirstName(firstNameVC.getText());
            person.setLastName(lastNameVC.getText());
            card.setPerson(person);
            card.setLine_validation(Integer.parseInt(linesList.getItemAt(linesList.getSelectedIndex())));

            toServer.put(whatToDo, card);
            dataForServer();
            System.out.println("Validation made");
            statusLabel.setForeground(Color.GREEN);
            statusLabel.setText("VALIDATION SUCCESSFUL!");
            toServer.clear();
        });

        /**
         * Fourth card (verify)
         */
        JPanel verifyCardPanel = new JPanel();
        Border borderVerifyCardPanel = verifyCardPanel.getBorder();
        verifyCardPanel.setBorder(new CompoundBorder(borderVerifyCardPanel, margin));
        verifyCardPanel.setLayout(panelGridBagLayout);

        addLabel("First Name:", 0, verifyCardPanel);
        JTextField firstNameVrC = addTextField(0, verifyCardPanel);
        addLabel("Last Name:", 1, verifyCardPanel);
        JTextField lastNameVrC = addTextField(1, verifyCardPanel);

        addLabel("Validation Line:", 2, verifyCardPanel);
        c.gridy = 2;
        c.gridx = 1;

        JComboBox<String> linesList2 = new JComboBox<>(lines);
        linesList2.setSelectedIndex(0);
        verifyCardPanel.add(linesList2, c);

        JButton doneBtnVrC = new JButton("Done");
        c.gridy = 3;
        c.gridx = 0;
        verifyCardPanel.add(doneBtnVrC, c);

        doneBtnVrC.addActionListener(e -> {
            toServer = new HashMap();
            String whatToDo = "verifyCard";
            Card card = new Card();
            Client person = new Client();
            person.setFirstName(firstNameVrC.getText());
            person.setLastName(lastNameVrC.getText());
            card.setPerson(person);
            card.setLine_validation(Integer.parseInt(linesList2.getItemAt(linesList2.getSelectedIndex())));

            toServer.put(whatToDo, card);
            dataForServer();
            toServer.clear();

            try {
                if (din.readUTF().equals("false")) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("AMENDA!");
                } else {
                    statusLabel.setForeground(Color.GREEN);
                    statusLabel.setText("VALIDAT!");
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Verification made");
        });

        contentPanel.add("AddCard", addCardPanel);
        contentPanel.add("RechargeCard", rechargeCardPanel);
        contentPanel.add("ValidateCard", validateCardPanel);
        contentPanel.add("VerifyCard", verifyCardPanel);

        addButton.addActionListener(e -> {
            setCardLayout(contentPanel, "AddCard");
            statusLabel.setText("");
        });
        rechargeButton.addActionListener(e -> {
            setCardLayout(contentPanel, "RechargeCard");
            statusLabel.setText("");
        });
        validateButton.addActionListener(e -> {
            setCardLayout(contentPanel, "ValidateCard");
            statusLabel.setText("");
        });
        verifyButton.addActionListener(e -> {
            setCardLayout(contentPanel, "VerifyCard");
            statusLabel.setText("");
        });


        controlPanel.add(contentPanel);
        controlPanel.add(statusLabel);
        mainFrame.setVisible(true);
    }

    private void dataForServer() {
        try {
            os.writeObject(toServer);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void setCardLayout(JPanel contentPanel, String panel) {
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, panel);
    }

    private JTextField addTextField(int yPos, Container containingPanel) {

        JTextField textField = new JTextField();
        GridBagConstraints gridBagConstraintForTextField = new GridBagConstraints();
        gridBagConstraintForTextField.fill = GridBagConstraints.BOTH;
        gridBagConstraintForTextField.insets = new Insets(0, 0, 5, 0);
        gridBagConstraintForTextField.gridx = 1;
        gridBagConstraintForTextField.gridy = yPos;
        containingPanel.add(textField, gridBagConstraintForTextField);
        textField.setColumns(10);
        return textField;
    }

    private void addLabel(String labelText, int yPos, Container containingPanel) {
        JLabel label = new JLabel(labelText);
        GridBagConstraints gridBagConstraintForLabel = new GridBagConstraints();
        gridBagConstraintForLabel.fill = GridBagConstraints.BOTH;
        gridBagConstraintForLabel.insets = new Insets(0, 0, 5, 5);
        gridBagConstraintForLabel.gridx = 0;
        gridBagConstraintForLabel.gridy = yPos;
        containingPanel.add(label, gridBagConstraintForLabel);
    }
}

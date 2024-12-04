
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout; 
import java.awt.CardLayout;
import java.awt.FlowLayout;
 
import javax.swing.JFrame;	
import javax.swing.JPanel;
 
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPasswordField;
import javax.swing.JOptionPane;
 
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;	
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Timer;

public class UI
{
    public static void main(String[] args)
    {
        UI u = new UI();
        u.run();
    }

    public void run()
    {
        JFrame gameFrame = new JFrame("PizzaStore");
        gameFrame.setDefaultCloseOperation(gameFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(800, 800);
        gameFrame.setLocation(410, 320);
        PizzaStorePanels phPan =  new PizzaStorePanels();
        gameFrame.getContentPane().add(phPan);
        gameFrame.setVisible(true);
    }
}

class PizzaStorePanels extends JPanel
{
    private CardLayout cards;
    private JButton login, userCreate,back;

    public PizzaStorePanels()
    {
        cards = new CardLayout();
        setLayout(cards);

        JPanel firstPage = makeFirstPage();
        add(firstPage, "Login page");

        JPanel loginPanel = makeLoginForm();
        add(loginPanel, "Login Form");
        
        JPanel signupPanel = makeSignupPage();
        add(signupPanel, "Signup Form");

        JPanel menuPanel = makeMenuPage();
        add(menuPanel, "Menu");

        JPanel welcomePanel = makeWelcomePage();
        add(welcomePanel, "Welcome Page");

        JPanel placeOrderPage = makePlaceOrderPage();
        add(placeOrderPage, "Place Order");

        JPanel orderHistoryPage = makeOrderHistoryPage();
        add(orderHistoryPage, "Order History");

        LoginButtonHandler lg = new LoginButtonHandler(cards, this);
        login.addActionListener(lg);

        UserCreateButtonHandler uc = new UserCreateButtonHandler(cards, this);
        userCreate.addActionListener(uc);
        BackButtonHandler b = new BackButtonHandler(cards, this);
        back.addActionListener(b);
    }
    
    public JPanel makeFirstPage()
    {

        JPanel firstPage = new JPanel();
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(3, 1));

        JLabel title = new JLabel("Pizza Store", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        login = new JButton("Login");
        userCreate = new JButton("Signup");
        JButton quit = new JButton("Quit");

        QuitButtonHandler q = new QuitButtonHandler(cards, this);
        quit.addActionListener(q);
        login.addActionListener(e->cards.show(this, "Welcome Page"));

        buttons.add(title);
        buttons.add(login);
        buttons.add(userCreate);
        buttons.add(quit);
        firstPage.setLayout(new BorderLayout());
        firstPage.add(buttons, BorderLayout.CENTER);

        return firstPage;
    }

    public JPanel makeWelcomePage() 
    {
        JPanel welcomePage = new JPanel(new GridLayout(5, 1, 10, 10));

        JLabel welcomeLabel = new JLabel("Welcome to Pizza Store", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton viewMenu = new JButton("View Menu");
        viewMenu.addActionListener(e -> cards.show(this, "Menu"));

        JButton placeOrder = new JButton("Place Order");
        placeOrder.addActionListener(e -> cards.show(this, "Place Order"));

        JButton viewOrders = new JButton("View Orders");
        viewOrders.addActionListener(e -> cards.show(this, "Order History"));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> cards.show(this, "Login page"));

        welcomePage.add(welcomeLabel);
        welcomePage.add(viewMenu);
        welcomePage.add(placeOrder);
        welcomePage.add(viewOrders);
        welcomePage.add(logoutButton);

        return welcomePage;
    }

    public JPanel makeMenuPage() 
    {
        JPanel menuPage = new JPanel(new BorderLayout());

        JLabel menuLabel = new JLabel("Pizza Store Menu", JLabel.CENTER);
        menuLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JTextArea menuArea = new JTextArea();
        menuArea.setEditable(false);
        menuArea.setText("Fetching menu from the database...");

   
        back = new JButton("Back");
        menuPage.add(menuLabel, BorderLayout.NORTH);
        menuPage.add(new JScrollPane(menuArea), BorderLayout.CENTER);
        menuPage.add(back, BorderLayout.SOUTH);

        return menuPage;
    }

    public JPanel makeLoginForm() 
    {
        JPanel loginPage = new JPanel(new GridLayout(4, 1, 10, 10));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JTextField passField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> cards.show(this, "Login Form"));

        loginPage.add(userLabel);
        loginPage.add(userField);
        loginPage.add(passLabel);
        loginPage.add(passField);
        loginPage.add(loginButton);

        return loginPage;
    }

    public JPanel makeSignupPage() 
    {
        JPanel signupPage = new JPanel(new GridLayout(5, 1, 10, 10));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JTextField passField = new JPasswordField();
        JLabel phoneLabel = new JLabel("Phone Number:");
        JTextField phoneField = new JTextField();

        JButton signupButton = new JButton("Signup");
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cards.show(this, "Login page"));
        signupButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Signup successful!");
            cards.show(this, "Login Page");
        });

        signupPage.add(userLabel);
        signupPage.add(userField);
        signupPage.add(passLabel);
        signupPage.add(passField);
        signupPage.add(phoneLabel);
        signupPage.add(phoneField);
        signupPage.add(signupButton);
        signupPage.add(backButton);

        return signupPage;
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0,0,800,800);
    }


public JPanel makeOrderHistoryPage() 
{
    JPanel orderHistoryPage = new JPanel(new BorderLayout());

    JLabel historyLabel = new JLabel("Order History", JLabel.CENTER);
    historyLabel.setFont(new Font("Arial", Font.BOLD, 24));

    JTextArea orderHistoryArea = new JTextArea();
    orderHistoryArea.setEditable(false);
    orderHistoryArea.setText("Fetching order history...\n");

    JButton backButton = new JButton("Back to Welcome");
    backButton.addActionListener(e -> cards.show(this, "Welcome Page"));

    orderHistoryPage.add(historyLabel, BorderLayout.NORTH);
    orderHistoryPage.add(new JScrollPane(orderHistoryArea), BorderLayout.CENTER);
    orderHistoryPage.add(backButton, BorderLayout.SOUTH);

    return orderHistoryPage;
}

public JPanel makePlaceOrderPage() 
{
    JPanel placeOrderPage = new JPanel(new BorderLayout());

    JLabel orderLabel = new JLabel("Place Your Order", JLabel.CENTER);
    orderLabel.setFont(new Font("Arial", Font.BOLD, 24));

    JPanel orderForm = new JPanel(new GridLayout(3, 2, 10, 10));
    JLabel storeLabel = new JLabel("Enter Store ID:");
    JTextField storeField = new JTextField();
    JLabel itemLabel = new JLabel("Enter Item:");
    JTextField itemField = new JTextField();
    JLabel quantityLabel = new JLabel("Enter Quantity:");
    JTextField quantityField = new JTextField();

    JButton addItemButton = new JButton("Add Item");
    JButton placeOrderButton = new JButton("Place Order");

    JTextArea orderSummary = new JTextArea("Order Summary:\n");
    orderSummary.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(orderSummary);

    // Dynamic total price tracking
    JLabel totalPriceLabel = new JLabel("Total Price: $0.00");

    JButton backButton = new JButton("Back to Welcome");
    backButton.addActionListener(e -> cards.show(this, "Welcome Page"));

    orderForm.add(storeLabel);
    orderForm.add(storeField);
    orderForm.add(itemLabel);
    orderForm.add(itemField);
    orderForm.add(quantityLabel);
    orderForm.add(quantityField);

    JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
    buttonPanel.add(addItemButton);
    buttonPanel.add(placeOrderButton);
    buttonPanel.add(backButton);

    JPanel totalPanel = new JPanel(new BorderLayout());
    totalPanel.add(totalPriceLabel, BorderLayout.EAST);


    placeOrderPage.add(orderLabel, BorderLayout.NORTH);
    placeOrderPage.add(orderForm, BorderLayout.CENTER);
    placeOrderPage.add(scrollPane, BorderLayout.EAST);
    placeOrderPage.add(buttonPanel, BorderLayout.SOUTH);
    placeOrderPage.add(totalPanel, BorderLayout.NORTH);
    placeOrderPage.add(new JScrollPane(orderSummary), BorderLayout.CENTER);
    placeOrderPage.add(backButton, BorderLayout.SOUTH);

    return placeOrderPage;
}
}

class UserCreateButtonHandler implements ActionListener 
{
    private CardLayout card;
    private PizzaStorePanels createPan;

    public UserCreateButtonHandler(CardLayout panel, PizzaStorePanels createPanel) 
    {
        card = panel;
        createPan = createPanel;
    }

    public void actionPerformed(ActionEvent evt) 
    {
        String click = evt.getActionCommand();
        if (click.equals("Signup")) 
        {
            card.show(createPan, "Signup Form");
        }
    }
}

class BackButtonHandler implements ActionListener
{
    private CardLayout card;
    private PizzaStorePanels backPan;

    public BackButtonHandler(CardLayout panel, PizzaStorePanels backPanel)
    {
        card = panel;
        backPan = backPanel;
    }

    public void actionPerformed(ActionEvent evt)
    {
        String click = evt.getActionCommand();
        if(click.equals("Back"))
        {
            card.show(backPan, "Login page");
        }
    }
}


class LoginButtonHandler implements ActionListener
{
    private CardLayout card;
    private PizzaStorePanels loginPan;

    public LoginButtonHandler(CardLayout panel, PizzaStorePanels loginPanel)
    {
        card = panel;
        loginPan = loginPanel;
    }

    public void actionPerformed(ActionEvent evt)
    {
        String click = evt.getActionCommand();
        if(click.equals("Login"))
        {
            card.show(loginPan, "Welcome");
        }
    }
}	

class QuitButtonHandler implements ActionListener
{
    private CardLayout card;
    private PizzaStorePanels quitPan;

    public QuitButtonHandler(CardLayout panel, PizzaStorePanels quitPanel)
    {
        card = panel;
        quitPan = quitPanel;
    }

    public void actionPerformed(ActionEvent evt)
    {
        String click = evt.getActionCommand();
        if(click.equals("Quit"))
		{
			System.exit(0);
 
		}
    }
}
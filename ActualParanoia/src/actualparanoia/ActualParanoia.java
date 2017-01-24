package actualparanoia;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class ActualParanoia {
    
    
    public static void main(String[] args) {
        try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
        }
		
        TheGame window = new TheGame();
        
        window.setVisible(true);
        
        window.start();
    }
    
}

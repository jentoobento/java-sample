import java.awt.Font;
import java.text.DecimalFormat;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class CheckingAcctMain{
    public static JFrame frame;
    public static Transaction t;
    public static Account A1;
   public static boolean InitialBalanceFlag = false;

    public static void main (String[] args){
       frame = new JFrame ("The Bank of El Camino");
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       Panel panel = new Panel();
       frame.getContentPane().add (panel);
       frame.pack();
       frame.setVisible(true);
    }

    public static void enterTransaction(){
        DecimalFormat fmt = new DecimalFormat("$#,##0.00;($#,##0.00)");
        String balanceStr, transCodeStr, transAmtStr, message;
        double balance, transAmt;
        int transCode, again;
        boolean flagUnder500 = false;

        if(InitialBalanceFlag == false){   
            balanceStr = JOptionPane.showInputDialog ("Enter the inital balance: ");
            balance = Double.parseDouble(balanceStr);    
            A1 = new Account(balance);
            InitialBalanceFlag = true;
        }
        t = new Transaction(A1.getTransCount(), 0, A1.getBalance()); 
        A1.addTrans(t);
        frame.setVisible(false);
        do {
            transCodeStr = JOptionPane.showInputDialog ("Enter option:\n1 - Withdraw\n2 - Deposit");
            transCode = Integer.parseInt(transCodeStr);
            while(transCode<1||transCode>2){
                transCodeStr = JOptionPane.showInputDialog ("Invalid option.\nEnter option:\n1 - Withdraw\n2 - Deposit");
                transCode = Integer.parseInt(transCodeStr); 
            }
            A1.setTransCode(transCode);   
            transAmtStr = JOptionPane.showInputDialog ("Enter the transaction amount: ");
            transAmt = Double.parseDouble(transAmtStr); 
            A1.setTransAmt(transAmt); 
            t = new Transaction(A1.getTransCount(), transCode, transAmt); 
            A1.addTrans(t);
            message = "Balance: "+fmt.format(A1.getBalance())+"\n";   
            message+="Transaction Amount: "+fmt.format(A1.getTransAmt())+"\n";             
            A1.setBalance(transCode, transAmt);
            message+="Transaction Code: ";  
                if (A1.getTransCode()==1) {
                    message+= "Withdrawal\nService Charge: $0.15\n";
                    A1.setBalance(1, 0.15);
                    A1.setTotalServiceCharge(0.15);
                    t = new Transaction(A1.getTransCount(), 3, 0.15); 
                    A1.addTrans(t);
                }
                if (A1.getTransCode()==2){
                    message+= "Deposit\nService Charge: $0.10\n";
                    A1.setBalance(1, 0.1);
                    A1.setTotalServiceCharge(0.1);
                    t = new Transaction(A1.getTransCount(), 3, 0.1); 
                    A1.addTrans(t);
                }
                if(A1.getBalance()<500.0 && flagUnder500==false && transCode==1) {
                    A1.setBalance(1, 5.0);
                    A1.setTotalServiceCharge(5.0);
                    t = new Transaction(A1.getTransCount(), 3, 5.0); 
                    A1.addTrans(t);
                    message+= "Account below $500.00. Service Charge: $5.00\n";
                    flagUnder500 = true;
                }  
                if(A1.getBalance() <50.0) {
                    message += "Warning! Balance is less than $50.00\n";
                } 
                if(A1.getBalance() < 0.0) {
                    A1.setBalance(1, 10.0);
                    A1.setTotalServiceCharge(10.0);
                    t = new Transaction(A1.getTransCount(), 3, 10.0); 
                    A1.addTrans(t);
                    message += "Account Overdrawn. Service Charge: $10.00\n";
                }
                message += "New balance is: "+fmt.format(A1.getBalance())+"\n";   
                message+="Current Total Service Charges: "+
                    fmt.format(A1.getTotalServiceCharge())+"\n";
                JOptionPane.showMessageDialog (null, message); 
                again = JOptionPane.showConfirmDialog (null, "Make another transaction?");
            } while (again == JOptionPane.YES_OPTION);
            frame.setVisible(true);
    }

    public static void listTransactions(){
        DecimalFormat fmt = new DecimalFormat("$#,##0.00;($#,##0.00)");
        JTextArea text = new JTextArea();
        String message = "";
        text.setOpaque(false);
        text.setFont(new Font("Monospaced", Font.PLAIN, 20));
        text.setBorder(null);
        message += "List of all Transactions\n\n";
        message += String.format("%-4s%-17s%10s\n", "#", "Transaction Id", "Amount");
        for (int x = 0; x < A1.getTransCount(); x++) {
            t = A1.getTrans(x);
            message += String.format("%-4d", t.getTransNum());
            if(t.getTransId() == 1){
                message += String.format("%-17s", "Withdrawal");
            }
            if(t.getTransId() == 2){
                message += String.format("%-17s", "Deposit");
            }
            if(t.getTransId() == 3) {
                message += String.format("%-17s", "Service Charge");
            }
            if(t.getTransId() == 0){
                message += String.format("%-17s", "Balance");
            }
            message += String.format("%10s", fmt.format(t.getTransAmt())) + "\n";
        }
        text.setText(message);
        JOptionPane.showMessageDialog(null, text);
    }

    public static void listWithdraws(){
        DecimalFormat fmt = new DecimalFormat("$#,##0.00;($#,##0.00)");
        JTextArea text = new JTextArea();
        String message = "";
        text.setOpaque(false);
        text.setFont(new Font("Monospaced", Font.PLAIN, 20));
        text.setBorder(null);
        message += "All Withdrawals\n\n";
        message += String.format("%-4s%-17s%10s\n", "#", "Transaction Id", "Amount");
        for (int x = 0; x < A1.getTransCount(); x++){
            t = A1.getTrans(x);
            if(t.getTransId() == 1){
                message += String.format("%-4d", t.getTransNum());
                message += String.format("%-17s", "Withdrawal");
                message += String.format("%10s", fmt.format(t.getTransAmt())) + "\n";
            }
            if(t.getTransId() == 3){
                message += String.format("%-4d", t.getTransNum());
                message += String.format("%-17s", "Service Charge");
                message += String.format("%10s", fmt.format(t.getTransAmt())) + "\n";
            }
        }
        text.setText(message);
        JOptionPane.showMessageDialog(null, text);
    }
    
    public static void listDesposits(){
        DecimalFormat fmt = new DecimalFormat("$#,##0.00;($#,##0.00)");
        JTextArea text = new JTextArea();
        String message = "";
        text.setOpaque(false);
        text.setFont(new Font("Monospaced", Font.PLAIN, 20));
        text.setBorder(null);
        message += "All Deposits\n\n";
        message += String.format("%-4s%-17s%10s\n", "#", "Transaction Id", "Amount");
        for (int x = 0; x < A1.getTransCount(); x++) {
            t = A1.getTrans(x);
            if(t.getTransId() == 0) {
                message += String.format("%-4d", t.getTransNum());
                message += String.format("%-17s", "Balance");
                message += String.format("%10s", fmt.format(t.getTransAmt())) + "\n";
            }
            if(t.getTransId() == 2){
                message += String.format("%-4d", t.getTransNum());
                message += String.format("%-17s", "Deposit");
                message += String.format("%10s", fmt.format(t.getTransAmt())) + "\n";
            }
        }
        text.setText(message);
        JOptionPane.showMessageDialog(null, text);
    }
}

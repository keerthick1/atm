
import java.sql.*;
import java.util.Scanner;

public class home {
	static int twoThsnd =0, fivHund = 0, hund = 0, atmTotal = 0;
	
	
	public static Connection con;
	static Scanner sc = new Scanner(System.in);
	
	
	public static void main(String[] args) throws Exception {
//		Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm", "root", "#Keer2003#");
		char ch;
		do {
			System.out.println("1. Load Amount in ATM.\n2. Show Customer Details\n3. Check Balance\n4. Withdraw Money\n5. Transfer Money\n6. Atm Balance");
			System.out.println("Enter the operation to be performed : ");
			int n = sc.nextInt();
			int[] a = new int[3];
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select * from atm_data");
			int i=0;
			while(rs.next()) {
				a[i++] = rs.getInt("number");
				atmTotal += rs.getInt("demonination")*rs.getInt("number");
			}
			twoThsnd = a[0];
			fivHund = a[1];
			hund = a[2];
//			atmTotal = a[0] + a[1] + a[2];
			switch(n) {
			case 1:
				loadAmount();
				break;
			case 2:
				showCustomerDetails();
				break;
			case 3:
				balance();
				break;
			case 4:
				withdraw();
				break;
			case 5:
				transfer();
				break;
			case 6:
				atmBalance();
				break;
			default:
				System.out.println("Wrong choice");
			}
			
			System.out.println("\nDo you want to continue ? (y/n)");
			ch = sc.next().charAt(0);
		
		}while(ch=='y');
		if(ch=='n')
			System.out.println("Thank you...");
	}
	
	static boolean pinExist(int acNo, int p) throws Exception{
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("select * from customer");
		while(rs.next()) {
			int pin = rs.getInt("pin");
			int an = rs.getInt("acc_no");
			if(p==pin && an==acNo) {
				return true;
			}
		}
		return false;
	}
	
	static void loadAmount() throws Exception {
		Statement st = con.createStatement();
		
		System.out.print("Enter no. of Rs. 2000 need to be added : ");
		int twoThousand = sc.nextInt();

		System.out.print("Enter no. of Rs. 500 need to be added : ");
		int fiveHundred = sc.nextInt();

		System.out.print("Enter no. of Rs. 100 need to be added : ");
		int hundred = sc.nextInt();

		st.executeUpdate("update atm_data set number = number + " + twoThousand + ", value = (number*demonination) where demonination = 2000");
		st.executeUpdate("update atm_data set number = number + " + fiveHundred + ", value = (number*demonination) where demonination = 500");
		st.executeUpdate("update atm_data set number = number + " + hundred + ", value = (number*demonination) where demonination = 100");
		System.out.println("successfully updated");
//		System.out.println(twoThsnd + " " + fivHund + " " + hund);


		ResultSet rs = st.executeQuery("select * from atm_data");
		
		System.out.println("Denomination\tNumber\tValue\n");
		
		while(rs.next()) {
			System.out.println(rs.getInt("demonination") + "\t\t" + rs.getInt("number") + "\t" + rs.getInt("value"));
		}
		
		System.out.println("Total\t\t\t" + atmTotal);
	}
	
	static void showCustomerDetails() throws Exception {
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("select * from customer");
		
		System.out.println("name\tAcc_no\tpin\tbalance");
		
		while(rs.next()) {
			System.out.println(rs.getString("name") + "\t" + rs.getInt("acc_no") + "\t" + rs.getInt("pin") + "\t" + rs.getInt("balance"));
		}
	}
	
	static void balance() throws Exception {
		Statement st = con.createStatement();
		System.out.println("Enter the Account Number : ");
		int acNo = sc.nextInt();
		
		System.out.println("Enter the Account Pin : ");
		int pin = sc.nextInt();
		
		if(pinExist(acNo, pin)) {
			System.out.println("Enter the Account Holder Name : ");
			String p = sc.next();
			
			ResultSet rs = st.executeQuery("select * from customer where name = '" + p + "'");

			System.out.println("name\tAcc_no\tpin\tbalance");
			
			while(rs.next()) {
				System.out.println(rs.getString("name") + "\t" + rs.getInt("acc_no") + "\t" + rs.getInt("pin") + "\t" + rs.getInt("balance"));
			}
		}
		else
		{
			System.out.println("No such user exist...");
		}
	}
	
	static void withdraw() throws Exception {
		
		Statement st = con.createStatement();
		System.out.println("Enter the Account Number : ");
		int acNo = sc.nextInt();
		
		System.out.println("Enter the Account Pin : ");
		int pin = sc.nextInt();
		
		if(pinExist(acNo, pin)) {
			System.out.println("Enter the Withdraw Amount: ");
			int withdraw = sc.nextInt();
			System.out.println("Enter the Account Holder Name : ");
			String p = sc.next();
			
			ResultSet rs = st.executeQuery("select * from customer where name = '" + p + "'");
			
			int actualAmt = 0;
			while(rs.next()) {
				actualAmt = rs.getInt("balance");
			}
			
			int temp = withdraw;
//			System.out.println(actualAmt);
			if(withdraw <= actualAmt) {
				if(withdraw <= 10000 && withdraw >=100) {
					
					st.executeUpdate("update customer set balance = (balance - " + withdraw + ") where name = '" + p + "'");
					System.out.println("Successfully updated...");
					atmTotal -= withdraw;
				}
				else {
					System.out.println("Insuffiecient Amount....");
				}
			}
			else
			{
				System.out.println("No such user exist...");
			}
			}
		}
	
	
	static void transfer() throws Exception{
		Statement st = con.createStatement();
		System.out.println("Enter the Account Number of the sender : ");
		int acNo1 = sc.nextInt();
		
		System.out.println("Enter the Account Pin of the sender : ");
		int pin1 = sc.nextInt();

		System.out.println("Enter the Account Number of the receiver : ");
		int acNo2 = sc.nextInt();
		
		System.out.println("Enter the Account Pin of the receiver : ");
		int pin2 = sc.nextInt();
		
		if(pinExist(acNo1, pin1) && pinExist(acNo2, pin2)) {
			System.out.println("Enter the Account Holder Name of the sender : ");
			String p1 = sc.next();

			System.out.println("Enter the Account Holder Name of the reciever : ");
			String p2 = sc.next();
			
			System.out.println("Enter the amount to be transfered : ");
			int x = sc.nextInt();
			
			ResultSet rs = st.executeQuery("select * from customer where name = '" + p1 + "'");
			
			int actualAmt = 0;
			while(rs.next()) {
				actualAmt = rs.getInt("balance");
			}
			
			System.out.println(actualAmt);
			
			if(actualAmt >= x) {
				st.executeUpdate("update customer set balance = (balance -  " + x + ") where name = '" + p1 + "'");
				System.out.println("Amount has been debittted....");
				
				st.executeUpdate("update customer set balance = (balance + " + x + ") where name = '" + p2 + "'");
				System.out.println("Amount has been credited....");
				
				System.out.println("Transaction successfull....");
			}else {
				System.out.println("Insufficient Amount....");
			}
		}
		else
		{
			System.out.println("No such user exist...");
		}
	}
	
	static void atmBalance() throws Exception{
		System.out.println("The atm Balance is : " + atmTotal);
	}
}

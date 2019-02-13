package com.cg.banking.services;

import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cg.banking.beans.Account;
import com.cg.banking.beans.Transaction;
import com.cg.banking.daoservices.AccountDAO;
import com.cg.banking.daoservices.TransactionDAO;
import com.cg.banking.exceptions.AccountBlockedException;
import com.cg.banking.exceptions.AccountNotFoundException;
import com.cg.banking.exceptions.BankingServicesDownException;
import com.cg.banking.exceptions.InsufficientAmountException;
import com.cg.banking.exceptions.InvalidAccountTypeException;
import com.cg.banking.exceptions.InvalidAmountException;
import com.cg.banking.exceptions.InvalidPinNumberException;

@Service
public class BankingServicesImpl implements BankingServices {

	@Autowired
	private AccountDAO accountDao;
	@Autowired
	private TransactionDAO transactionDao;
	
	public AccountDAO getAccountDao() {
		return accountDao;
	}
	public void setAccountDao(AccountDAO accountDao) {
		this.accountDao = accountDao;
	}
	public TransactionDAO getTransactionDao() {
		return transactionDao;
	}
	public void setTransactionDao(TransactionDAO transactionDao) {
		this.transactionDao = transactionDao;
	}
	
	public BankingServicesImpl() {	}

	@Override
	public Account openAccount(Account account)
			throws InvalidAmountException, InvalidAccountTypeException, BankingServicesDownException {
		if(!(account.getAccountType().equals("Salary")||account.getAccountType().equals("Savings")||account.getAccountType().equals("Current"))) throw new InvalidAccountTypeException("Please Enter a valid account type.");
		account.setAccountType(account.getAccountType());
		if(account.getAccountBalance()<500) throw new InvalidAmountException("Please enter a valid Initial amount.");
		account.setAccountBalance(account.getAccountBalance());
		account.setPinNumber(Integer.parseInt(String.format("%04d", new Random().nextInt(8999)+1000)));
		account.setStatus("Active");
		if(accountDao.save(account)==null) throw new BankingServicesDownException();
		transactionDao.save(new Transaction(account.getAccountBalance(), "Credited", account));
		return account;
	}

	@Override
	public float depositAmount(long accountNo, float amount)
			throws AccountNotFoundException, BankingServicesDownException, AccountBlockedException {
		Account account= accountDao.findOne(accountNo);
		if(account== null) throw new AccountNotFoundException("Please Enter Valid Acc Number.");
		if(!account.getStatus().equals("Active")) throw new AccountBlockedException("Your Account Is Currently Blocked.");
		account.setAccountBalance(account.getAccountBalance()+ amount);
		Transaction transaction= new Transaction(amount, "Credited", account);
		transactionDao.save(transaction);
		accountDao.update(account);		
		return account.getAccountBalance();
	}

	@Override
	public float withdrawAmount(long accountNo, float amount, int pinNumber) throws InsufficientAmountException,
	AccountNotFoundException, InvalidPinNumberException, BankingServicesDownException, AccountBlockedException {
		Account account= accountDao.findOne(accountNo);
		if(accountDao.findOne(accountNo)== null) throw new AccountNotFoundException("Please Enter Valid Acc Number.");
		if(!accountDao.findOne(accountNo).getStatus().equals("Active")) throw new AccountBlockedException("Your Account Is Currently Blocked.");
		if(pinNumber!= account.getPinNumber()) throw new InvalidPinNumberException();
		if(account.getAccountBalance()< amount) throw new InsufficientAmountException();
		account.setAccountBalance(account.getAccountBalance()-amount);
		accountDao.update(account);
		Transaction transaction= new Transaction(amount, "Debited", account);
		transactionDao.save(transaction);
		return account.getAccountBalance();
	}

	@Override
	public boolean fundTransfer(long accountNoTo, long accountNoFrom, float transferAmount, int pinNumber)
			throws InsufficientAmountException, AccountNotFoundException, InvalidPinNumberException,
			BankingServicesDownException, AccountBlockedException {
		Account payer= accountDao.findOne(accountNoFrom);
		Account payee= accountDao.findOne(accountNoTo);
		if(payer== null || payee== null) throw new AccountNotFoundException();
		if(pinNumber!= payer.getPinNumber()) throw new InvalidPinNumberException();
		if(!payer.getStatus().equals("Active")|| !payee.getStatus().equals("Active")) throw new AccountBlockedException();
		if(payer.getAccountBalance()< transferAmount) throw new InsufficientAmountException();
		payer.setAccountBalance(payer.getAccountBalance()- transferAmount);
		payee.setAccountBalance(payee.getAccountBalance()+ transferAmount);
		accountDao.update(payer);
		accountDao.update(payee);
		transactionDao.save(new Transaction(transferAmount, "Debited", payer));
		transactionDao.save(new Transaction(transferAmount, "Credited", payee));
		return true;
	}

	@Override
	public Account getAccountDetails(long accountNo) throws AccountNotFoundException, BankingServicesDownException {
		if(accountDao.findOne(accountNo)==null) throw new AccountNotFoundException();
		return accountDao.findOne(accountNo);
	}

	@Override
	public List<Account> getAllAccountDetails() throws BankingServicesDownException {
		return accountDao.findAll();
	}

	@Override
	public List<Transaction> getAccountAllTransaction(long accountNo)
			throws BankingServicesDownException, AccountNotFoundException {
		Account account= accountDao.findOne(accountNo);
		if(account== null) throw new AccountNotFoundException();
		return transactionDao.findAll(accountNo);
	}

	@Override
	public String accountStatus(long accountNo)
			throws BankingServicesDownException, AccountNotFoundException, AccountBlockedException {
		Account account= accountDao.findOne(accountNo);
		if(account== null) throw new AccountNotFoundException();
		if(!account.getStatus().equals("Active")) throw new AccountBlockedException();
		return account.getStatus();
	}

	/*@Override
	public void pdfGenerator(long accountNo) throws FileNotFoundException, DocumentException, BankingServicesDownException, AccountNotFoundException {
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("Transactions.pdf"));
		document.open();
		document.add(new Paragraph("Transactions: "));
		com.itextpdf.text.List orderedList = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);
		document.add(orderedList);
		for(Transaction t: new ArrayList<>(getAccountAllTransaction(accountNo))) {
			orderedList.add(t.toString());
		}
		document.add(orderedList);
		document.close();
		writer.close();
	}*/

}

package com.cg.banking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cg.banking.beans.Account;
import com.cg.banking.exceptions.BankingServicesDownException;
import com.cg.banking.exceptions.InvalidAccountTypeException;
import com.cg.banking.exceptions.InvalidAmountException;
import com.cg.banking.services.BankingServices;

@Controller
public class BankingControllers {
	
	@Autowired
	BankingServices services;

	public BankingServices getServices() {
		return services;
	}
	public void setServices(BankingServices services) {
		this.services = services;
	}
	
	@RequestMapping(value="OpenAccount", method=RequestMethod.GET)
	public String displayOpenAccount(Model model) {
		model.addAttribute("acc", new Account());
		return "OpenAccPage";
	}
	
	@RequestMapping(value="AcceptAccountDetails", method=RequestMethod.POST)
	public String acceptAccountDetails(@ModelAttribute(value="acc")Account account, Model model) throws InvalidAmountException, InvalidAccountTypeException, BankingServicesDownException {
		services.openAccount(account);
		return "AccountOpenSuccess";
	}
	
	
}

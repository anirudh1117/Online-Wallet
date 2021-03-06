package org.com.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import org.com.dao.WalletAccountDao;
import org.com.dao.WalletTransactionDao;
import org.com.dao.WalletUserDao;
import org.com.model.WalletAccount;
import org.com.model.WalletTransaction;
import org.com.model.WalletUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

	@Autowired
	WalletTransactionDao tdao;

	@Autowired
	WalletUserDao udao;

	@Autowired
	WalletAccountDao adao;

	// Add Transaction
	public boolean addTransaction(WalletTransaction wt) {
		Optional<WalletTransaction> find = tdao.findById(wt.getTransactionId());
		if (!find.isPresent()) {
			tdao.save(wt);
			return true;
		} else
			return false;
	}

	// Update Balance on Receiver side
	public boolean updateBalanceAtRecieverEnd(WalletTransaction wt) {
		Optional<WalletAccount> account = adao.findById(wt.getReceiverId());
		if (account.isPresent()) {
			WalletAccount wa = account.get();
			wa.setAccountBalance(wa.getAccountBalance() + wt.getAmount());
			adao.save(wa);
			return true;
		} else
			return false;
	}

	// Update Balance at Sender side
	public boolean updateBalanceAtSenderEnd(WalletTransaction wt) {
		Optional<WalletAccount> account = adao.findById(wt.getSenderId());
		if (account.isPresent()) {
			WalletAccount wa = account.get();
			wa.setAccountBalance(wa.getAccountBalance() - wt.getAmount());
			adao.save(wa);
			return true;
		} else
			return false;
	}

	// Show All Transactions
	public List<WalletTransaction> showAllTransactions() {
		return tdao.findAll();
	}

	// Search Users By Name or Phone Number
	public List<WalletUser> SerachByNameOrPhoneNo(String key, int id) {
		key = key.toLowerCase();
		List<WalletUser> listname = udao.findByName(key);
		List<WalletUser> list = new ArrayList<>();
		Optional<WalletUser> user2 = udao.findById(id);
		for(WalletUser user :listname ) {
			if(user.getUserName()==user2.get().getUserName()&& user.getUserId()==user2.get().getUserId());
			else
			{
				user.setPassword("");
				list.add(user);
			}
		}
		if(!list.isEmpty());
		else {
			List<WalletUser> listall = udao.findAll();
			for(WalletUser user : listall) {
				user.setPassword("");
				String str = new Long(user.getPhoneNumber()).toString();
				if(str.contains(key)&& user.getUserId()!=user2.get().getUserId())
					list.add(user);
			}
		}
		
		return list;
	}

	// Get Balance by UserId
	public double getBalance(int id) {
		Optional<WalletUser> user = udao.findById(id);
		return user.get().getWalletAccount().getAccountBalance();
	}

	// Get Wallet User By Id
	public WalletUser getWalletUser(int userid) {
		Optional<WalletUser> user = udao.findById(userid);
		if (user.isPresent()) {
			user.get().setPassword("");
			return user.get();
		} else
			return null;
	}

}

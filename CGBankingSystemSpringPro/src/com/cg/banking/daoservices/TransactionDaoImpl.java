package com.cg.banking.daoservices;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Repository;
import com.cg.banking.beans.Transaction;

@Repository
@Transactional
public class TransactionDaoImpl implements TransactionDAO{

	@PersistenceContext
	EntityManager entityManager;
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public Transaction save(Transaction transaction) {
		entityManager.persist(transaction);
		return transaction;
	}

	@Override
	public boolean update(Transaction transaction) {
		entityManager.merge(transaction);
		return true;
	}

	@Override
	public Transaction findOne(int transactionId) {
		return entityManager.find(Transaction.class, transactionId);
	}

	@Override
	public List<Transaction> findAll(long accountNo) {
		return entityManager.createQuery("from Transaction t where ACCOUNT_ACCOUNTNO="+accountNo, Transaction.class).getResultList();
	}

}

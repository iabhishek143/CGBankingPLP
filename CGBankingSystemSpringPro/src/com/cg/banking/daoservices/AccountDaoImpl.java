package com.cg.banking.daoservices;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Repository;
import com.cg.banking.beans.Account;

@Repository
@Transactional
public class AccountDaoImpl implements AccountDAO{

	@PersistenceContext
	EntityManager entityManager;
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	public void setEntityManger(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public Account save(Account account) {
		entityManager.persist(account);
		return account;
	}

	@Override
	public boolean update(Account account) {
	entityManager.merge(account);
		return true;
	}

	@Override
	public List<Account> findAll() {
		return entityManager.createQuery("from Account a", Account.class).getResultList();
	}

	@Override
	public Account findOne(long accountNo) {
		return entityManager.find(Account.class, accountNo);
	}

}

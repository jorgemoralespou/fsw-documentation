package org.jboss.switchyard.examples.transactions;

public interface TransactionalTest {
	public String doInOutTransaction(String message);
	
	public void doInOnlyTransaction(String message);
}

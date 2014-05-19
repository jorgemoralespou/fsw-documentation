package org.jboss.switchyard.examples.transactions;

import javax.naming.NamingException;

import org.switchyard.component.bean.Service;

@Service(name="TransactionalTest",value=TransactionalTest.class)
public class TransactionalTestBean implements TransactionalTest {

	@Override
	public String doInOutTransaction(String message) {
		// TODO Auto-generated method stub
		try {
			if (TransactionUtils.getUserTransaction() != null){
				System.out.println("There is an ongoing transaction");
			}
			else{
				System.out.println("There is not transaction");
			}
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "OK";
	}

	@Override
	public void doInOnlyTransaction(String message) {
		// TODO Auto-generated method stub
		try {
			if (TransactionUtils.getUserTransaction() != null){
				System.out.println("There is an ongoing transaction");
			}
			else{
				System.out.println("There is not transaction");
			}
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

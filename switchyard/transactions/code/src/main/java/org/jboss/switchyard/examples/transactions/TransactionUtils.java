package org.jboss.switchyard.examples.transactions;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

public class TransactionUtils {

    public static final UserTransaction getUserTransaction() throws NamingException {
        return (UserTransaction) new InitialContext().lookup("java:jboss/UserTransaction");
    }
    
}

package com.origamisoftware.teach.advanced.services;

import com.origamisoftware.teach.advanced.model.User;
import com.origamisoftware.teach.advanced.util.DatabaseInitializationException;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class DatabaseUserServiceTest extends DatabaseServiceTest{


    private UserService databaseUserService;

    @Before
    public void setUp() throws DatabaseInitializationException {
        super.setUp();
        databaseUserService = ServiceFactory.getUserService();
    }

    @Test
    public void testAddPerson() throws Exception{
        String sam = "Vic";
        User user = new User(sam);
        databaseUserService.addPerson(user);
    }

    @Test(expected = DuplicateUserNameException.class)
    public void testAddPersonDuplicateUser() throws Exception{
        String sam = "Sam";
        User user = new User(sam);
        databaseUserService.addPerson(user);

    }

}

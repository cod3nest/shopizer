package com.salesmanager.shop.store.security;

import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.user.GroupService;
import com.salesmanager.core.business.services.user.PermissionService;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.shop.store.security.user.CustomerDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;


/**
 * @author casams1
 * http://stackoverflow.com/questions/5105776/spring-security-with
 * -custom-user-details
 */
@Slf4j
@Service
class CustomerServicesImpl extends AbstractCustomerServices {

    private final CustomerService customerService;
    private final PermissionService permissionService;
    private final GroupService groupService;

    public CustomerServicesImpl(CustomerService customerService, PermissionService permissionService, GroupService groupService) {
        super(customerService, permissionService, groupService);
        this.customerService = customerService;
        this.permissionService = permissionService;
        this.groupService = groupService;
    }

    @Override
    protected UserDetails userDetails(String userName, Customer customer, Collection<GrantedAuthority> authorities) {

        CustomerDetails authUser = new CustomerDetails(userName, customer.getPassword(), true, true,
                true, true, authorities);

        authUser.setEmail(customer.getEmailAddress());
        authUser.setId(customer.getId());

        return authUser;
    }


}

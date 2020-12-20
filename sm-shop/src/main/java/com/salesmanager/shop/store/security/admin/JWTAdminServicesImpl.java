package com.salesmanager.shop.store.security.admin;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.user.GroupService;
import com.salesmanager.core.business.services.user.PermissionService;
import com.salesmanager.core.business.services.user.UserService;
import com.salesmanager.core.model.common.audit.AuditSection;
import com.salesmanager.core.model.user.Group;
import com.salesmanager.core.model.user.Permission;
import com.salesmanager.core.model.user.User;
import com.salesmanager.shop.admin.security.SecurityDataAccessException;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.store.security.user.JWTUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
class JwtAdminServicesImpl implements UserDetailsService {

    public final static String ROLE_PREFIX = "ROLE_";//Spring Security 4

    private final UserService userService;
    private final PermissionService permissionService;
    private final GroupService groupService;

    private UserDetails userDetails(String userName, User user, Collection<GrantedAuthority> authorities) {

        AuditSection section = null;
        section = user.getAuditSection();
        Date lastModified = null;
        //if(section != null) {//does not represent password change
        //	lastModified = section.getDateModified();
        //}

        return new JWTUser(
                user.getId(),
                userName,
                user.getFirstName(),
                user.getLastName(),
                user.getAdminEmail(),
                user.getAdminPassword(),
                authorities,
                true,
                lastModified
        );
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = null;
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        try {

            LOGGER.debug("Loading user by user id: {}", userName);

            user = userService.getByUserName(userName);

            if (user == null) {
                //return null;
                throw new UsernameNotFoundException("User " + userName + " not found");
            }

            GrantedAuthority role = new SimpleGrantedAuthority(ROLE_PREFIX + Constants.PERMISSION_AUTHENTICATED);//required to login
            authorities.add(role);

            List<Integer> groupsId = new ArrayList<Integer>();
            List<Group> groups = user.getGroups();
            for (Group group : groups) {
                groupsId.add(group.getId());
            }


            if (CollectionUtils.isNotEmpty(groupsId)) {
                List<Permission> permissions = permissionService.getPermissions(groupsId);
                for (Permission permission : permissions) {
                    GrantedAuthority auth = new SimpleGrantedAuthority(permission.getPermissionName());
                    authorities.add(auth);
                }
            }


        } catch (ServiceException e) {
            LOGGER.error("Exception while querrying customer", e);
            throw new SecurityDataAccessException("Cannot authenticate customer", e);
        }

        return userDetails(userName, user, authorities);
    }

}

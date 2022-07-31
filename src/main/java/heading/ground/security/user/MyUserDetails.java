package heading.ground.security.user;

import heading.ground.user.entity.BaseUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;


public class MyUserDetails implements UserDetails {

    private BaseUser user;

    public MyUserDetails(BaseUser user) {
        this.user = user;
    }

    public Long getId(){
        return user.getId();
    }

    public BaseUser getUser() {
        return user;
    }

    public String getRole(){
        return this.user.getRole().toString();
    }

    public String getLoginId(){return this.user.getLoginId();}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole().toString();
            }
        });
        //user.getRole();
        return roles;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getLoginId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isNon_locked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}

package heading.ground.security.user;

import heading.ground.user.entity.BaseUser;
import heading.ground.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public MyUserDetails loadUserByUsername(String username){
        Optional<BaseUser> optional = userRepository.findByLoginId(username);

        optional.orElseThrow(()->new UsernameNotFoundException("Unknown : " + username));

        return optional.map(MyUserDetails::new).get();
    }
}

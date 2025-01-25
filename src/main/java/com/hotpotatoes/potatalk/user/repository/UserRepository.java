package com.hotpotatoes.potatalk.user.repository;

import com.hotpotatoes.potatalk.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);
    Optional<User> findByLoginIdAndEmail(String loginId, String email);
    Optional<User> findByRefreshToken(String refreshToken);  // HEAD에서 추가된 메서드 유지

}

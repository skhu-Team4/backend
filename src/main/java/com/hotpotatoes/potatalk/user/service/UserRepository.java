package com.hotpotatoes.potatalk.user.service;

import com.hotpotatoes.potatalk.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository // 생략 가능, 가독성을 위해 추가 가능
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId); // loginId를 기반으로 사용자 조회

    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);

    Optional<User> findByLoginIdAndEmail(String loginId, String email);


}

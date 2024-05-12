package com.springbatch.springbatch.repository;

import com.springbatch.springbatch.model.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserData,Integer> {
}

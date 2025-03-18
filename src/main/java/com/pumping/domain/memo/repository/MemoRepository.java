package com.pumping.domain.memo.repository;

import com.pumping.domain.memo.model.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoRepository extends JpaRepository<Memo, Long> {
}

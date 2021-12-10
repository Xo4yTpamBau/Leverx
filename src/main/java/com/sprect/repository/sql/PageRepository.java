package com.sprect.repository.sql;

import com.sprect.model.entity.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PageRepository extends JpaRepository<Page, Long> {

    List<Page> findAllByApproved(Boolean approved);
    List<Page> findAllByApprovedOrderByRateDesc(Boolean approved);
}

package com.comin.eapproval.dashboard.repository;

import com.comin.eapproval.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 대시보드 데이터 접근 기능을 제공하는 Repository.
 */
public interface DashboardRepository extends JpaRepository<Document, Long>, DashboardRepositoryCustom {
}
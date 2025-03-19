package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.CIF;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.prj.LoneHPManagement.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CIFRepository extends JpaRepository<CIF, Integer> {
    CIF findByCifCode(String cifCode);
    @Query("SELECT DISTINCT c.email FROM CIF c")
    List<String> findAllUniqueEmails();
    @Query("SELECT DISTINCT c.phoneNumber FROM CIF c")
    List<String> findAllUniquePhoneNumbers();
    CIF findByEmail(String email);

    @Query("SELECT MAX(c.cifCode) FROM CIF c WHERE c.createdUser  = :createdUser ")
    String findMaxCIFCodeByUser (@Param("createdUser") User createdUser );

   // List<CIF> findByCifCodeContainingIgnoreCase(String searchTerm);
    CIF findByPhoneNumber(String phoneNumber);

    Page<CIF> findAll(Pageable pageable);

    List<CIF> findByCifCodeContainingIgnoreCase(String cifCode);

    // For RegularBranchLevel: search CIF records where cifCode starts with branchCode
    // and also contains the searchTerm (case‑insensitive)
    @Query("SELECT c FROM CIF c " +
            "WHERE c.cifCode LIKE CONCAT(:branchCode, '%') " +
            "AND LOWER(c.cifCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<CIF> findByBranchCodeAndCifCodeContainingIgnoreCase(@Param("branchCode") String branchCode,
                                                             @Param("searchTerm") String searchTerm);
    @Query("SELECT c FROM CIF c WHERE c.cifCode LIKE CONCAT(:branchCode, '%')")
    Page<CIF> findByBranchCode(String branchCode, Pageable pageable);
    @Query("SELECT c FROM CIF c WHERE c.cifCode LIKE CONCAT(:branchCode, '%')")
    List<CIF> findByBranchCOde(String branchCode);

    @Query("SELECT c FROM CIF c WHERE c.address.state = :state")
    Page<CIF> findByState(String state, Pageable pageable);
}

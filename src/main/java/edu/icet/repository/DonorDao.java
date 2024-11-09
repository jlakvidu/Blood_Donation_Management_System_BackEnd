package edu.icet.repository;

import edu.icet.dto.DonorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonorDao extends JpaRepository<DonorEntity,Integer> {
    DonorEntity findByEmailAddress(String emailAddress);
    DonorEntity findByName(String name);
    void deleteByContactNumber(String contactNumber);
    List<DonorEntity> findByBloodTypeAndDistrict(String bloodType, String district);
    boolean existsByEmailAddress(String emailAddress);
    List<DonorEntity> findByDistrict(String district);
}

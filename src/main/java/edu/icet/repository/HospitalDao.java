package edu.icet.repository;

import edu.icet.entity.HospitalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HospitalDao extends JpaRepository<HospitalEntity,Long> {
    HospitalEntity findByName(String name);
    HospitalEntity findByEmailAddress(String emailAddress);
    List<HospitalEntity> findByDistrict(String district);
    boolean existsByEmailAddress(String emailAddress);

}

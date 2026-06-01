package com.garageautobot.garagemautobot.repositories;

import com.garageautobot.garagemautobot.entities.ItemOS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemOSRepository extends JpaRepository<ItemOS, Long> {
    List<ItemOS> findByOrdemServicoId(Long osId);
}
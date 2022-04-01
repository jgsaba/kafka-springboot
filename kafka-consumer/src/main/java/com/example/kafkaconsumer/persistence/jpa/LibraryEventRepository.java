package com.example.kafkaconsumer.persistence.jpa;

import com.example.kafkaconsumer.persistence.entities.LibraryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryEventRepository extends JpaRepository<LibraryEvent, Long> {


}

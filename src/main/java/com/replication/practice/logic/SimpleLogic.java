package com.replication.practice.logic;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.replication.practice.logic.entity.SimpleEntity;
import com.replication.practice.logic.repository.SimpleJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SimpleLogic {

	private final SimpleJpaRepository simpleJpaRepository;

	@Transactional
	public void insertLogic() {
		log.info("insertLogic Start May be Master DB Connection");
		SimpleEntity simpleEntity = new SimpleEntity("test");
		simpleJpaRepository.save(simpleEntity);
		log.info("insertLogic End");
	}

	@Transactional(readOnly = true)
	public void selectLogic() {
		log.info("selectLogic Start May be Slave DB Connection");
		simpleJpaRepository.findAll();
		log.info("selectLogic End");
	}
}

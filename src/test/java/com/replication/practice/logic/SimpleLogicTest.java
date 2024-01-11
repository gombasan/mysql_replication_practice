package com.replication.practice.logic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SimpleLogicTest {

	@Autowired
	private SimpleLogic simpleLogic;

	@Test
	void insertLogic_test() {
		simpleLogic.insertLogic();
	}

	@Test
	void selectLogic_test() {
		simpleLogic.selectLogic();
	}
}

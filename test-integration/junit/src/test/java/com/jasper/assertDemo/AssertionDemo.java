package com.jasper.assertDemo;

import com.jasper.Calculator;
import com.jasper.pojo.entity.Person;
import org.junit.jupiter.api.Test;

import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.*;

/**
 * see  org.junit.jupiter.api.Assertions
 */
@SuppressWarnings("all")
class AssertionDemo {
    private final Calculator calculator = new Calculator();

    Person person = new Person(1, "jasper", 20, "shannxi");

    @Test
    void standardAssertions() {
        assertEquals(2, calculator.add(1, 1));
        assertEquals(4, calculator.multiply(2, 2), "failure message");

        // Lazily evaluates 延迟加载写法：只有在断言失败时才会去执行生成消息的逻辑
        assertTrue('a' < 'b', () -> "failure message");
    }

    /**
     * 一旦某个断言失败，测试就会立即中断并抛出异常，后面的断言无法执行
     * 它接受一个标题（如 "person"）和多个可执行的断言（通过 Lambda 表达式传入）。无论前面的断言是否成功，所有的 Lambda 都会被执行
     */
    @Test
    void groupedAssertions() {
        assertAll("person",
                () -> assertEquals("jasper", person.getName()),
                () -> assertEquals(20, person.getAge())
        );
    }

    @Test
    void dependentAssertions() {
//		 第一个代码块失败了 第二个还是会执行
        assertAll("properties",
                () -> {
                    int id = person.getId();
                    assertNotNull(id);
//					 Executed only if the previous assertion is valid
                    assertAll("person",
                            () -> assertTrue(person.getName().startsWith("j")),
                            () -> {
                                assertTrue(person.getAge() > 15);
                            }
                    );
                },
                () -> {
                    int id = person.getId();
                    assertNotNull(id);
                    assertAll("person1",
                            () -> assertTrue(person.getCity().startsWith("s")),
                            () -> {
                                assertTrue(person.getAge() > 15);
                            }
                    );
                }
        );
    }

	@Test
	void exceptionTesting() {
		Exception exception = assertThrows(ArithmeticException.class, () ->
				calculator.divide(1, 0));
		System.out.println("exception = " + exception);
		assertEquals("/ by zero", exception.getMessage());
	}

	@Test
	void timeoutNotExceeded() {
		// 断言：执行这段代码的时间应该在 2 秒以内
		assertTimeout(ofSeconds(2), () -> {
			// 模拟一个耗时 1 秒的任务（未超时，测试通过）
			Thread.sleep(1000);
		});
	}

	@Test
	void timeoutNotExceededWithResult() {
		// assertTimeout 还可以接收一个有返回值的代码块，并把结果返回
		String result = assertTimeout(ofSeconds(1), () -> {
			// 执行某些耗时较短的业务逻辑
			Thread.sleep(500);
			return "Hello JUnit 5";
		});

		// 验证返回的结果
		assertEquals("Hello JUnit 5", result);
	}

	@Test
	void timeoutExceededExample() {
		// 失败！因为任务需要 1.5 秒，但限制时间只有 1 秒
		assertTimeout(ofSeconds(1), () -> {
			Thread.sleep(1500);
		});
	}
	private String fetchDataFromServer() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return "Hello Timeout";
	}

	@Test
	void timeoutNotExceededWithReturnValueMethod() {
		// 如果被调用的方法有返回值，assertTimeout 同样可以正常接收并返回结果
		String greeting = assertTimeout(ofSeconds(1), this::fetchDataFromServer);

		assertEquals("Hello Timeout", greeting);
	}
}

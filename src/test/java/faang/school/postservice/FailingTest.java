package faang.school.postservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class FailingTest {

    @Test
    public void testShouldFail() { fail("this test is designed to fail"); }
}

package dmax.plua.domain;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Maxim Dybarsky | maxim.dybarskyy@gmail.com
 * on 15.12.14 at 15:55
 */
public class LinkEqualsTest {

    @Test
    public void test_shouldBeNotEqual_IfIdsDifferent() {
        Link l1 = new Link();
        l1.setId(1);
        Link l2 = new Link();
        l2.setId(2);

        assertFalse(l1.equals(l2));
    }

    @Test
    public void test_shouldBeNotEqual_IfIdsSameAndDataDifferent() {
        Link l1 = new Link();
        l1.setId(1);
        l1.setWordId(Language.POLISH, 1);
        l1.setWordId(Language.UKRAINIAN, 2);
        Link l2 = new Link();
        l2.setId(1);
        l2.setWordId(Language.POLISH, 3);
        l2.setWordId(Language.UKRAINIAN, 4);

        assertFalse(l1.equals(l2));
    }

    @Test
    public void test_shouldBeNotEqual_IfIdsNotSetAndDataDifferent() {
        Link l1 = new Link();
        l1.setWordId(Language.POLISH, 1);
        l1.setWordId(Language.UKRAINIAN, 2);
        Link l2 = new Link();
        l2.setWordId(Language.POLISH, 3);
        l2.setWordId(Language.UKRAINIAN, 4);

        assertFalse(l1.equals(l2));
    }

    @Test
    public void test_shouldBeNotEqual_IfOneIdNotSetAndDataDifferent() {
        Link l1 = new Link();
        l1.setId(1);
        l1.setWordId(Language.POLISH, 1);
        l1.setWordId(Language.UKRAINIAN, 2);
        Link l2 = new Link();
        l2.setWordId(Language.POLISH, 3);
        l2.setWordId(Language.UKRAINIAN, 4);

        assertFalse(l1.equals(l2));
    }

    @Test
    public void test_shouldBeEqual_IfOneIdNotSetAndDataSame() {
        Link l1 = new Link();
        l1.setId(9);
        l1.setWordId(Language.POLISH, 1);
        l1.setWordId(Language.UKRAINIAN, 2);
        Link l2 = new Link();
        l2.setWordId(Language.POLISH, 1);
        l2.setWordId(Language.UKRAINIAN, 2);

        assertTrue(l1.equals(l2));
    }

    @Test
    public void test_shouldBeEqual_IfIdsNotSetAndDataSame() {
        Link l1 = new Link();
        l1.setWordId(Language.POLISH, 1);
        l1.setWordId(Language.UKRAINIAN, 2);
        Link l2 = new Link();
        l2.setWordId(Language.POLISH, 1);
        l2.setWordId(Language.UKRAINIAN, 2);

        assertTrue(l1.equals(l2));
    }

    @Test
    public void test_shouldBeEqual_IfIdsSameAndDataSame() {
        Link l1 = new Link();
        l1.setId(1);
        l1.setWordId(Language.POLISH, 1);
        l1.setWordId(Language.UKRAINIAN, 2);
        Link l2 = new Link();
        l2.setId(1);
        l2.setWordId(Language.POLISH, 1);
        l2.setWordId(Language.UKRAINIAN, 2);

        assertTrue(l1.equals(l2));
    }

    @Test
    public void test_shouldBeEqual_IfNoWordsAndPriorityUpdateSame() {
        Link l1 = new Link();
        l1.setId(1);
        l1.setPriority(1);
        l1.setUpdated(2);
        Link l2 = new Link();
        l2.setId(1);
        l2.setPriority(1);
        l2.setUpdated(2);

        assertTrue(l1.equals(l2));
    }

    @Test
    public void test_shouldBeNotEqual_IfNoWordsAndPriorityDifferent() {
        Link l1 = new Link();
        l1.setId(1);
        l1.setPriority(1);
        l1.setUpdated(2);
        Link l2 = new Link();
        l2.setId(1);
        l2.setPriority(3);
        l2.setUpdated(2);

        assertFalse(l1.equals(l2));
    }

    @Test
    public void test_shouldBeNotEqual_IfNoWordsAndUpdatedDifferent() {
        Link l1 = new Link();
        l1.setId(1);
        l1.setPriority(1);
        l1.setUpdated(2);
        Link l2 = new Link();
        l2.setId(1);
        l2.setPriority(1);
        l2.setUpdated(4);

        assertFalse(l1.equals(l2));
    }
}

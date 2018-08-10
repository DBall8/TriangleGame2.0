import physics.Line;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LineTest {

    @Test
    public void lineIntersectTest(){
        Line l1 = new Line(0, 0, 20, 0);
        Line l2 = new Line(10, 10, 10, -10);

        assertEquals(true, l1.intersects(l2));
        assertEquals(true, l2.intersects(l1));

        l1 = new Line(0, 0, 20, 20);
        l2 = new Line(0, 20, 20, 0);
        assertEquals(true, l1.intersects(l2));
        assertEquals(true, l2.intersects(l1));

        l1 = new Line(0, 0, 20, 20);
        l2 = new Line(0, 0, 20, 20);
        assertEquals(true, l1.intersects(l2));
        assertEquals(true, l2.intersects(l1));

        l1 = new Line(0, 0, 20, 20);
        l2 = new Line(20, 20, 20, 20);
        assertEquals(true, l1.intersects(l2));
        assertEquals(true, l2.intersects(l1));

        l1 = new Line(0, 0, 20, 20);
        l2 = new Line(20, 20, 40, 40);
        assertEquals(true, l1.intersects(l2));
        assertEquals(true, l2.intersects(l1));

        l1 = new Line(10, 10, 20, 20);
        l2 = new Line(0, 0, 40, 40);
        assertEquals(true, l1.intersects(l2));
        assertEquals(true, l2.intersects(l1));

        l1 = new Line(0, 0, 20, 20);
        l2 = new Line(30, 30, 40, 40);
        assertEquals(false, l1.intersects(l2));
        assertEquals(false, l2.intersects(l1));

        l1 = new Line(0, 0, 20, 20);
        l2 = new Line(10, 11, 0, 40);
        assertEquals(false, l1.intersects(l2));
        assertEquals(false, l2.intersects(l1));

    }

}

package pt.feup.tvvs.soulknight;

import com.googlecode.lanterna.TextColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.controller.menu.ParticleMenuController;
import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.menu.Menu;
import pt.feup.tvvs.soulknight.model.menu.Particle;
import pt.feup.tvvs.soulknight.state.particle.*;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ParticleMenuControllerWhiteBoxTests {

    private Particle particle;
    private ParticleMenuController controller;

    @BeforeEach
    void setUp() {
        Menu menu = mock(Menu.class);
        particle = mock(Particle.class);

        when(menu.getParticles()).thenReturn(List.of(particle));

        pt.feup.tvvs.soulknight.state.particle.ParticleState fakeState =
                mock(pt.feup.tvvs.soulknight.state.particle.ParticleState.class);

        when(particle.getState()).thenReturn(fakeState);
        when(fakeState.move(any(Particle.class), anyLong(), any(ParticleMenuController.class)))
                .thenReturn(new Position(10, 10));

        controller = new ParticleMenuController(menu);
    }


    @Test
    public void move_mode0_setsRandomState() throws Exception {
        controller.move(mock(pt.feup.tvvs.soulknight.Game.class), GUI.ACTION.NULL, 0); // (0/50)%5 = 0
        verify(particle).setState(isA(RandomState.class));
        verify(particle).setPosition(new Position(10, 10));
    }

    @Test
    public void move_mode1_setsWindyState() throws Exception {
        controller.move(mock(pt.feup.tvvs.soulknight.Game.class), GUI.ACTION.NULL, 50); // 1
        verify(particle).setState(isA(WindyState.class));
    }

    @Test
    public void move_mode2_setsCalmState() throws Exception {
        controller.move(mock(pt.feup.tvvs.soulknight.Game.class), GUI.ACTION.NULL, 100); // 2
        verify(particle).setState(isA(CalmState.class));
    }

    @Test
    public void move_mode3_setsDispersingState() throws Exception {
        controller.move(mock(pt.feup.tvvs.soulknight.Game.class), GUI.ACTION.NULL, 150); // 3
        verify(particle).setState(isA(DispersingState.class));
    }

    @Test
    public void move_mode4_setsZicoState() throws Exception {
        controller.move(mock(pt.feup.tvvs.soulknight.Game.class), GUI.ACTION.NULL, 200); // 4
        verify(particle).setState(isA(ZicoState.class));
    }

    @Test
    public void move_negativeTime_hitsDefaultBranch_andThrows() {
        // time=-50 => time/modeDuration=-1; -1%5=-1 => default
        assertThrows(IllegalStateException.class, () ->
                controller.move(mock(pt.feup.tvvs.soulknight.Game.class), GUI.ACTION.NULL, -50)
        );
    }

    @Test
    public void gradients_timeMultipleOf500_startsTransition_setsTransitionStartTick_andChangesNextColors() throws Exception {
        setPrivate(controller, "transitionStartTick", -1);

        TextColor.RGB oldNextStart = (TextColor.RGB) getPrivate(controller, "nextStartColor");
        TextColor.RGB oldNextEnd = (TextColor.RGB) getPrivate(controller, "nextEndColor");

        controller.move(mock(pt.feup.tvvs.soulknight.Game.class), GUI.ACTION.NULL, 500); // dispara start

        int startTick = (int) getPrivate(controller, "transitionStartTick");
        assertEquals(500, startTick);

        TextColor.RGB newNextStart = (TextColor.RGB) getPrivate(controller, "nextStartColor");
        TextColor.RGB newNextEnd = (TextColor.RGB) getPrivate(controller, "nextEndColor");

        assertNotNull(newNextStart);
        assertNotNull(newNextEnd);
        assertNotNull(oldNextStart);
        assertNotNull(oldNextEnd);
    }

    @Test
    public void gradients_duringTransition_interpolatesBranch_executes_andKeepsTransitionActive() throws Exception {
        setPrivate(controller, "transitionStartTick", 0);

        TextColor.RGB cs = new TextColor.RGB(0, 0, 0);
        TextColor.RGB ce = new TextColor.RGB(0, 0, 0);
        TextColor.RGB ns = new TextColor.RGB(100, 100, 100);
        TextColor.RGB ne = new TextColor.RGB(200, 200, 200);

        setPrivate(controller, "currentStartColor", cs);
        setPrivate(controller, "currentEndColor", ce);
        setPrivate(controller, "nextStartColor", ns);
        setPrivate(controller, "nextEndColor", ne);

        controller.move(mock(pt.feup.tvvs.soulknight.Game.class), GUI.ACTION.NULL, 50); // 0 <= 50 < 100

        assertEquals(0, (int) getPrivate(controller, "transitionStartTick"));

        TextColor.RGB afterStart = (TextColor.RGB) getPrivate(controller, "currentStartColor");
        TextColor.RGB afterEnd = (TextColor.RGB) getPrivate(controller, "currentEndColor");

        assertNotEquals(0, afterStart.getRed());
        assertNotEquals(0, afterEnd.getRed());
    }

    @Test
    public void gradients_afterTransition_finalizesBranch_setsCurrentToNext_andResetsTransitionStartTick() throws Exception {
        setPrivate(controller, "transitionStartTick", 0);

        TextColor.RGB ns = new TextColor.RGB(10, 20, 30);
        TextColor.RGB ne = new TextColor.RGB(40, 50, 60);
        setPrivate(controller, "nextStartColor", ns);
        setPrivate(controller, "nextEndColor", ne);

        controller.move(mock(pt.feup.tvvs.soulknight.Game.class), GUI.ACTION.NULL, 150); // >= 100

        assertEquals(-1, (int) getPrivate(controller, "transitionStartTick"));

        TextColor.RGB cs = (TextColor.RGB) getPrivate(controller, "currentStartColor");
        TextColor.RGB ce = (TextColor.RGB) getPrivate(controller, "currentEndColor");
        assertEquals(10, cs.getRed());
        assertEquals(20, cs.getGreen());
        assertEquals(30, cs.getBlue());
        assertEquals(40, ce.getRed());
        assertEquals(50, ce.getGreen());
        assertEquals(60, ce.getBlue());
    }


    @Test
    public void wrapPosition_wrapsWhenOutsideBounds() {
        Position p = controller.wrapPosition(-5, 999); // x<0 => 219; y>=110 => 1
        assertEquals(219, p.x());
        assertEquals(1, p.y());
    }

    private static Object getPrivate(Object obj, String fieldName) throws Exception {
        Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(obj);
    }

    private static void setPrivate(Object obj, String fieldName, Object value) throws Exception {
        Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(obj, value);
    }
}

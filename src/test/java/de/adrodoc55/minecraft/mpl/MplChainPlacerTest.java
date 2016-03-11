package de.adrodoc55.minecraft.mpl;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import de.adrodoc55.minecraft.mpl.commands.Command;
import de.adrodoc55.minecraft.mpl.compilation.MplChainPlacer;

public class MplChainPlacerTest extends MplTestBase {

  @Test
  public void getLongestSuccessiveConditionalCount_throws_NullPointerException() {
    try {
      // when:
      MplChainPlacer.getLongestSuccessiveConditionalCount(null);
      // then:
    } catch (NullPointerException ex) {
      assertThat(ex.getMessage()).isEqualTo("chainParts == null!");
    }
  }

  @Test
  public void getLongestSuccessiveConditionalCount_is_0_for_empty_list() {
    // when:
    int result = MplChainPlacer.getLongestSuccessiveConditionalCount(new LinkedList<>());
    // then:
    assertThat(result).isZero();
  }

  @Test
  public void getLongestSuccessiveConditionalCount_is_0_for_multiple_unconditionals() {
    // given:
    List<Command> list = listOf(Command().withConditional(false));
    // when:
    int result = MplChainPlacer.getLongestSuccessiveConditionalCount(list);
    // then:
    assertThat(result).isZero();
  }

  @Test
  public void getLongestSuccessiveConditionalCount_is_1_for_one_conditional() {
    // given:
    Command conditional = new Command("test", true);
    LinkedList<Command> list = new LinkedList<>();
    list.add(conditional);
    // when:
    int result = MplChainPlacer.getLongestSuccessiveConditionalCount(list);
    // then:
    assertThat(result).isEqualTo(1);
  }

  @Test
  public void getLongestSuccessiveConditionalCount_is_n_for_n_conditionals() {
    // given:
    List<Command> list = listOf(Command().withConditional(true));
    // when:
    int result = MplChainPlacer.getLongestSuccessiveConditionalCount(list);
    // then:
    assertThat(result).isEqualTo(list.size());
  }

  @Test
  public void getLongestSuccessiveConditionalCount_is_max_of_m_and_n_for_m_plus_n_conditionals() {
    // given:
    List<Command> conditionals1 = listOf(Command().withConditional(true));
    List<Command> unconditionals = listOf(someInt(1, 100), Command().withConditional(false));
    List<Command> conditionals2 = listOf(Command().withConditional(true));

    List<Command> list = new LinkedList<>();
    list.addAll(conditionals1);
    list.addAll(unconditionals);
    list.addAll(conditionals2);
    // when:
    int result = MplChainPlacer.getLongestSuccessiveConditionalCount(list);
    // then:
    assertThat(result).isEqualTo(Math.max(conditionals1.size(), conditionals2.size()));
  }
}
